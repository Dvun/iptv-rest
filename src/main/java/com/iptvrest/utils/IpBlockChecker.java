package com.iptvrest.utils;

import com.iptvrest.entity.IpBlock;
import com.iptvrest.entity.IpBlockWhoIs;
import com.iptvrest.repository.IpBlockRepository;
import com.iptvrest.repository.IpBlockWhoIsRepository;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import lombok.Getter;

import java.util.List;

@Getter
public class IpBlockWhoIsChecker {

    private IpBlockWhoIsRepository ipBlockWhoIsRepository;
    private IpBlockRepository ipBlockRepository;
    private final String ipAddress;
    private String providerCode;

    public IpBlockWhoIsChecker(String ipAddress, String dbName) {
        this.ipAddress = ipAddress;
        this.providerCode = "";
        if (dbName.equals("ip_block")) {
            checkIpBlock(ipBlockRepository.findAll());
        } else checkIpBlockWhoIs(ipBlockWhoIsRepository.findAll());
    }


    private void checkIpBlock(List<IpBlock> ipBlockList) {
        for (IpBlock block:ipBlockList) {
            IPAddress subnetAddress = new IPAddressString(block.getIpBlock()).getAddress();
            IPAddress subnet = subnetAddress.toPrefixBlock();
            IPAddress testAddress = new IPAddressString(ipAddress).getAddress();
            if (subnet.contains(testAddress)) {
                providerCode = block.getProviderCode();
                break;
            }
        }
    }

    private void checkIpBlockWhoIs(List<IpBlockWhoIs> ipBlockList) {
        for (IpBlockWhoIs block:ipBlockList) {
            IPAddress subnetAddress = new IPAddressString(block.getIpBlock()).getAddress();
            IPAddress subnet = subnetAddress.toPrefixBlock();
            IPAddress testAddress = new IPAddressString(ipAddress).getAddress();
            if (subnet.contains(testAddress)) {
                providerCode = block.getProviderCode();
                break;
            }
        }
    }
}
