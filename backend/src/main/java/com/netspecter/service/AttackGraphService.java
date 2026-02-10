package com.netspecter.service;

import com.netspecter.model.ScanResult;
import com.netspecter.model.ScanResult.Vulnerability;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class AttackGraphService {

    public AttackGraph buildAttackGraph(ScanResult result, Consumer<String> logger) {
        logger.accept("[STAGE:VISUAL] Constructing Attack Path Graph...");
        AttackGraph graph = new AttackGraph();

        // Node 1: Internet (Attacker Entry)
        Node internet = new Node("Internet/Attacker", "actor");
        graph.addNode(internet);

        // Node 2: Firewall / Gateway
        Node firewall = new Node("Firewall/Edge", "device");
        graph.addNode(firewall);
        graph.addEdge(internet.getId(), firewall.getId(), "Direct Connect");

        // Node 3: Target Server (Web Server)
        Node server = new Node("Target: " + result.getTarget(), "server");
        graph.addNode(server);

        // Calculate Access from Firewall to Server based on Ports
        if (result.getOpenPorts() != null && !result.getOpenPorts().isEmpty()) {
            StringBuilder ports = new StringBuilder();
            for (Integer p : result.getOpenPorts())
                ports.append(p).append(",");
            graph.addEdge(firewall.getId(), server.getId(), "Allow: " + ports.toString());
            logger.accept("MAP: Exposed Ports -> " + ports);
        } else {
            graph.addEdge(firewall.getId(), server.getId(), "Full Block / Unknown");
        }

        // Node 4: Application Layer (Tech Stack)
        Node app = new Node("App Logic", "software");
        graph.addNode(app);
        graph.addEdge(server.getId(), app.getId(), "Host Process");

        // Identify Attack Paths based on Vulns
        if (result.getVulnerabilities() != null) {
            for (Vulnerability v : result.getVulnerabilities()) {
                if (v.getType().contains("HTML"))
                    continue; // Skip minor

                Node vulnNode = new Node("Unknown Exploit", "exploit");
                if (v.getType().contains("XSS")) {
                    vulnNode = new Node("XSS Payload", "exploit");
                    graph.addEdge(internet.getId(), app.getId(), "Bypasses Firewall (Layer 7)");
                } else if (v.getType().contains("SSL")) {
                    vulnNode = new Node("MITM Opportunity", "exploit");
                    graph.addEdge(internet.getId(), server.getId(), "Intercept Traffic");
                }

                graph.addNode(vulnNode);
                // graph.addEdge(internet.getId(), vulnNode.getId(), "Execute");
                // graph.addEdge(vulnNode.getId(), app.getId(), "Compromise");
            }
        }

        // Potential Backend / Database (Inferred)
        Node database = new Node("Database (Inferred)", "database");
        graph.addNode(database);

        // If high threat score, assume potential lateral movement
        if (result.getThreatScore() > 50) {
            graph.addEdge(app.getId(), database.getId(), "SQL Injection / Data Exfil Risk");
            logger.accept("MAP: Potential Lateral Movement -> Database");
        } else {
            graph.addEdge(app.getId(), database.getId(), "Internal Query");
        }

        return graph;
    }

    // --- Embedded Graph Model ---

    public static class AttackGraph {
        private final List<Node> nodes = new ArrayList<>();
        private final List<Edge> edges = new ArrayList<>();

        public void addNode(Node n) {
            this.nodes.add(n);
        }

        public void addEdge(String src, String tgt, String label) {
            this.edges.add(new Edge(src, tgt, label));
        }

        public List<Node> getNodes() {
            return nodes;
        }

        public List<Edge> getEdges() {
            return edges;
        }
    }

    public static class Node {
        private String id;
        private String label;
        private String type; // actor, device, server, software, database, exploit

        public Node(String label, String type) {
            this.id = UUID.randomUUID().toString();
            this.label = label;
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public String getType() {
            return type;
        }
    }

    public static class Edge {
        private String source;
        private String target;
        private String label;

        public Edge(String s, String t, String l) {
            this.source = s;
            this.target = t;
            this.label = l;
        }

        public String getSource() {
            return source;
        }

        public String getTarget() {
            return target;
        }

        public String getLabel() {
            return label;
        }
    }
}
