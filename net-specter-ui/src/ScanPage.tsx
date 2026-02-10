import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Shield, Globe, Terminal, Activity, AlertTriangle, CheckCircle,
    Search, LogOut, Lock, Cpu, Radio, Hash
} from 'lucide-react';
import './App.css';

interface Vulnerability {
    type: string;
    severity: "Low" | "Medium" | "High" | "Critical";
    description: string;
    remediation: string;
}

interface ScanResult {
    target: string;
    scanTime: string;
    threatScore: number;
    summary: string;
    codename: string;
    ipInfo: {
        ipAddress: string;
        hostname: string;
        organization: string;
    };
    dnsInfo: {
        aRecords: string[];
        mxRecords: string[];
        txtRecords: string[];
        serverLocation: string;
    };
    sslInfo?: {
        valid: boolean;
        algorithm: string;
        issuer: string;
        subject: string;
        expiresOn: string;
    };
    openPorts?: number[];
    techStack?: string[];
    vulnerabilities: Vulnerability[];
    darkWebFindings?: string[];
    attackGraph?: {
        nodes: { id: string; label: string; type: string }[];
        edges: { source: string; target: string; label: string }[];
    };
    geoTrace?: {
        step: number;
        ip: string;
        location: string;
        latitude: number;
        longitude: number;
    }[];
    advancedRecon?: {
        subdomains: string[];
        leakedEmails: string[];
        cloudAssets: string[];
        wafStatus: string;
        whoisRegistrar: string;
    };
}

