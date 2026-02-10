package com.netspecter.model;

import java.util.List;

public class ScanResult {
    private String target;
    private String scanTime;
    private DnsInfo dnsInfo;
    private IpInfo ipInfo;
    private List<Vulnerability> vulnerabilities;
    private double threatScore;
    private String summary;
    private String codename;
    private List<String> darkWebFindings;
    private com.netspecter.service.AttackGraphService.AttackGraph attackGraph;

    // Getters and Setters
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<String> getDarkWebFindings() {
        return darkWebFindings;
    }

    public void setDarkWebFindings(List<String> findings) {
        this.darkWebFindings = findings;
    }

    public com.netspecter.service.AttackGraphService.AttackGraph getAttackGraph() {
        return attackGraph;
    }

    public void setAttackGraph(com.netspecter.service.AttackGraphService.AttackGraph graph) {
        this.attackGraph = graph;
    }

    public String getScanTime() {
        return scanTime;
    }

    public void setScanTime(String scanTime) {
        this.scanTime = scanTime;
    }

    public DnsInfo getDnsInfo() {
        return dnsInfo;
    }

    public void setDnsInfo(DnsInfo dnsInfo) {
        this.dnsInfo = dnsInfo;
    }

    public IpInfo getIpInfo() {
        return ipInfo;
    }

    public void setIpInfo(IpInfo ipInfo) {
        this.ipInfo = ipInfo;
    }

    public List<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    public void setVulnerabilities(List<Vulnerability> vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }

    public double getThreatScore() {
        return threatScore;
    }

    public void setThreatScore(double threatScore) {
        this.threatScore = threatScore;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCodename() {
        return codename;
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    private SslInfo sslInfo;
    private List<Integer> openPorts;
    private List<String> techStack;

    public SslInfo getSslInfo() {
        return sslInfo;
    }

    public void setSslInfo(SslInfo sslInfo) {
        this.sslInfo = sslInfo;
    }

    public List<Integer> getOpenPorts() {
        return openPorts;
    }

    public void setOpenPorts(List<Integer> openPorts) {
        this.openPorts = openPorts;
    }

    public List<String> getTechStack() {
        return techStack;
    }

    public void setTechStack(List<String> techStack) {
        this.techStack = techStack;
    }

    public static class SslInfo {
        private boolean valid;
        private String algorithm;
        private String issuer;
        private String subject;
        private String expiresOn;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getExpiresOn() {
            return expiresOn;
        }

        public void setExpiresOn(String expiresOn) {
            this.expiresOn = expiresOn;
        }
    }

    public static class DnsInfo {
        private List<String> aRecords;
        private List<String> mxRecords;
        private List<String> txtRecords;
        private String serverLocation;

        public List<String> getARecords() {
            return aRecords;
        }

        public void setARecords(List<String> aRecords) {
            this.aRecords = aRecords;
        }

        public List<String> getMxRecords() {
            return mxRecords;
        }

        public void setMxRecords(List<String> mxRecords) {
            this.mxRecords = mxRecords;
        }

        public List<String> getTxtRecords() {
            return txtRecords;
        }

        public void setTxtRecords(List<String> txtRecords) {
            this.txtRecords = txtRecords;
        }

        public String getServerLocation() {
            return serverLocation;
        }

        public void setServerLocation(String serverLocation) {
            this.serverLocation = serverLocation;
        }
    }

    public static class IpInfo {
        private String ipAddress;
        private String hostname;
        private String organization;

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public String getOrganization() {
            return organization;
        }

        public void setOrganization(String organization) {
            this.organization = organization;
        }
    }

    public static class Vulnerability {
        private String type;
        private String severity; // LOW, MEDIUM, HIGH, CRITICAL
        private String description;
        private String remediation;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getRemediation() {
            return remediation;
        }

        public void setRemediation(String remediation) {
            this.remediation = remediation;
        }
    }

    private List<GeoHop> geoTrace;
    private AdvancedRecon advancedRecon;

    public List<GeoHop> getGeoTrace() {
        return geoTrace;
    }

    public void setGeoTrace(List<GeoHop> geoTrace) {
        this.geoTrace = geoTrace;
    }

    public AdvancedRecon getAdvancedRecon() {
        return advancedRecon;
    }

    public void setAdvancedRecon(AdvancedRecon advancedRecon) {
        this.advancedRecon = advancedRecon;
    }

    public static class AdvancedRecon {
        private List<String> subdomains;
        private List<String> leakedEmails;
        private List<String> cloudAssets; // S3 Buckets, Blobs
        private String wafStatus;
        private String whoisRegistrar;

        public List<String> getSubdomains() {
            return subdomains;
        }

        public void setSubdomains(List<String> subdomains) {
            this.subdomains = subdomains;
        }

        public List<String> getLeakedEmails() {
            return leakedEmails;
        }

        public void setLeakedEmails(List<String> leakedEmails) {
            this.leakedEmails = leakedEmails;
        }

        public List<String> getCloudAssets() {
            return cloudAssets;
        }

        public void setCloudAssets(List<String> cloudAssets) {
            this.cloudAssets = cloudAssets;
        }

        public String getWafStatus() {
            return wafStatus;
        }

        public void setWafStatus(String wafStatus) {
            this.wafStatus = wafStatus;
        }

        public String getWhoisRegistrar() {
            return whoisRegistrar;
        }

        public void setWhoisRegistrar(String whoisRegistrar) {
            this.whoisRegistrar = whoisRegistrar;
        }
    }

    public static class GeoHop {
        private int step;
        private String ip;
        private String location;
        private double latitude;
        private double longitude;

        public GeoHop(int step, String ip, String location, double latitude, double longitude) {
            this.step = step;
            this.ip = ip;
            this.location = location;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public int getStep() {
            return step;
        }

        public String getIp() {
            return ip;
        }

        public String getLocation() {
            return location;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
}
