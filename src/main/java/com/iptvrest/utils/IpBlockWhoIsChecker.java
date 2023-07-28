package com.iptvrest.utils;

import com.iptvrest.entity.CodeWhoIs;
import com.iptvrest.entity.IpBlockWhoIs;
import com.iptvrest.entity.Provider;
import com.iptvrest.entity.UdpBlock;
import com.iptvrest.repository.CodeWhoIsRepository;
import com.iptvrest.repository.IpBlockWhoIsRepository;
import com.iptvrest.repository.ProviderRepository;
import com.iptvrest.repository.UdpBlockRepository;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Service
public class IpBlockWhoIsChecker {

    @Autowired
    private IpBlockWhoIsRepository ipBlockWhoIsRepository;
    @Autowired
    private CodeWhoIsRepository codeWhoIsRepository;
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private UdpBlockRepository udpBlockRepository;

    @Autowired
    private HttpRequest httpRequest;

    private String providerCode = "";
    private String providerName = "No Name";
    private List<String> ipList = new ArrayList<>();
    private List<String> ports = new ArrayList<>();
    private Provider newProvider = new Provider();



    @Transactional
    public void start(String ipAddress) {
        List<IpBlockWhoIs> ipBlockWhoIsList = ipBlockWhoIsRepository.findAll();

        if (ipBlockWhoIsList.isEmpty()) {
            searchInProxyTV(ipAddress);
        }

        for (IpBlockWhoIs block:ipBlockWhoIsList) {
            IPAddress subnetAddress = new IPAddressString(block.getIpBlock()).getAddress();
            IPAddress subnet = subnetAddress.toPrefixBlock();
            IPAddress testAddress = new IPAddressString(ipAddress).getAddress();
            if (subnet.contains(testAddress)) {
                parseProperty(block.getProviderCode());
            } else {
                searchInProxyTV(ipAddress);
            }
        }
    }

    private void parseProperty(String code) {
        List<IpBlockWhoIs> ipBlockWhoIsList = ipBlockWhoIsRepository.findAllByProviderCode(code);
        for (IpBlockWhoIs ipBlock:ipBlockWhoIsList) {
            ipList.add(ipBlock.getIpBlock());
        }
        CodeWhoIs codeWhoIs = codeWhoIsRepository.findByProviderCode(code);
        if (codeWhoIs != null && !codeWhoIs.getProviderName().isEmpty()) providerName = codeWhoIs.getProviderName();
        providerCode = code;
    }

    public void searchInProxyTV(String ip) {
        httpRequest.start(String.format("https://proxytv.ru/proxybot/search.php?ip=%s", ip));
        if (httpRequest.getStatusCode() == 200 && !Objects.equals(httpRequest.getResponse().toString(), "")) {
            providerCode = httpRequest.getResponse().toString();

            CodeWhoIs codeWhoIs = codeWhoIsRepository.findByProviderCode(providerCode);
            if (codeWhoIs != null) {
                codeWhoIsRepository.deleteById(httpRequest.getResponse().toString());
            }

            List<IpBlockWhoIs> ipBlockWhoIsList = ipBlockWhoIsRepository.findAllByProviderCode(providerCode);
            if (!ipBlockWhoIsList.isEmpty()) {
                ipBlockWhoIsRepository.deleteAllByProviderCode(providerCode);
            }

            parseProxyTV(providerCode, ip);
        } else {
            parseWhoIsGlobal(ip);
        }
    }

    private void parseProxyTV(String code, String ip) {
        httpRequest.start(String.format("https://proxytv.ru/proxybot/dlwhois.php?ascode=%s", code));
        if (httpRequest.getStatusCode() == 200) {
            providerCode = httpRequest.getResponse().toString().split(";")[0];
            providerName = httpRequest.getResponse().toString().split(";")[1];
            ipList = List.of(httpRequest.getResponse().toString().split(";")[2].split(","));
            IpBlockWhoIs ipBlockWhoIs = new IpBlockWhoIs();
            for (String item : ipList) {
                ipBlockWhoIs.setIpBlock(item);
                ipBlockWhoIs.setProviderCode(providerCode);
                ipBlockWhoIsRepository.save(ipBlockWhoIs);
            }
            CodeWhoIs codeWhoIs = new CodeWhoIs();
            codeWhoIs.setProviderCode(providerCode);
            codeWhoIs.setProviderName(providerName);
            codeWhoIsRepository.save(codeWhoIs);

            checkProvider(providerCode, ip);
        }
    }

