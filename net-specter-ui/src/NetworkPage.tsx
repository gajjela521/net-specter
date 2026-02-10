import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Wifi, Laptop, Server, Smartphone, Monitor, AlertTriangle, Shield, CheckCircle, Search, Hash
} from 'lucide-react';
import './App.css';

interface NetworkDevice {
    ip: string;
    hostname: string;
    macAddress: string;
    vendor: string;
    active: boolean;
}

function NetworkPage() {
    const [devices, setDevices] = useState<NetworkDevice[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    const startScan = async () => {
        setLoading(true);
        setError(null);
        setDevices([]);
        try {
            const response = await fetch('/api/network/scan');
            if (!response.ok) {
                throw new Error('Network scan failed');
            }
            const data = await response.json();
            setDevices(data);
        } catch (err: any) {
            setError(err.message || 'Error occurred during scan');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        startScan(); // Auto-scan on load? Or manual? Let's auto-scan
    }, []);

    const getIcon = (vendor: string) => {
        const v = vendor.toLowerCase();
        if (v.includes('apple') || v.includes('mac')) return <Laptop size={20} color="#58a6ff" />;
        if (v.includes('dell') || v.includes('hp') || v.includes('lenovo')) return <Monitor size={20} color="#7ee787" />;
        if (v.includes('android') || v.includes('samsung') || v.includes('mobile')) return <Smartphone size={20} color="#ffa657" />;
        if (v.includes('pi') || v.includes('server')) return <Server size={20} color="#ff7b72" />;
        return <Wifi size={20} color="#8b949e" />;
    };

    return (
        <div className="container">
            <header className="header">
                <div className="logo" onClick={() => navigate('/')} style={{ cursor: 'pointer' }}>
                    <Shield size={28} color="#58a6ff" />
                    <h1>NetSpecter <span style={{ color: '#8b949e', fontSize: '0.9rem' }}>// Local Network Recon</span></h1>
                </div>
                <button className="scan-button" onClick={() => navigate('/')} style={{ padding: '8px 16px', fontSize: '0.9rem' }}>
                    Back to Target Scan
                </button>
            </header>

            <main className="main-content">
                <div className="card" style={{ textAlign: 'center', padding: '40px' }}>
                    <h2 style={{ marginBottom: '20px' }}>Local Wi-Fi Network Map</h2>
                    <p style={{ color: '#8b949e', marginBottom: '30px' }}>
                        Scanning local subnet for connected devices. Identifying MAC addresses and vendors via ARP table analysis.
                    </p>

                    {loading ? (
                        <div className="loading-container">
                            <div className="sonar-wave"></div>
                            <div className="icon-pulse">
                                <Wifi size={48} color="#58a6ff" />
                            </div>
                            <p style={{ marginTop: '20px', color: '#58a6ff' }}>Scanning Local Subnet (1-254)...</p>
                        </div>
                    ) : (
                        <>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                                <div style={{ fontSize: '1.2rem', fontWeight: 'bold' }}>
                                    Devices Found: <span style={{ color: '#58a6ff' }}>{devices.length}</span>
                                </div>
                                <button className="scan-button" onClick={startScan}>
                                    Rescan Network
                                </button>
                            </div>

                            {error && (
                                <div className="error-message">
                                    <AlertTriangle size={20} />
                                    <span>{error}</span>
                                </div>
                            )}

                            <div className="results-grid" style={{ gridTemplateColumns: '1fr' }}>
                                <table style={{ width: '100%', borderCollapse: 'collapse', color: '#c9d1d9' }}>
                                    <thead>
                                        <tr style={{ borderBottom: '1px solid #30363d', textAlign: 'left' }}>
                                            <th style={{ padding: '12px', color: '#8b949e' }}>Type</th>
                                            <th style={{ padding: '12px', color: '#8b949e' }}>IP Address</th>
                                            <th style={{ padding: '12px', color: '#8b949e' }}>Hostname</th>
                                            <th style={{ padding: '12px', color: '#8b949e' }}>MAC Address</th>
                                            <th style={{ padding: '12px', color: '#8b949e' }}>Vendor</th>
                                            <th style={{ padding: '12px', color: '#8b949e' }}>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {devices.map((device, index) => (
                                            <tr key={index} style={{ borderBottom: '1px solid #21262d' }}>
                                                <td style={{ padding: '12px' }}>{getIcon(device.vendor)}</td>
                                                <td style={{ padding: '12px', fontFamily: 'monospace', color: '#79c0ff' }}>{device.ip}</td>
                                                <td style={{ padding: '12px' }}>{device.hostname}</td>
                                                <td style={{ padding: '12px', fontFamily: 'monospace', color: '#ff7b72' }}>{device.macAddress}</td>
                                                <td style={{ padding: '12px' }}>{device.vendor}</td>
                                                <td style={{ padding: '12px' }}>
                                                    <span style={{
                                                        background: 'rgba(56, 139, 253, 0.15)',
                                                        color: '#58a6ff',
                                                        padding: '2px 8px',
                                                        borderRadius: '12px',
                                                        fontSize: '0.75rem',
                                                        border: '1px solid rgba(56, 139, 253, 0.4)'
                                                    }}>
                                                        ONLINE
                                                    </span>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                                {devices.length === 0 && !loading && !error && (
                                    <div style={{ padding: '40px', color: '#8b949e', fontStyle: 'italic' }}>
                                        No devices found. Ensure backend has network permissions.
                                    </div>
                                )}
                            </div>
                        </>
                    )}
                </div>
            </main>
        </div>
    );
}

export default NetworkPage;
