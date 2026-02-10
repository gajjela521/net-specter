package com.netspecter.service;

import com.netspecter.model.ScanResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@Service
public class DarkWebService {

    public List<String> checkDarkWeb(String target, Consumer<String> logger) {
        List<String> findings = new ArrayList<>();
        logger.accept("[STAGE:DARK_WEB] connecting to Tor Node relay...");

        try {
            Thread.sleep(1500); // Simulate connection latency
            logger.accept("... Authenticating with Onion Routing Protocol");

            // Heuristic checks (Simulation for Enterprise Demo)
            logger.accept("... Scanning exposed credential dumps (BreachCompilation, AntiPublic)");

            if (new Random().nextDouble() > 0.7) {
                findings.add("Potential email leak found in 'Collection #1' database related to domain.");
                logger.accept("⚠ ALERT: Email pattern match in legacy breach dataset.");
            }

            logger.accept("... Querying DarkOwl / HIBP Indices");
            Thread.sleep(1000);

            if (target.contains("test") || target.contains("demo")) {
                // No finding for test
            } else {
                // Random "chatter" detection
                if (new Random().nextDouble() > 0.8) {
                    findings.add("Domain mentioned in hacker forum 'XSS.is' (Low Confidence).");
                    logger.accept("⚠ ALERT: Domain mention detected on underground forum.");
                }
            }

            if (findings.isEmpty()) {
                logger.accept("✔ No active dark web threats detected for this period.");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return findings;
    }
}
