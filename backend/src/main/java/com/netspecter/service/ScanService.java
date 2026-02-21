package com.netspecter.service;

import com.netspecter.model.ScanResult;
import com.netspecter.model.ScanResult.*; 
import com.netspecter.service.DarkWebService;
import com.netspecter.service.AttackGraphService;
import com.netspecter.service.GeoRouteService;
import com.netspecter.service.AdvancedReconService;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.xbill.DNS.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
public class ScanService {

    @Autowired

    @Autowired
    private AttackGraphService attackGraphService;

    @Autowired
    private GeoRouteService geoRouteService;

    @Autowired
    private AdvancedReconService advancedReconService;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    public ScanResult performScan(Str
        ing input) {
        // Delegate to the streaming version with a no-op logger
        final ScanResult[] resultHolder = new ScanResult[1];
        performScan(input, (msg) -> {}, (res) -> resultHolder[0] = res);
        return resultHolder[0];
    }

    // Enterprise-grade streaming scan
    public void performScan(String input, Consumer<String> logger, Consumer<ScanResult> onComplete) {
        executor.submit(() -> {
            String target = extractTarget(input);
            ScanResult result = new ScanResult();
            result.setTarget(target);
            result.setScanTime(LocalDateTime.now().toString());
            result.setCodename(generateCodename(target));
            
            // Enterprise Feature: Initialize Empty Data Structures
            result.setVulnerabilities(new ArrayList<>());
            result.setIpInfo(new IpInfo());
            result.setDnsInfo(new DnsInfo());
            result.setSslInfo(new SslInfo());
            result.setOpenPorts(new ArrayList<>());
            result.setTechStack(new ArrayList<>());

            logger.accept("INIT: Initializing NetSpecter Security Grid...");
            logger.accept("TARGET: " + target);
            logger.accept("CODENAME: " + result.getCodename());

            try {
                // Phase 1: Network Level (IP & DNS)
                logger.accept("[STAGE:NETWORK] Initiating Network Reconnaissance...");
                InetAddress address = InetAddress.getByName(target);
                IpInfo ipInfo = new IpInfo();
                ipInfo.setIpAddress(address.getHostAddress());
                ipInfo.setHostname(address.getHostName());
                ipInfo.setOrganization(detectOrganization(address)); 
                result.setIpInfo(ipInfo);
                logger.accept("[+] IP RESOLVED: " + address.getHostAddress());

                // DNS Enumeration
                logger.accept("... Enumerating DNS Records");
                DnsInfo dnsInfo = new DnsInfo();
                dnsInfo.setARecords(lookupRecords(target, Type.A));
                dnsInfo.setMxRecords(lookupRecords(target, Type.MX));
                dnsInfo.setTxtRecords(lookupRecords(target, Type.TXT));
                dnsInfo.setServerLocation("Geo-location initiated..."); 
                result.setDnsInfo(dnsInfo);
                logger.accept("[+] DNS MAPPING COMPLETE (" + (dnsInfo.getARecords().size() + dnsInfo.getMxRecords().size()) + " records found)");
                logger.accept("[STAGE:NETWORK] Network Phase Complete.");

                // Phase 2: Application Level (Ports, SSL, Tech)
                logger.accept("[STAGE:APP] Starting Application & Port Analysis...");
                
                // Port Scanning
                List<Integer> commonPorts = List.of(80, 443, 8080, 8443, 21, 22, 25, 3306, 5432);
                List<Integer> openPorts = new ArrayList<>();
                for (int port : commonPorts) {
                    if (isPortOpen(target, port)) {
                        openPorts.add(port);
                        logger.accept("[!] OPEN PORT: " + port);
                    }
                }
                result.setOpenPorts(openPorts);

                // SSL/TLS Operations
                logger.accept("... Analyzing SSL/TLS Configuration");
                SslInfo sslInfo = analyzeSsl(target, logger);
                result.setSslInfo(sslInfo);

                // Tech Stack Detection
                logger.accept("... Fingerprinting Technology Stack");
                List<Vulnerability> vulns = scanHeadersAndTech(target, logger, result);
                logger.accept("[STAGE:APP] Application Phase Complete.");

                // Phase 3: Security & API Level (Headers, Vulns, Threat Score)
                logger.accept("[STAGE:SECURITY] Initiating Security Protocol & API Audit...");
                result.setVulnerabilities(vulns); // Already populated in Phase 2 step but logically belongs here for reporting

                // Threat Calculation
                logger.accept("... Calculating Threat Model");
                calculateThreatScore(result);
                logger.accept("[+] THREAT SCORE: " + result.getThreatScore() + "/100");

                if (result.getThreatScore() > 75) {
                    result.setSummary("CRITICAL THREAT DETECTED. Immediate remediation required.");
                } else if (result.getThreatScore() > 40) {
                     result.setSummary("ELEVATED RISK. Security hardening recommended.");
                } else {
                    result.setSummary("SECURE. System operating within normal parameters.");
                }

                logger.accept("[STAGE:SECURITY] Scan Completed Successfully.");
                
                // Phase 4: Dark Web Intelligence
                List<String> findings = darkWebService.checkDarkWeb(target, logger);
                result.setDarkWebFindings(findings);
                logger.accept("[STAGE:DARK_WEB] Dark Web Analysis Complete.");

                // Phase 5: Attack Graph Visualization
                AttackGraphService.AttackGraph graph = attackGraphService.buildAttackGraph(result, logger);
                result.setAttackGraph(graph);
                logger.accept("[STAGE:VISUAL] Attack Path Map Generated.");

                // Phase 6: Live Geo-Tracking
                List<GeoHop> geoHops = geoRouteService.traceRoute(target, logger);
                result.setGeoTrace(geoHops);
                logger.accept("[STAGE:GEO] Location Trace Complete.");

                // Phase 7: Advanced Reconnaissance (Hacker-Grade Checks)
                ScanResult.AdvancedRecon recon = advancedReconService.performDeepRecon(target, logger);
                result.setAdvancedRecon(recon);
                logger.accept("[STAGE:DEEP] OSINT & Asset Discovery Finished.");
                
            } catch (Throwable e) {
                e.printStackTrace();
                logger.accept("CRITICAL FAILURE: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                result.setSummary("Scan Failed: " + e.getMessage());
                result.setThreatScore(0.0);
            }

            onComplete.accept(result);
        });
    }

    // --- Helper Methods ---

    private String detectOrganization(InetAddress address) {
        // Simulating enterprise GeoIP/ASN lookup
        return "Unknown (Requires GeoIP License)";
    }

    private boolean isPortOpen(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 200); // Fast timeout
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private SslInfo analyzeSsl(String target, Consumer<String> logger) {
        SslInfo info = new SslInfo();
        info.setValid(false);
        try {
             TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            
            URL url = new URL("https://" + target);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.connect();
            
            java.security.cert.Certificate[] certs = conn.getServerCertificates();
            if (certs.length > 0 && certs[0] instanceof X509Certificate) {
                X509Certificate x509 = (X509Certificate) certs[0];
                info.setIssuer(x509.getIssuerDN().getName());
                info.setSubject(x509.getSubjectDN().getName());
                info.setExpiresOn(x509.getNotAfter().toString());
                info.setValid(true);
                info.setAlgorithm(x509.getSigAlgName());
                logger.accept("[+] SSL CERT: " + info.getSubject());
                logger.accept("  Issuer: " + info.getIssuer());
            }
        } catch (Exception e) {
            logger.accept("[!] SSL ANALYSIS FAILED: " + e.getMessage());
        }
        return info;
    }

    private List<Vulnerability> scanHeadersAndTech(String target, Consumer<String> logger, ScanResult result) {
        List<Vulnerability> vulns = new ArrayList<>();
        HttpURLConnection conn = null;
        try {
            // Try HTTP first
            logger.accept("Connecting via HTTP...");
            URL url = java.net.URI.create("http://" + target).toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(10000); 
            conn.setReadTimeout(10000);
            // CONCATENATED STRING TO PREVENT TOOL WRAPPING ISSUES
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " + 
                "NetSpecter-Enterprise/1.0");
            conn.setInstanceFollowRedirect
            conn.connect();

                    
            // Handle Redirects
            int status = conn.getResponseCode();
            logger.accept("HTTP Response: " + status);

            if (status >= 300 && status < 400) {
                String newUrl = conn.getHeaderField("Location");
                if (newUrl != null && newUrl.startsWith("https")) {
                    logger.accept("Following redirect to HTTPS...");
                    conn = (HttpURLConnection) new URL(newUrl).openConnection();
                    conn.setRequestMethod("HEAD");
                    conn.setConnectTimeout(10000);
                    conn.connect();
                }
            }
            
            // Tech Detection via Headers
            S

                result.getTechStack().add("Server: " + server);
                logger.accept("[*] DETECTED TECH: " + server);
            }
            String poweredBy = conn.getHeaderField("X-Powered-By");
            if (poweredBy != null) {
                result.getTechStack().add("Framework: " + poweredBy);
                logger.accept("[*] DETECTED TECH: " + poweredBy);
            }

            // Perform header checks
            performChecks(conn, vulns);

        } catch (Exception e) {
            // Fallback to HTTPS directly
            try {
                logger.accept("HTTP connection failed. Trying direct HTTPS...");
                String httpsTarget = target.startsWith("http") ? target : "https://" + target; 
                URL url = java.net.URI.create(httpsTarget).toURL();
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("HEAD");
                conn.setConnectTimeout(10000);
                // CONCATENATED STRING TO PREVENT TOOL WRAPPING ISSUES
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " + 
                    "NetSpecter-Enterprise/1.0");
                conn.connect();
                        
                

                tch (Exception ex) {
                String error = "Connection failed: " + ex.getMessage();
                logger.accept("[-] " + error);
                vulns.add(createVuln("Connectivity", "Critical", error, "Check URL validity"));
            }
        }
        return vulns;
    }

    private void performChecks(HttpURLConnection conn, List<Vulnerability> vulns) {
        checkMissingHeader(conn, "X-Content-Type-Options", "nosniff", "Low", "Prevents MIME-sniffing", vulns);
        checkMissingHeader(conn, "X-Frame-Options", "DENY", "Medium", "Prevents Clickjacking", vulns);
        checkMissingHeader(conn, "Content-Security-Policy", null, "High", "Mitigates XSS", vulns);
        checkMissingHeader(conn, "Strict-Transport-Security", null, "High", "Enforces HTTPS", vulns);
        checkMissingHeader(conn, "Permissions-Policy", null, "Low", "Controls browser features", vulns);
        checkMissingHeader(conn, "Referrer-Policy", null, "Low", "Controls referrer information", vulns);
    }
    
    private List<String> lookupRecords(String domain, int type) {
        List<String> records = new ArrayList<>();
        try {
            org.xbill.DNS.Record[] results = new Lookup(domain, type).run();
            if (results != null) {
                for (org.xbill.DNS.Record record : results) {
                    records.add(record.rdataToString());
                }
            }
        } catch (TextParseException e) {}
        return records;
    }

    private String extractTarget(String input) {
        if (input == null) return "";
        input = input.trim();
        if (input.contains("@")) input = input.substring(input.indexOf("@") + 1);
        if (input.toLowerCase().startsWith("http://")) input = input.substring(7);
        else if (input.toLowerCase().startsWith("https://")) input = input.substring(8);
        if (input.contains("/")) input = input.substring(0, input.indexOf("/"));
        if (input.contains("?")) input = input.substring(0, input.indexOf("?"));
        return input;
    }

    private void checkMissingHeader(HttpURLConnection conn, String header, String expectedValue, String severity, String desc, List<Vulnerability> vulns) {
        String value = conn.getHeaderField(header);
        if (value == null) {
            vulns.add(createVuln("Missing Security Header", severity, "Missing " + header + ". " + desc, "Add " + header + " to server config."));
        } else if (expectedValue != null && !value.contains(expectedValue)) {
            // Loose check
        }
    }

    private Vulnerability createVuln(String type, String severity, String desc, String rem) {
        Vulnerability v = new Vulnerability();
        v.setType(type);
        v.setSeverity(severity);
        v.setDescription(desc);
        v.setRemediation(rem);
        return v;
    }

    private void calculateThreatScore(ScanResult result) {
        double score = 0;
        if (result.getVulnerabilities() != null) {
            for (Vulnerability v : result.getVulnerabilities()) {
                switch (v.getSeverity()) {
                    case "Critical": score += 25; break;
                    case "High": score += 15; break;
                    case "Medium": score += 10; break;
                    case "Low": score += 5; break;
                }
            }
        }
        if (result.getOpenPorts() != null && !result.getOpenPorts().isEmpty()) {
            score += result.getOpenPorts().size() * 2;
        }
        if (result.getSslInfo() != null && !result.getSslInfo().isValid()) {
            score += 20; 
        }
        result.setThreatScore(Math.min(score, 100));
    }

    private String generateCodename(String target) {
        String[] prefixes = { "Operation", "Project", "Initiative", "Protocol" };
        String[] adjectives = { "Black", "Red", "Silent", "Shadow", "Crimson", "Zero", "Iron", "Ghost", "Dark", "Neon" };
        String[] nouns = { "Wolf", "Eagle", "Storm", "Specter", "Viper", "Cobra", "Dragon", "Phoenix", "Helix", "Cipher" };
        int hash = Math.abs(target.hashCode());
        return prefixes[hash % prefixes.length] + " " + adjectives[(hash / 10) % adjectives.length] + " " + nouns[(hash / 100) % nouns.length];
    }
}

        
                

        
                