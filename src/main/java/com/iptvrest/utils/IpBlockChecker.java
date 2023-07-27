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
public class IpBlockChecker {

    private final List<IpBlock> ipBlockList;
    private final String ipAddress;
    private String providerCode;

    public IpBlockChecker(List<IpBlock> ipBlockList, String ipAddress) {
        this.ipBlockList = ipBlockList;
        this.ipAddress = ipAddress;
        this.providerCode = "";
        checkIpBlock();
    }


    private void checkIpBlock() {
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
}
