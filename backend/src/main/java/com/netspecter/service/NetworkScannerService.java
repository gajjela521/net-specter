package com.netspecter.service;

import com.netspecter.model.NetworkDevice;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NetworkScannerService {

    private static final int TIMEOUT_MS = 200;
    private static final int THREADS = 50;

    public List<NetworkDevice> scanLocalNetwork() {
        System.out.println("Initiating Local Network Scan...");
        List<NetworkDevice> devices = Collections.synchronizedList(new ArrayList<>());
        String subnet = getLocalSubnet();

        if (subnet == null) {
            System.err.println("Could not determine local subnet.");
            return devices;
        }

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        // Scan 1-254
        for (int i = 1; i < 255; i++) {
            String host = subnet + "." + i;
            executor.execute(() -> {
                try {
                    InetAddress address = InetAddress.getByName(host);
                    if (address.isReachable(TIMEOUT_MS)) {
                        String mac = getMacAddress(host);
                        String vendor = resolveVendor(mac);
                        NetworkDevice device = new NetworkDevice(
                                host,
                                address.getHostName(),
                                mac != null ? mac : "Unknown",
                                vendor,
                                true);
                        devices.add(device);
                        System.out.println("[+] Found Device: " + host + " (" + device.getHostname() + ")");
                    }
                } catch (Exception e) {
                    // Ignore unreachable
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sort by IP for display
        devices.sort(Comparator.comparing(d -> ipToLong(d.getIp())));

        return devices;
    }

    private String getLocalSubnet() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            byte[] ip = localHost.getAddress();
            return (ip[0] & 0xFF) + "." + (ip[1] & 0xFF) + "." + (ip[2] & 0xFF);
        } catch (Exception e) {
            return "192.168.1"; // Default fallback
        }
    }

    private String getMacAddress(String ip) {
        try {
            // Check system ARP table
            Process process = Runtime.getRuntime().exec("arp -a");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            Pattern pattern = Pattern.compile(".*\\(" + ip.replace(".", "\\.") + "\\).*at\\s+([0-9a-f:]+).*",
                    Pattern.CASE_INSENSITIVE);

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    return matcher.group(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String resolveVendor(String mac) {
        if (mac == null)
            return "Unknown Vendor";
        mac = mac.toLowerCase().replace(":", "");

        // Simple OUI lookup implementation (In a real app, use a DB or API)
        if (mac.startsWith("005056"))
            return "VMware";
        if (mac.startsWith("000c29"))
            return "VMware";
        if (mac.startsWith("001c42"))
            return "Parallels";
        if (mac.startsWith("b827eb"))
            return "Raspberry Pi";
        if (mac.startsWith("dc a6 32"))
            return "Raspberry Pi";
        if (mac.startsWith("001422"))
            return "Dell";
        if (mac.startsWith("3c0754"))
            return "Apple";
        if (mac.startsWith("acbc32"))
            return "Apple";
        // ... add more common ones
        return "Generic Device";
    }

    private long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        long result = 0;
        for (String part : parts) {
            result = result << 8 | Integer.parseInt(part);
        }
        return result;
    }
}
