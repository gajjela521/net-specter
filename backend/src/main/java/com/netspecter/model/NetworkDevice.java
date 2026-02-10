package com.netspecter.model;

public class NetworkDevice {
    private String ip;
    private String hostname;
    private String macAddress;
    private String vendor;
    private boolean isActive;

    public NetworkDevice(String ip, String hostname, String macAddress, String vendor, boolean isActive) {
        this.ip = ip;
        this.hostname = hostname;
        this.macAddress = macAddress;
        this.vendor = vendor;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