function ScanPage() {
    const [target, setTarget] = useState('');
    const [loading, setLoading] = useState(false);
    const [result, setResult] = useState<ScanResult | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [logs, setLogs] = useState<string[]>([]);
    const logsEndRef = useRef<HTMLDivElement>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) navigate('/login');
    }, [navigate]);

    useEffect(() => {
        if (logsEndRef.current) {
            logsEndRef.current.scrollIntoView({ behavior: 'smooth' });
        }
    }, [logs]);

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    const handleScan = async () => {
        if (!target) return;
        setLoading(true);
        setError(null);
        setResult(null);
        setLogs(["Creating secure channel...", "Initializing NetSpecter Engine..."]);

        const token = localStorage.getItem('token');
        if (!token) {
            navigate('/login');
            return;
        }

        try {
            let apiUrl = import.meta.env.VITE_API_URL || 'https://net-specter-1.onrender.com';
            if (!apiUrl.startsWith('http')) apiUrl = `https://${apiUrl}`;

            // SSE Connection
            // Note: EventSource doesn't support headers natively in all browsers, 
            // but we use query param for target. Auth token is harder with native EventSource.
            // For now, we assume the stream endpoint is public or we pass token in query param (temporary solution).
            // Better Enterprise solution: Use fetch() with ReadableStream or a library like event-source-polyfill.

            // Construct URL with query params
            const streamUrl = new URL(`${apiUrl}/api/scan/stream`);
            streamUrl.searchParams.append("target", target);
            // In a real app, we'd secure this better, e.g., via a short-lived ticket.

            const eventSource = new EventSource(streamUrl.toString());

            eventSource.addEventListener("log", (event) => {
                const message = event.data;
                setLogs(prev => [...prev, message]);
            });

            eventSource.addEventListener("result", (event) => {
                try {
                    const data = JSON.parse(event.data);
                    setResult(data);
                    setLoading(false);
                    eventSource.close();
                } catch (e) {
                    console.error("Failed to parse result", e);
                }
            });

            eventSource.onerror = (err) => {
                console.error("EventSource failed:", err);
                eventSource.close();
                setLoading(false);
                // Only set error if we haven't received a result yet
                if (!result) {
                    setError("Connection to scan stream interrupted. The scan may have completed or failed.");
                }
            };

        } catch (err: any) {
            setError(err.message || "Failed to initialize scan");
            setLoading(false);
        }
    };

    // Server Stats Logic
    const [stats, setStats] = useState({ activeUsers: 0, activeRequests: 0, uptime: '0h 00m' });

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const res = await fetch('/api/status');
                if (res.ok) {
                    const data = await res.json();
                    setStats(data);
                }
            } catch (e) { console.error("Stats fetch failed", e); }
        };

        fetchStats();
        const interval = setInterval(fetchStats, 5000);
        return () => clearInterval(interval);
    }, []);

    const getSeverityColor = (severity: string) => {
        switch (severity) {
            case 'Critical': return 'risk-critical';
            case 'High': return 'risk-high';
            case 'Medium': return 'risk-medium';
            case 'Low': return 'risk-low';
            default: return 'risk-low';
        }
    };

    return (
        <div style={{ maxWidth: '1400px', margin: '0 auto', padding: '2rem' }}>
            {/* Header */}
            <header style={{ display: 'flex', alignItems: 'center', marginBottom: '2rem', justifyContent: 'space-between', borderBottom: '1px solid var(--border)', paddingBottom: '1rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <Shield size={40} color="#58a6ff" />
                    <div>
                        <h1 style={{ margin: 0, fontSize: '2rem', letterSpacing: '-1px' }}>NetSpecter <span style={{ fontSize: '0.8rem', color: 'var(--accent)', verticalAlign: 'super' }}>ENTERPRISE</span></h1>
                        <p style={{ margin: 0, opacity: 0.6, fontSize: '0.9rem' }}>Advanced Cyber Intelligence Platform</p>
                    </div>
                </div>
                {/* Live Stats Bar */}
                <div style={{
                    display: 'flex',
                    gap: '20px',
                    alignItems: 'center',
                    background: 'rgba(13, 17, 23, 0.8)',
                    padding: '8px 16px',
                    borderRadius: '6px',
                    border: '1px solid #30363d'
                }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.85rem', color: '#8b949e' }}>
                        <Globe size={14} color="#58a6ff" />
                        <span>Active Users: <strong style={{ color: '#c9d1d9' }}>{stats.activeUsers}</strong></span>
                    </div>
                    <div style={{ width: '1px', height: '16px', background: '#30363d' }}></div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.85rem', color: '#8b949e' }}>
                        <Activity size={14} color="#7ee787" />
                        <span>Live Requests: <strong style={{ color: '#c9d1d9' }}>{stats.activeRequests}</strong></span>
                    </div>
                    <div style={{ width: '1px', height: '16px', background: '#30363d' }}></div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.85rem', color: '#8b949e' }}>
                        <CheckCircle size={14} color="#e3b341" />
                        <span>Uptime: <strong style={{ color: '#c9d1d9' }}>{stats.uptime}</strong></span>
                    </div>
                </div>

                <div style={{ display: 'flex', gap: '1rem' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', background: 'rgba(35, 134, 54, 0.2)', padding: '0.5rem 1rem', borderRadius: '4px', border: '1px solid var(--success-border)', color: 'var(--success)' }}>
                        <div style={{ width: '8px', height: '8px', borderRadius: '50%', background: 'var(--success)', boxShadow: '0 0 8px var(--success)' }}></div>
                        System Operational
                    </div>
                    <button onClick={handleLogout} style={{ background: 'var(--panel-bg)', border: '1px solid var(--border)', color: 'white', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <LogOut size={16} /> Logout
                    </button>
                    <button onClick={() => navigate('/network')} style={{ background: 'var(--panel-bg)', border: '1px solid var(--accent)', color: 'var(--accent)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <Radio size={16} /> WiFi Scan
                    </button>
                </div>
            </header>

            {/* Scan Controls */}
            <div className="card" style={{ display: 'flex', gap: '1rem', alignItems: 'center', marginBottom: '2rem', boxShadow: '0 4px 20px rgba(0,0,0,0.3)' }}>
                <Search size={20} style={{ opacity: 0.5 }} />
                <input
                    type="text"
                    placeholder="Enter target (e.g., example.com, 192.168.1.5)"
                    style={{ flex: 1, background: 'transparent', border: 'none', fontSize: '1.2rem', fontFamily: 'monospace' }}
                    value={target}
                    onChange={(e) => setTarget(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && handleScan()}
                    disabled={loading}
                />
                <button
                    onClick={handleScan}
                    disabled={loading}
                    style={{
                        background: loading ? 'var(--panel-bg)' : 'var(--primary)',
                        borderColor: loading ? 'var(--border)' : 'var(--primary)',
                        opacity: loading ? 0.7 : 1
                    }}
                >
                    {loading ? <Activity className="spin" size={18} /> : <Terminal size={18} />}
                    {loading ? ' Scanning...' : ' Launch Scan'}
                </button>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(12, 1fr)', gap: '20px' }}>

                {/* Live Logs Terminal */}
                {/* Live Logs Terminal - Multi-Stage Display */}
                <div className="card" style={{
                    gridColumn: result ? 'span 4' : 'span 12',
                    fontFamily: 'monospace',
                    background: '#0d1117',
                    border: '1px solid var(--border)',
                    height: '500px',
                    display: 'flex',
                    flexDirection: 'column',
                    padding: 0,
                    overflow: 'hidden'
                }}>
                    <h3 style={{
                        margin: 0,
                        display: 'flex',
                        alignItems: 'center',
                        gap: '10px',
                        color: '#58a6ff',
                        fontSize: '0.9rem',
                        borderBottom: '1px solid #30363d',
                        padding: '10px 15px',
                        background: '#161b22'
                    }}>
                        <Terminal size={16} /> Live Operation Stages
                    </h3>

                    <div style={{ flex: 1, overflowY: 'auto', padding: '10px' }}>
                        {logs.length === 0 && <div style={{ opacity: 0.3, fontStyle: 'italic', padding: '1rem' }}>Waiting for command...</div>}

                        {/* Network Stage Logs */}
                        <div style={{ marginBottom: '15px' }}>
                            <div style={{ color: '#79c0ff', fontSize: '0.8rem', fontWeight: 'bold', borderBottom: '1px solid #30363d', marginBottom: '5px', paddingBottom: '2px', textTransform: 'uppercase' }}>
                                [Stage 1] Network Infrastructure
                            </div>
                            {logs.filter(l => {
                                const appIndex = logs.findIndex(x => x.includes("[STAGE:APP]"));
                                const myIndex = logs.indexOf(l);
                                if (l.includes("[STAGE:NETWORK]")) return true;
                                if (l.includes("[STAGE:")) return false;
                                if (appIndex === -1) return true;
                                return myIndex < appIndex;
                            }).map((log, i) => (
                                <div key={i} style={{
                                    paddingLeft: '8px',
                                    fontSize: '0.8rem',
                                    color: log.includes('✔') ? '#7ee787' : '#8b949e',
                                    display: log.includes("[STAGE:") ? 'none' : 'block'
                                }}>
                                    {log}
                                </div>
                            ))}
                        </div>

                        {/* App Stage Logs */}
                        {logs.some(l => l.includes("[STAGE:APP]")) && (
                            <div style={{ marginBottom: '15px' }}>
                                <div style={{ color: '#d2a8ff', fontSize: '0.8rem', fontWeight: 'bold', borderBottom: '1px solid #30363d', marginBottom: '5px', paddingBottom: '2px', textTransform: 'uppercase' }}>
                                    [Stage 2] Application & Ports
                                </div>
                                {logs.filter(l => {
                                    const appStart = logs.findIndex(x => x.includes("[STAGE:APP]"));
                                    const secStart = logs.findIndex(x => x.includes("[STAGE:SECURITY]"));
                                    const index = logs.indexOf(l);
                                    return index > appStart && (secStart === -1 || index < secStart);
                                }).map((log, i) => (
                                    <div key={i} style={{
                                        paddingLeft: '8px',
                                        fontSize: '0.8rem',
                                        color: log.includes('⚠') ? '#e3b341' : '#8b949e',
                                        display: log.includes("[STAGE:") ? 'none' : 'block'
                                    }}>
                                        {log}
                                    </div>
                                ))}
                            </div>
                        )}

                        {/* Security Stage Logs */}
                        {logs.some(l => l.includes("[STAGE:SECURITY]")) && (
                            <div style={{ marginBottom: '15px' }}>
                                <div style={{ color: '#ff7b72', fontSize: '0.8rem', fontWeight: 'bold', borderBottom: '1px solid #30363d', marginBottom: '5px', paddingBottom: '2px', textTransform: 'uppercase' }}>
                                    [Stage 3] Security & API Audit
                                </div>
                                {logs.filter(l => {
                                    const secStart = logs.findIndex(x => x.includes("[STAGE:SECURITY]"));
                                    return logs.indexOf(l) > secStart;
                                }).map((log, i) => (
                                    <div key={i} style={{
                                        paddingLeft: '8px',
                                        fontSize: '0.8rem',
                                        color: log.includes('CRITICAL') ? '#ff7b72' : '#8b949e',
                                        display: log.includes("[STAGE:") ? 'none' : 'block'
                                    }}>
                                        {log}
                                    </div>
                                ))}
                            </div>
                        )}

                        <div ref={logsEndRef} />
                        {error && (
                            <div style={{ color: '#ff7b72', borderTop: '1px solid #30363d', padding: '10px', marginTop: '10px' }}>
                                [!] {error}
                            </div>
                        )}
                    </div>
                </div>

                {/* Results Panel */}
                {result && (
                    <div style={{ gridColumn: 'span 8', display: 'flex', flexDirection: 'column', gap: '20px' }}>

                        {/* Top Stats Row */}
                        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '20px' }}>
                            <div className="card" style={{ textAlign: 'center', position: 'relative', overflow: 'hidden' }}>
                                <div style={{ position: 'absolute', top: 0, left: 0, width: '100%', height: '4px', background: result.threatScore > 50 ? 'var(--danger)' : 'var(--success)' }}></div>
                                <h3 style={{ fontSize: '0.8rem', opacity: 0.7, textTransform: 'uppercase' }}>Threat Score</h3>
                                <div style={{ fontSize: '3.5rem', fontWeight: 'bold', color: result.threatScore > 50 ? 'var(--danger)' : 'var(--success)' }}>
                                    {result.threatScore.toFixed(0)}
                                </div>
                            </div>

                            <div className="card">
                                <h3 style={{ fontSize: '0.8rem', opacity: 0.7, textTransform: 'uppercase', display: 'flex', alignItems: 'center', gap: '8px' }}>
                                    <Globe size={14} /> Target Intel
                                </h3>
                                <div style={{ fontSize: '0.9rem', lineHeight: '1.6' }}>
                                    <div><strong>IP:</strong> {result.ipInfo?.ipAddress}</div>
                                    <div style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}><strong>Loc:</strong> {result.dnsInfo?.serverLocation}</div>
                                    <div><strong>Org:</strong> {result.ipInfo?.organization}</div>
                                </div>
                            </div>

                            <div className="card">
                                <h3 style={{ fontSize: '0.8rem', opacity: 0.7, textTransform: 'uppercase', display: 'flex', alignItems: 'center', gap: '8px' }}>
                                    <Hash size={14} /> Operation ID
                                </h3>
                                <div style={{ fontSize: '1.2rem', fontFamily: 'monospace', color: 'var(--accent)', marginTop: '0.5rem' }}>
                                    {result.codename}
                                </div>
                                <div style={{ fontSize: '0.8rem', opacity: 0.5, marginTop: '0.5rem' }}>
                                    {result.scanTime.split('T')[0]}
                                </div>
                            </div>
                        </div>

                        {/* SSL & Ports Row */}
                        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '20px' }}>
                            <div className="card">
                                <h3 style={{ fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '8px', borderBottom: '1px solid var(--border)', paddingBottom: '10px' }}>
                                    <Lock size={16} /> SSL/TLS Configuration
                                </h3>
                                {result.sslInfo?.valid ? (
                                    <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', fontSize: '0.9rem', marginTop: '10px' }}>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--success)' }}>
                                            <CheckCircle size={16} /> Valid Certificate
                                        </div>
                                        <div><strong>Issuer:</strong> {result.sslInfo.issuer?.split(',')[0]}</div>
                                        <div><strong>Subject:</strong> {result.sslInfo.subject?.split(',')[0]}</div>
                                        <div><strong>Expires:</strong> {result.sslInfo.expiresOn?.split(' ')[0]}</div>
                                    </div>
                                ) : (
                                    <div style={{ color: 'var(--danger)', marginTop: '10px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                                        <AlertTriangle size={16} /> SSL Invalid or Missing
                                    </div>
                                )}
                            </div>

                            <div className="card">
                                <h3 style={{ fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '8px', borderBottom: '1px solid var(--border)', paddingBottom: '10px' }}>
                                    <Radio size={16} /> Open Ports & Services
                                </h3>
                                <div style={{ marginTop: '10px', display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                                    {result.openPorts?.map(port => (
                                        <span key={port} style={{
                                            background: 'rgba(232, 112, 112, 0.15)',
                                            color: '#ff7b72',
                                            border: '1px solid rgba(232, 112, 112, 0.3)',
                                            padding: '2px 8px',
                                            borderRadius: '4px',
                                            fontSize: '0.85rem',
                                            fontFamily: 'monospace'
                                        }}>
                                            {port} / TCP
                                        </span>
                                    ))}
                                    {(!result.openPorts || result.openPorts.length === 0) && (
                                        <div style={{ opacity: 0.6, fontSize: '0.9rem' }}>No common open ports detected (or firewall blocking).</div>
                                    )}
                                </div>
                                {result.techStack && result.techStack.length > 0 && (
                                    <div style={{ marginTop: '15px' }}>
                                        <div style={{ fontSize: '0.8rem', opacity: 0.7, marginBottom: '5px' }}>DETECTED TECHNOLOGY</div>
                                        {result.techStack.map((tech, i) => (
                                            <div key={i} style={{ fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '6px' }}>
                                                <Cpu size={14} /> {tech}
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>
                        </div>

                        {/* Vulnerabilities Table */}
                        <div className="card">
                            <h3 style={{ fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '8px', borderBottom: '1px solid var(--border)', paddingBottom: '10px' }}>
                                <Activity size={16} /> Security Findings
                            </h3>
                            {result.vulnerabilities?.length === 0 ? (
                                <div style={{ padding: '20px', textAlign: 'center', color: 'var(--success)' }}>
                                    <CheckCircle size={32} style={{ marginBottom: '10px' }} />
                                    <div>No vulnerabilities detected in basic scan.</div>
                                </div>
                            ) : (
                                <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px', fontSize: '0.9rem' }}>
                                    <thead>
                                        <tr style={{ textAlign: 'left', borderBottom: '1px solid var(--border)', opacity: 0.7 }}>
                                            <th style={{ padding: '10px' }}>Severity</th>
                                            <th style={{ padding: '10px' }}>Issue</th>
                                            <th style={{ padding: '10px' }}>Remediation</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {result.vulnerabilities.map((v, i) => (
                                            <tr key={i} style={{ borderBottom: '1px solid #30363d' }}>
                                                <td style={{ padding: '12px 10px' }}>
                                                    <span className={`risk-badge ${getSeverityColor(v.severity)}`}>{v.severity}</span>
                                                </td>
                                                <td style={{ padding: '12px 10px' }}>
                                                    <div style={{ fontWeight: 'bold', marginBottom: '4px' }}>{v.type}</div>
                                                    <div style={{ opacity: 0.7, fontSize: '0.85rem' }}>{v.description}</div>
                                                </td>
                                                <td style={{ padding: '12px 10px', fontFamily: 'monospace', color: 'var(--accent)' }}>
                                                    {v.remediation}
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            )}
                        </div>

                        {/* Dark Web Intelligence */}
                        {result.darkWebFindings && result.darkWebFindings.length > 0 && (
                            <div className="card">
                                <h3 style={{ fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '8px', borderBottom: '1px solid var(--border)', paddingBottom: '10px', color: '#d2a8ff' }}>
                                    <Globe size={16} /> Dark Web Intelligence
                                </h3>
                                <div style={{ marginTop: '15px', display: 'flex', flexDirection: 'column', gap: '10px' }}>
                                    {result.darkWebFindings.map((finding, i) => (
                                        <div key={i} style={{
                                            background: 'rgba(210, 168, 255, 0.1)',
                                            borderLeft: '3px solid #d2a8ff',
                                            padding: '10px',
                                            fontSize: '0.9rem'
                                        }}>
                                            <strong>[BREACH DATA]</strong> {finding}
                                        </div>
                                    ))}
                                </div>
                            </div>
                        )}

                        {/* Visual Attack Map */}
                        {result.attackGraph && (
                            <div className="card" style={{ overflowX: 'auto' }}>
                                <h3 style={{ fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '8px', borderBottom: '1px solid var(--border)', paddingBottom: '10px', color: '#ff7b72' }}>
                                    <AlertTriangle size={16} /> Attack Surface Map
                                </h3>
                                <div style={{ marginTop: '20px', minHeight: '300px', background: '#0d1117', borderRadius: '6px', position: 'relative', border: '1px solid #30363d' }}>
                                    <svg width="100%" height="300" style={{ minWidth: '600px' }}>
                                        <defs>
                                            <marker id="arrowhead" markerWidth="10" markerHeight="7" refX="28" refY="3.5" orient="auto">
                                                <polygon points="0 0, 10 3.5, 0 7" fill="#8b949e" />
                                            </marker>
                                        </defs>
                                        {/* Simple Auto-Layout for Visualization */}
                                        {(() => {
                                            const nodes = result.attackGraph?.nodes || [];
                                            const edges = result.attackGraph?.edges || [];

                                            // Assign ranks based on type
                                            const getRank = (type: string) => {
                                                if (type === 'actor') return 0;
                                                if (type === 'device') return 1;
                                                if (type === 'server') return 2;
                                                if (type === 'software') return 3;
                                                if (type === 'exploit') return 3.5; // branching
                                                if (type === 'database') return 4;
                                                return 5;
                                            };

                                            // Calculate positions
                                            const positions: Record<string, { x: number, y: number }> = {};
                                            const levelCounts: Record<number, number> = {};

                                            nodes.forEach(n => {
                                                const rank = getRank(n.type);
                                                levelCounts[rank] = (levelCounts[rank] || 0) + 1;
                                            });

                                            const currentCounts: Record<number, number> = {};
                                            nodes.forEach(n => {
                                                const rank = getRank(n.type);
                                                const count = levelCounts[rank];
                                                const index = currentCounts[rank] || 0;
                                                currentCounts[rank] = index + 1;

                                                positions[n.id] = {
                                                    x: 50 + (rank * 150),
                                                    y: 150 + ((index - (count - 1) / 2) * 80)
                                                };
                                            });

                                            return (
                                                <>
                                                    {/* Edges */}
                                                    {edges.map((e, i) => {
                                                        const start = positions[e.source];
                                                        const end = positions[e.target];
                                                        if (!start || !end) return null;
                                                        return (
                                                            <g key={`edge-${i}`}>
                                                                <line
                                                                    x1={start.x} y1={start.y}
                                                                    x2={end.x} y2={end.y}
                                                                    stroke="#30363d"
                                                                    strokeWidth="2"
                                                                    markerEnd="url(#arrowhead)"
                                                                />
                                                                <text x={(start.x + end.x) / 2} y={(start.y + end.y) / 2 - 5} fill="#8b949e" fontSize="10" textAnchor="middle">{e.label}</text>
                                                            </g>
                                                        );
                                                    })}

                                                    {/* Nodes */}
                                                    {nodes.map((n, i) => {
                                                        const pos = positions[n.id];
                                                        let color = '#58a6ff'; // Default blue
                                                        if (n.type === 'actor') color = '#fff';
                                                        if (n.type === 'exploit') color = '#ff7b72'; // Red
                                                        if (n.type === 'database') color = '#e3b341'; // Yellow

                                                        return (
                                                            <g key={`node-${i}`}>
                                                                <circle cx={pos.x} cy={pos.y} r="25" fill="#161b22" stroke={color} strokeWidth="2" />
                                                                <text x={pos.x} y={pos.y + 40} fill="#c9d1d9" fontSize="12" textAnchor="middle" style={{ fontWeight: 'bold' }}>{n.label}</text>
                                                                <text x={pos.x} y={pos.y + 5} fill={color} fontSize="18" textAnchor="middle" dominantBaseline="middle">
                                                                    {n.type === 'actor' ? '👤' : n.type === 'database' ? '🛢' : n.type === 'exploit' ? '⚡' : n.type === 'server' ? '🖥' : '⚙'}
                                                                </text>
                                                            </g>
                                                        );
                                                    })}
                                                </>
                                            );
                                        })()}
                                    </svg>
                                </div>
                            </div>
                        )}

                        {/* Live Geo-Trace */}
                        {result.geoTrace && (
                            <div className="card" style={{ overflowX: 'auto' }}>
                                <h3 style={{ fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '8px', borderBottom: '1px solid var(--border)', paddingBottom: '10px', color: '#79c0ff' }}>
                                    <Globe size={16} /> Live Network Trace (Texas ➜ Target)
                                </h3>
                                <div style={{ marginTop: '20px', minHeight: '400px', background: '#0d1117', borderRadius: '6px', position: 'relative', border: '1px solid #30363d', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                                    <svg width="800" height="400" viewBox="0 0 800 400" style={{ background: '#0a0c10' }}>
                                        {/* Simple World Map Silhouette (Abstract) */}
                                        <g fill="#21262d">
                                            <path d="M140,100 Q150,80 180,90 T220,120 T260,100 T300,80 T350,110 T400,100 T450,80 T500,100 T550,120 T600,140 T650,120 T700,150 T750,200 L750,300 L50,300 L50,150 Z" opacity="0.2" />
                                            {/* North America */}
                                            <path d="M50,60 L240,60 L200,200 L80,180 Z" />
                                            {/* South America */}
                                            <path d="M180,210 L260,210 L240,350 L190,340 Z" />
                                            {/* Europe/Asia/Africa */}
                                            <path d="M300,60 L750,60 L750,250 L600,320 L400,320 L320,200 L280,100 Z" />
                                            {/* Australia */}
                                            <path d="M620,260 L740,260 L720,340 L640,330 Z" />
                                        </g>

                                        {/* Grid Lines */}
                                        <g stroke="#30363d" strokeWidth="0.5" opacity="0.3">
                                            <line x1="0" y1="100" x2="800" y2="100" />
                                            <line x1="0" y1="200" x2="800" y2="200" />
                                            <line x1="0" y1="300" x2="800" y2="300" />
                                            <line x1="200" y1="0" x2="200" y2="400" />
                                            <line x1="400" y1="0" x2="400" y2="400" />
                                            <line x1="600" y1="0" x2="600" y2="400" />
                                        </g>

                                        {/* Connection Lines */}
                                        <polyline
                                            points={result.geoTrace.map(h => {
                                                // Simple Mercator Projection Shim
                                                const x = (h.longitude + 180) * (800 / 360);
                                                const latRad = h.latitude * Math.PI / 180;
                                                const mercN = Math.log(Math.tan((Math.PI / 4) + (latRad / 2)));
                                                const y = (400 / 2) - (800 * mercN / (2 * Math.PI));
                                                return `${x},${y}`;
                                            }).join(' ')}
                                            fill="none"
                                            stroke="#58a6ff"
                                            strokeWidth="2"
                                            strokeDasharray="5,5"
                                            opacity="0.6"
                                        />

                                        {/* Hops */}
                                        {result.geoTrace.map((h, i) => {
                                            // Simple Mercator Projection Shim
                                            const x = (h.longitude + 180) * (800 / 360);
                                            const latRad = h.latitude * Math.PI / 180;
                                            const mercN = Math.log(Math.tan((Math.PI / 4) + (latRad / 2)));
                                            const y = (400 / 2) - (800 * mercN / (2 * Math.PI));

                                            return (
                                                <g key={i}>
                                                    <circle cx={x} cy={y} r="4" fill={i === 0 ? '#7ee787' : i === result.geoTrace!.length - 1 ? '#ff7b72' : '#58a6ff'} />
                                                    <circle cx={x} cy={y} r="8" fill={i === 0 ? '#7ee787' : i === result.geoTrace!.length - 1 ? '#ff7b72' : '#58a6ff'} opacity="0.3">
                                                        <animate attributeName="r" values="4;12;4" dur="2s" repeatCount="indefinite" />
                                                        <animate attributeName="opacity" values="0.6;0;0.6" dur="2s" repeatCount="indefinite" />
                                                    </circle>
                                                    <text x={x} y={y - 12} fill="#c9d1d9" fontSize="10" textAnchor="middle" style={{ textShadow: '0 1px 4px #000' }}>
                                                        {h.location.split(',')[0]}
                                                    </text>
                                                    <text x={x} y={y + 15} fill="#8b949e" fontSize="9" textAnchor="middle">
                                                        {h.ip}
                                                    </text>
                                                </g>
                                            );
                                        })}
                                    </svg>
                                </div>
                            </div>
                        )}

                        {/* Advanced Deep Recon (Hacker View) */}
                        {result.advancedRecon && (
                            <div className="card" style={{ border: '1px solid #d2a8ff', background: '#0d1117' }}>
                                <h3 style={{ fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '8px', borderBottom: '1px solid #d2a8ff', paddingBottom: '10px', color: '#d2a8ff', fontFamily: 'monospace' }}>
                                    <Terminal size={16} /> DEEP RECONNAISSANCE MODE (OSINT)
                                </h3>
                                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginTop: '20px' }}>

                                    {/* Subdomains */}
                                    <div>
                                        <div style={{ color: '#7ee787', fontSize: '0.85rem', marginBottom: '10px', textTransform: 'uppercase' }}>>> Subdomain Enumeration</div>
                                        <div style={{ background: '#0a0c10', padding: '10px', borderRadius: '4px', border: '1px solid #30363d', fontFamily: 'monospace', fontSize: '0.85rem', color: '#8b949e', height: '150px', overflowY: 'auto' }}>
                                            {result.advancedRecon.subdomains.map((sub, i) => (
                                                <div key={i}>[+] {sub}</div>
                                            ))}
                                            {result.advancedRecon.subdomains.length === 0 && <div>No subdomains found.</div>}
                                        </div>
                                    </div>

                                    {/* Cloud Assets */}
                                    <div>
                                        <div style={{ color: '#e3b341', fontSize: '0.85rem', marginBottom: '10px', textTransform: 'uppercase' }}>>> Local Cloud Leaks (S3/Blobs)</div>
                                        <div style={{ background: '#0a0c10', padding: '10px', borderRadius: '4px', border: '1px solid #30363d', fontFamily: 'monospace', fontSize: '0.85rem', color: '#e3b341', height: '150px', overflowY: 'auto' }}>
                                            {result.advancedRecon.cloudAssets.map((asset, i) => (
                                                <div key={i}>{asset}</div>
                                            ))}
                                        </div>
                                    </div>

                                    {/* Emails */}
                                    <div>
                                        <div style={{ color: '#ff7b72', fontSize: '0.85rem', marginBottom: '10px', textTransform: 'uppercase' }}>>> Harvested Credentials (Emails)</div>
                                        <div style={{ background: '#0a0c10', padding: '10px', borderRadius: '4px', border: '1px solid #30363d', fontFamily: 'monospace', fontSize: '0.85rem', color: '#ff7b72', height: '150px', overflowY: 'auto' }}>
                                            {result.advancedRecon.leakedEmails.map((email, i) => (
                                                <div key={i}>[!] {email}</div>
                                            ))}
                                        </div>
                                    </div>

                                    {/* WAF & Registrar */}
                                    <div>
                                        <div style={{ color: '#79c0ff', fontSize: '0.85rem', marginBottom: '10px', textTransform: 'uppercase' }}>>> Infrastructure Intelligence</div>
                                        <div style={{ background: '#0a0c10', padding: '10px', borderRadius: '4px', border: '1px solid #30363d', fontFamily: 'monospace', fontSize: '0.85rem', color: '#c9d1d9', height: '150px', overflowY: 'auto' }}>
                                            <div style={{ marginBottom: '10px' }}>
                                                <span style={{ color: '#8b949e' }}>WAF Status: </span>
                                                <span style={{ color: result.advancedRecon.wafStatus.includes('Detected') ? '#e3b341' : '#79c0ff' }}>{result.advancedRecon.wafStatus}</span>
                                            </div>
                                            <div>
                                                <span style={{ color: '#8b949e' }}>Registrar: </span>
                                                <span style={{ color: '#79c0ff' }}>{result.advancedRecon.whoisRegistrar}</span>
                                            </div>
                                        </div>
                                    </div>

                                </div>
                            </div>
                        )}

                    </div>
                )}
            </div>
        </div >
    );
}

export default ScanPage;
