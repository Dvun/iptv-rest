package com.iptvrest.utils;

import com.iptvrest.entity.IpBlockWhoIs;
import com.iptvrest.repository.IpBlockWhoIsRepository;
import com.iptvrest.service.OptimBotService;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Configuration
public class IpBlockWhoIsChecker {

    @Autowired
    private IpBlockWhoIsRepository ipBlockWhoIsRepository;
    private OptimBotService optimBotService;
    private String providerCode = "";

    public IpBlockWhoIsChecker() {
        checkIpBlock();
    }


    private void checkIpBlock() {
        List<IpBlockWhoIs> ipBlockWhoIsList = ipBlockWhoIsRepository.findAll();
        for (IpBlockWhoIs block:ipBlockWhoIsList) {
            IPAddress subnetAddress = new IPAddressString(block.getIpBlock()).getAddress();
            IPAddress subnet = subnetAddress.toPrefixBlock();
            IPAddress testAddress = new IPAddressString(optimBotService.getIpAddress()).getAddress();
            if (subnet.contains(testAddress)) {
                providerCode = block.getProviderCode();
                break;
            }
        }
    }
}
