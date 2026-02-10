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

    // Getters and Setters
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
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
}
