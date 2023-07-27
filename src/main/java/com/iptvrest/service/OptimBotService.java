package com.iptvrest.service;

import com.iptvrest.entity.IpBlock;
import com.iptvrest.entity.Provider;
import com.iptvrest.repository.IpBlockRepository;
import com.iptvrest.repository.IpBlockWhoIsRepository;
import com.iptvrest.repository.ProviderRepository;
import com.iptvrest.utils.IpBlockChecker;
import com.iptvrest.utils.IpBlockWhoIsChecker;
import com.iptvrest.utils.ResponseHandler;
import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddressString;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class OptimBotService {

    private final IpBlockRepository ipBlockRepository;
    private final IpBlockWhoIsRepository ipBlockWhoIsRepository;
    private final ProviderRepository providerRepository;
    private String prwdrCode = "";
    private String ipAddress = "";
    public OptimBotService(IpBlockRepository ipBlockRepository, IpBlockWhoIsRepository ipBlockWhoIsRepository, ProviderRepository providerRepository) {
        this.ipBlockRepository = ipBlockRepository;
        this.ipBlockWhoIsRepository = ipBlockWhoIsRepository;
        this.providerRepository = providerRepository;
    }


    public ResponseEntity<Object> checkIpAddress(String ip) {
        ipAddress = ip;
        try {
            IPAddressString str = new IPAddressString(ip);
            boolean isIp = str.toAddress().isIPAddress();

            if (isIp) {
                List<IpBlock> ipBlockList = ipBlockRepository.findAll();
                IpBlockChecker providerCode = new IpBlockChecker(ipBlockList, ip);
                if (providerCode.getProviderCode().isEmpty()) {
                    prwdrCode = providerCode.getProviderCode();
                    return ResponseHandler.response("Override IpBlock in Database?", HttpStatus.OK, null);
                } else {
                    IpBlockWhoIsChecker providerCodeWhoIs = new IpBlockWhoIsChecker();
                }
            }

        } catch (AddressStringException e) {
            return ResponseHandler.response("Bad IP address!", HttpStatus.BAD_REQUEST, null);
        }
        return ResponseHandler.response("", HttpStatus.OK, null);
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
