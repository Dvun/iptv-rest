package com.iptvrest.service;

import com.iptvrest.entity.IpBlock;
import com.iptvrest.entity.Provider;
import com.iptvrest.repository.CodeWhoIsRepository;
import com.iptvrest.repository.IpBlockRepository;
import com.iptvrest.repository.ProviderRepository;
import com.iptvrest.utils.IpBlockChecker;
import com.iptvrest.utils.IpBlockWhoIsChecker;
import com.iptvrest.utils.ResponseHandler;
import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddressString;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class OptimBotService {

    private final IpBlockRepository ipBlockRepository;
    private final ProviderRepository providerRepository;

    @Autowired
    private final IpBlockWhoIsChecker ipBlockWhoIsChecker;
    private String prwdrCode = "";
    public OptimBotService(IpBlockRepository ipBlockRepository, ProviderRepository providerRepository, CodeWhoIsRepository codeWhoIsRepository) {
        this.ipBlockRepository = ipBlockRepository;
        this.providerRepository = providerRepository;
        this.ipBlockWhoIsChecker = new IpBlockWhoIsChecker();
    }


    public ResponseEntity<Object> checkIpAddress(String ip) {
        try {
            IPAddressString str = new IPAddressString(ip);
            boolean isIp = str.toAddress().isIPAddress();

            if (isIp) {
                List<IpBlock> ipBlockList = ipBlockRepository.findAll();
                IpBlockChecker providerCode = new IpBlockChecker(ipBlockList, ip);
                if (!providerCode.getProviderCode().isEmpty()) {
                    prwdrCode = providerCode.getProviderCode();
                    return ResponseHandler.response("Override IpBlock in Database?", HttpStatus.OK, null);
                } else {
                    ipBlockWhoIsChecker.start(ip);
                }
            }

        } catch (AddressStringException e) {
            return ResponseHandler.response("Bad IP address!", HttpStatus.BAD_REQUEST, null);
        }
        return ResponseHandler.response("UdpBlocks, Provider, IpBlocksWhoIs, CodeWhoIs is added to database", HttpStatus.OK, null);
    }


    @Transactional
    public void overrideOrAddIpAddressBlock() {
        Provider provider = providerRepository.findById(prwdrCode).orElse(null);
        if (provider != null) {
            ipBlockRepository.deleteAllByProviderCode(provider.getProviderCode());
            providerRepository.deleteById(provider.getProviderCode());
        } else {

        }

    }


}
