package com.netspecter.controller;

import com.netspecter.model.NetworkDevice;
import com.netspecter.service.NetworkScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/network")
public class NetworkScanController {

    @Autowired
    private NetworkScannerService networkScannerService;

    @GetMapping("/scan")
    public List<NetworkDevice> scanNetwork() {
        return networkScannerService.scanLocalNetwork();
    }
}
