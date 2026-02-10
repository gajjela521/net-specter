package com.netspecter.controller;

import com.netspecter.model.ScanResult;
import com.netspecter.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scan")
public class ScanController {

    @Autowired
    private ScanService scanService;

    @GetMapping
    public ResponseEntity<ScanResult> scanTarget(@RequestParam String target) {
        ScanResult result = scanService.performScan(target);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stream")
    public org.springframework.web.servlet.mvc.method.annotation.SseEmitter streamScan(@RequestParam String target) {
        org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter = new org.springframework.web.servlet.mvc.method.annotation.SseEmitter(
                600000L); // 10 mins timeout

        java.util.concurrent.ExecutorService sseExecutor = java.util.concurrent.Executors.newSingleThreadExecutor();

        sseExecutor.execute(() -> {
            try {
                scanService.performScan(target,
                        // Logger Consumer
                        (logMessage) -> {
                            try {
                                emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event()
                                        .name("log").data(logMessage));
                            } catch (Exception e) {
                                emitter.completeWithError(e);
                            }
                        },
                        // OnComplete Consumer
                        (result) -> {
                            try {
                                emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event()
                                        .name("result").data(result));
                                emitter.complete();
                            } catch (Exception e) {
                                emitter.completeWithError(e);
                            }
                        });
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });

        return emitter;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Scan Controller is ACTIVE and REACHABLE!");
    }

    @GetMapping("/dns")
    public ResponseEntity<String> checkDns() {
        try {
            org.xbill.DNS.Lookup lookup = new org.xbill.DNS.Lookup("google.com", org.xbill.DNS.Type.A);
            lookup.run();
            if (lookup.getResult() == org.xbill.DNS.Lookup.SUCCESSFUL) {
                return ResponseEntity.ok("DNS Resolution WORKS!");
            } else {
                return ResponseEntity.internalServerError().body("DNS lookup failed: " + lookup.getErrorString());
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("DNS Error: " + e.getMessage());
        }
    }
}
