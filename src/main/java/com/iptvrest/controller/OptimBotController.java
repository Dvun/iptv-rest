package com.iptvrest.controller;

import com.iptvrest.service.OptimBotService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class OptimBotController {


    private final OptimBotService optimBotService;

    @GetMapping("/checkIp/{ipAddress}")
    public ResponseEntity<Object> setIpAddress(@PathVariable String ipAddress) {
        return optimBotService.checkIpAddress(ipAddress);
    }

    @GetMapping("/checkIp/override/{override}")
    public void setIpAddress(@PathVariable boolean override) {
        if (override) optimBotService.overrideOrAddIpAddressBlock();
    }


}
