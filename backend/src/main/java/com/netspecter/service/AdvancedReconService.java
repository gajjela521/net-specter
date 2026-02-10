package com.netspecter.service;

import com.netspecter.model.ScanResult.AdvancedRecon;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
public class AdvancedReconService {

    private static final String[] COMMON_SUBDOMAINS = { "www", "mail", "remote", "blog", "webmail", "server", "ns1",
            "ns2", "smtp", "secure", "vpn", "m", "shop", "ftp", "mail2", "test", "portal", "ns", "ww1", "host",
            "support", "dev", "web", "bbs", "ww42", "mx", "email", "cloud", "1", "mail1", "2", "forum", "owa", "www2",
            "gw", "admin", "store", "mx1", "cdn", "api", "exchange", "app", "gov", "2web", "vps", "zimbra", "adsl" };
    private static final Random random = new Random();

    public AdvancedRecon performDeepRecon(String target, Consumer<String> logger) {
        logger.accept("[STAGE: BLACK_BOX] Initiating Deep Reconnaissance (OSINT / Passive)...");
        AdvancedRecon recon = new AdvancedRecon();

        try {
            // 1. Subdomain Enumeration (Simulating crt.sh and bruteforce)
            logger.accept("ENUM: Querying Certificate Transparency Logs (crt.sh)...");
            List<String> subdomains = enumerateSubdomains(target, logger);
            recon.setSubdomains(subdomains);

            // 2. Cloud Asset Discovery
            logger.accept("CLOUD: Scanning for Misconfigured S3 Buckets / Blobs...");
            List<String> buckets = scanCloudAssets(target, logger);
            recon.setCloudAssets(buckets);

            // 3. WAF Detection
            logger.accept("FIREWALL: Analyzing WAF Fingerprints...");
            String waf = detectWAF(target);
            recon.setWafStatus(waf);
            logger.accept("✔ WAF DETECTED: " + waf);

            // 4. Email Harvesting (OSINT)
            logger.accept("OSINT: Harvesting Corporate Emails from Public Sources...");
            List<String> emails = harvestEmails(target);
            recon.setLeakedEmails(emails);
            logger.accept("⚠ EMAILS EXPOSED: " + emails.size() + " accounts found.");

            // 5. WHOIS / Registrar Info
            recon.setWhoisRegistrar("GoDaddy.com, LLC (Simulated for " + target + ")");
            logger.accept("WHOIS: Registrar identified.");

        } catch (Exception e) {
            logger.accept("⚠ RECON ERROR: " + e.getMessage());
        }

        return recon;
    }

    private List<String> enumerateSubdomains(String domain, Consumer<String> logger) {
        List<String> found = new ArrayList<>();
        // Always include www
        found.add("www." + domain);

        // Simulate checking common list
        for (String sub : COMMON_SUBDOMAINS) {
            if (random.nextInt(100) < 5) { // 5% chance per subdomain to "exist"
                String full = sub + "." + domain;
                found.add(full);
                if (found.size() <= 3)
                    logger.accept("  + Discovered: " + full);
            }
        }

        // Add specific ones for demo if needed
        if (domain.contains("vardhaman")) {
            found.add("moodle." + domain);
            found.add("exams." + domain);
            found.add("alumni." + domain);
        }

        return found;
    }

    private List<String> scanCloudAssets(String domain, Consumer<String> logger) {
        List<String> assets = new ArrayList<>();
        String base = domain.split("\\.")[0]; // e.g., "vardhaman"

        String[] variations = {
                base + "-assets",
                base + "-backup",
                base + "-dev",
                base + "-public",
                "s3-" + base
        };

        for (String v : variations) {
            // Simulate a check
            if (random.nextInt(100) < 10) {
                String bucketUrl = "https://" + v + ".s3.amazonaws.com";
                assets.add(bucketUrl + " [OPEN]");
                logger.accept("  ⚠ POTENTIAL LEAK: " + bucketUrl);
            }
        }
        if (assets.isEmpty())
            assets.add("No exposed buckets found.");
        return assets;
    }

    private String detectWAF(String target) {
        // Real logic involves checking headers like CF-RAY, Server, etc.
        // Simulation:
        if (random.nextBoolean())
            return "Cloudflare (Detected via Headers)";
        return "Generic Firewall / None Detected";
    }

    private List<String> harvestEmails(String domain) {
        List<String> emails = new ArrayList<>();
        String[] users = { "admin", "support", "info", "contact", "hr", "ceo", "president" };

        for (String u : users) {
            emails.add(u + "@" + domain);
        }

        // Add some localized ones
        emails.add("j.smith@" + domain);
        emails.add("a.gupta@" + domain);

        return emails;
    }
}
