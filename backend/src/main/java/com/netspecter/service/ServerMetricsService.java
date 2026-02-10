package com.netspecter.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ServerMetricsService {

    private final AtomicLong activeRequests = new AtomicLong(0);
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final Map<String, LocalDateTime> activeUsers = new ConcurrentHashMap<>();
    private final LocalDateTime startupTime = LocalDateTime.now();

    public void incrementRequests() {
        activeRequests.incrementAndGet();
        totalRequests.incrementAndGet();
    }

    public void decrementRequests() {
        activeRequests.decrementAndGet();
    }

    public void trackUserActivity(String username) {
        if (username != null && !username.isEmpty()) {
            activeUsers.put(username, LocalDateTime.now());
        }
    }

    public long getActiveRequestCount() {
        return activeRequests.get();
    }

    public long getTotalRequestCount() {
        return totalRequests.get();
    }

    public int getActiveUserCount() {
        // Consider users active if they had activity in the last 5 minutes
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
        activeUsers.entrySet().removeIf(entry -> entry.getValue().isBefore(threshold));
        return activeUsers.size();
    }

    public String getUptime() {
        java.time.Duration duration = java.time.Duration.between(startupTime, LocalDateTime.now());
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%dh %02dm", hours, minutes);
    }
}