    private void checkProvider(String code, String ip) {
        Provider providerOpened = providerRepository.findByProviderCode(code);
        Provider providerClosed = providerRepository.findByProviderCode("*" + code);
        if (providerOpened != null) {
            providerRepository.delete(providerOpened);
        } else if (providerClosed != null) {
            providerRepository.delete(providerClosed);
        }

        newProvider.setProviderCode(providerCode);
        newProvider.setProviderName(providerName);
        newProvider.setTimeOut(0.25);
        newProvider.setMinCount(10);
        newProvider.setUdpxy(true);
        newProvider.setMsDlt(false);

        runPortScan(ip, 65535);
    }

    private void parseWhoIsGlobal(String ip) {
        httpRequest.start(String.format("https://2ip.ua/ru/services/information-service/provider-ip?a=act&ip=%s", ip));
        if (httpRequest.getStatusCode() == 200) {
            String response = httpRequest.getResponse().toString();
        }
    }

    public void runPortScan(String ip, int nbrPortMaxToScan) {
        AtomicBoolean shouldStop = new AtomicBoolean(false);
        ConcurrentLinkedQueue<Object> openPorts = new ConcurrentLinkedQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        AtomicInteger port = new AtomicInteger(0);
        while (port.get() < nbrPortMaxToScan && !shouldStop.get()) {
            final int currentPort = port.getAndIncrement();
            executorService.submit(() -> {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, currentPort), 200);
                    socket.close();
                    openPorts.add(currentPort);
                    if (currentPort != 80 && currentPort != 53 && currentPort != 21 && currentPort != 443) {
                        String mCast = checkIpAndPortStatus(ip, Integer.toString(currentPort));
                        if (!mCast.isEmpty()) {
                            executorService.shutdownNow();
                            executorService.shutdown();
                            shouldStop.set(true);
                        }
                    }
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<Object> openPortList = new ArrayList<>();
        while (!openPorts.isEmpty()) {
            openPortList.add(openPorts.poll());
        }
    }

    private String checkIpAndPortStatus(String ip, String port) {
        String mCast = "";
        httpRequest.start(String.format("http://%s:%s/status", ip, port));
        if (httpRequest.getStatusCode() == 200 && !httpRequest.getResponse().equals("Error: request timed out")) {
            Document doc = Jsoup.parse(httpRequest.getResponse().toString());
            Element footerDiv = doc.getElementById("footer");
            if (footerDiv != null && footerDiv.text().contains("udpxy")) {
                Elements thElements = doc.select("th:contains(Destination)");
                Element table = thElements.parents().select("table").first();
                Elements trElements = table.select("tr");
                mCast = trElements.get(1).select("td").get(2).text();
            }

            if (!mCast.isEmpty()) {
                newProvider.setMCast(mCast);
                providerRepository.save(newProvider);

                String ipAddress = mCast.split(":")[0];
                String mCastPort = mCast.split(":")[1];
                String[] parts = ipAddress.split("\\.");
                parts[2] = "0";
                parts[3] = "0";
                String modifiedIp = String.join(".", parts);

                UdpBlock udpBlock = new UdpBlock();
                udpBlock.setProviderCode(providerCode);
                udpBlock.setBlock(modifiedIp);
                udpBlock.setPorts(mCastPort);
                udpBlock.setBlockBegin("1");
                udpBlock.setBlockEnd("254");
                udpBlockRepository.save(udpBlock);
            }
        }


        return mCast;
    }
}
