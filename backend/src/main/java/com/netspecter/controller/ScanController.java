package com.netspecter.controller;

import com.netspecter.model.ScanResult;
import com.netspecter.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Type;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/scan")
@CrossOrigin(origins = "*") // Allow all origins for development
public class ScanController {

    @Autowired
    private ScanService scanService;

    // Use a cached thread pool to avoid excessive thread creation if many requests
    // come in
    private final ExecutorService sseExecutor = Executors.newCachedThreadPool();

    @GetMapping
    public ResponseEntity<ScanResult> scanTarget(@RequestParam String target) {
        ScanResult result = scanService.performScan(target);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stream")
    public SseEmitter streamScan(@RequestParam String target) {
        // increased timeout to 10 minutes
        SseEmitter emitter = new SseEmitter(600_000L);

        // Execute scan in a separate thread so we can return the emitter immediately
        // Note: ScanService.performScan is also async but we wrap it here to catch
        // immediate exceptions
        sseExecutor.submit(() -> {
            try {
                scanService.performScan(target,
                        // Logger Consumer
                        (logMessage) -> {
                            try {
                                emitter.send(SseEmitter.event().name("log").data(logMessage));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        // OnComplete Consumer
                        (result) -> {
                            try {
                                emitter.send(SseEmitter.event().name("result").data(result));
                                emitter.complete();
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        });
            } catch (Exception ex) {
                try {
                    emitter.completeWithError(ex);
                } catch (Exception e) {
                    // ignore if already completed
                }
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
            Lookup lookup = new Lookup("google.com", Type.A);
            lookup.run();
            if (lookup.getResult() == Lookup.SUCCESSFUL) {
                return ResponseEntity.ok("DNS Resolution WORKS!");
            } else {
                return ResponseEntity.internalServerError().body("DNS lookup failed: " + lookup.getErrorString());
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("DNS Error: " + e.getMessage());
        }
    }
}
