package com.netspecter.controller;

import com.netspecter.service.ServerMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow UI to fetch it
public class ServerStatusController {

    private final ServerMetricsService metricsService;

    @GetMapping
    public ResponseEntity<?> getStatus() {
        return ResponseEntity.ok(Map.of(
                "activeUsers", metricsService.getActiveUserCount(),
                "activeRequests", metricsService.getActiveRequestCount(),
                "totalRequests", metricsService.getTotalRequestCount(),
                "uptime", metricsService.getUptime(),
                "lastUpdated", LocalDateTime.now()));
    }
}
