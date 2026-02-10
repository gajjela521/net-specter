package com.netspecter.service;

import com.netspecter.model.ScanResult;
import com.netspecter.model.ScanResult.GeoHop;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
public class GeoRouteService {

    public List<GeoHop> traceRoute(String target, Consumer<String> logger) {
        logger.accept("[STAGE:GEO] Initiating Geo-Location Trace...");
        List<GeoHop> hops = new ArrayList<>();

        try {
            // Source: User Location (Approximated as "Texas, USA" per request)
            hops.add(new GeoHop(1, "192.168.1.5", "Texas, USA (Source)", 31.9686, -99.9018));
            logger.accept("TRACE: Hop 1 [Source] -> Texas, USA");

            // Intermediate Hops (Simulated Path through major internet exchanges)
            // Path: Texas -> New York -> London -> Mumbai -> Hyderabad

            // Hop 2: ISP Backbone (New York)
            Thread.sleep(600);
            hops.add(new GeoHop(2, "208.67.222.222", "New York, USA (Backbone)", 40.7128, -74.0060));
            logger.accept("TRACE: Hop 2 [Backbone] -> New York, USA");

            // Hop 3: Trans-Atlantic Link (London)
            Thread.sleep(800);
            hops.add(new GeoHop(3, "212.58.244.20", "London, UK (Exchange)", 51.5074, -0.1278));
            logger.accept("TRACE: Hop 3 [Gateway] -> London, UK");

            // Hop 4: Middle East / Asia Link (Mumbai or Dubai)
            Thread.sleep(600);
            hops.add(new GeoHop(4, "103.21.244.0", "Mumbai, India (ISP)", 19.0760, 72.8777));
            logger.accept("TRACE: Hop 4 [Regional ISP] -> Mumbai, India");

            // Hop 5: Destination (Target Server)
            InetAddress address = InetAddress.getByName(target);
            Thread.sleep(400);

            // For demo purposes, if target is vardhaman.org, we explicitly place it in
            // Hyderabad.
            // Otherwise, we'd use a GeoIP library. Here we simulate the final hop based on
            // the request.
            double destLat = 17.3850;
            double destLon = 78.4867;
            String destLoc = "Hyderabad, India (Target)";

            // Fallback for non-specific targets (randomize slightly for variation)
            if (!target.contains("vardhaman") && !target.contains("india")) {
                // Keep Hyderabad for the specific requested demo flow, but normally this would
                // be dynamic.
                // Since the user asked specifically about this flow, we hardcode the
                // destination visualization
                // to match their story for the demo.
            }

            hops.add(new GeoHop(5, address.getHostAddress(), destLoc, destLat, destLon));
            logger.accept("TRACE: Hop 5 [Destination] -> " + destLoc + " (" + address.getHostAddress() + ")");
            logger.accept("✔ GEO-TRACE COMPLETE: 5 Hops Mapped.");

        } catch (Exception e) {
            logger.accept("⚠ GEO-TRACE ERROR: " + e.getMessage());
        }

        return hops;
    }
}
