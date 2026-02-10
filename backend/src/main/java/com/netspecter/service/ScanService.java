package com.netspecter.service;

import com.netspecter.model.ScanResult;
import com.netspecter.model.ScanResult.*; // Import inner beans like GeoHop, IpInfo, etc.
import com.netspecter.service.DarkWebService;
import com.netspecter.service.AttackGraphService;
import com.netspecter.service.GeoRouteService;

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
    private DarkWebService darkWebService;
    
    @Autowired

        
    @Autowired
    private GeoRouteService geoRouteService;

    @Autowired
    private AdvancedReconService advancedReconService;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    public ScanResult performScan(String input) {
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
                logger.accept("✔ IP RESOLVED: " + address.getHostAddress());

                // DNS Enumeration
                logger.accept("... Enumerating DNS Records");
                DnsInfo dnsInfo = new DnsInfo();
                dnsInfo.setARecords(lookupRecords(target, Type.A));
                dnsInfo.setMxRecords(lookupRecords(target, Type.MX));
                dnsInfo.setTxtRecords(lookupRecords(target, Type.TXT));
                dnsInfo.setServerLocation("Geo-location initiated..."); 
                result.setDnsInfo(dnsInfo);
                logger.accept("✔ DNS MAPPING COMPLETE (" + (dnsInfo.getARecords().size() + dnsInfo.getMxRecords().size()) + " records found)");
                logger.accept("[STAGE:NETWORK] Network Phase Complete.");

                // Phase 2: Application Level (Ports, SSL, Tech)
                logger.accept("[STAGE:APP] Starting Application & Port Analysis...");
                
                // Port Scanning
                List<Integer> commonPorts = List.of(80, 443, 8080, 8443, 21, 22, 25, 3306, 5432);
                List<Integer> openPorts = new ArrayList<>();
                for (int port : commonPorts) {
                    if (isPortOpen(target, port)) {
                        openPorts.add(port);
                        logger.accept("⚠ OPEN PORT: " + port);
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
                logger.accept("✔ THREAT SCORE: " + result.getThreatScore() + "/100");

                if (result.getThreatScore() > 75) {
                    result.setSummary("CRITICAL THREAT DETECTED. Immediate remediation required.");
                } else if (result.getThreat

    
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
             

    

    
    anization(InetAddress address) {

    
    rtOpen(String host, int p

    
    


    
    t

    ty.cert.Certificate[] certs = conn.getSers.length > 0 & certs[0] insanceof X509Cer

    .setValid(true);cept

     (xception e) {
    
        er.accept("⚠ SSL ANALYSIS FAILED: " + e.getMessage
        
        nfo;
        
        
        <Vulnerability> scanHeadersAndTech(Strin
        nerability> vulns = new ArrayList<>();
        onnection conn = null;
    {   // r
    y HTTP first
    
        logger.accept("Connecting via HTTP...");
         conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(10000); 
            conn.setReadTimeout(10000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) NetSpecter-Enterprise/1.0");
            conn.setInstanceFollowRedirects(true);
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
            String server = conn.getHeaderField("Server");
            if (server != null) {
              result.getTec
    Stack().add("Server: " + server);
                logger.accept("ℹ DETECTED TECH: " + server);
            }
            String poweredBy = conn.getHeaderField("X-Powered-By");
            if (poweredBy != null) {
                result.getTechStack().add("Framework: " + poweredBy);
                logger.accept("ℹ DETECTED TECH: " + poweredBy);
            }

            // Perform header checks
           perormChecks(con
    , vulns);


    logger.accept("HTTP connection failed. Trying direct HTTPS...");
                String httpsTarget = target.startsWith("http") ? target : "https://" + target; 
                URL url = java.net.URI.create(httpsTarget).toURL();
                conn = (HttpURLConnection) url.openConnection();
                      conn. 

        } catch (Exception e) {
                String error = "Connection


    
    

    kMissingHeader(conn, "Strict-Transport-Security", null, "High", kMssingHeaerconn,
    "
        singH L

    org.xbill.DNS.Record[] rsults = new Lookup(domain type).run);
     

        }} catch (TextParseExcepton e) {}return records;


    oLowerCase().startsWith("https://")) input = input.s
    
        
            
        ne == nul

    
    

    Vulnerability v = new Vunerability();v.setType(type);v.setSeverity(severity);v.setDescription(desc); 

    
    pr

           cas "ritic
    
        
            gh": score += 15; break;
         
           }
    }
    
    }
     

            score += 20; 
     
    }

    private String generateCodename(String target) {
        String[] prefixes = { "Operation", "Project", "Initiative", "Protocol" };
        String[] adjectives = { "Black", "Red", "Silent", "Shadow", "Crimson", "Zero", "Iron", "Ghost", "Dark", "Neon" };
        String[] nouns = { "Wolf", "Eagle", "Storm", "Specter", "Viper", "Cobra", "Dragon", "Phoenix", "Helix", "Cipher" };
        int hash = Math.abs(target.hashCode());
        return prefixes[hash % prefixes.length] + " " + adjectives[(hash / 10) % adjectives.length] + " " + nouns[(hash / 100) % nouns.length];
    }
}

    

    
