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
                <div style={{ display: 'flex', gap: '1rem' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', background: 'rgba(35, 134, 54, 0.2)', padding: '0.5rem 1rem', borderRadius: '4px', border: '1px solid var(--success-border)', color: 'var(--success)' }}>
                        <div style={{ width: '8px', height: '8px', borderRadius: '50%', background: 'var(--success)', boxShadow: '0 0 8px var(--success)' }}></div>
                        System Operational
                    </div>
                    <button onClick={handleLogout} style={{ background: 'var(--panel-bg)', border: '1px solid var(--border)', color: 'white', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <LogOut size={16} /> Logout
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
                            {logs.filter(l => l.includes("[STAGE:NETWORK]") || (!l.includes("[STAGE:") && logs.indexOf(l) < logs.findIndex(x => x.includes("[STAGE:APP]")) && logs.indexOf(l) >= 0)).map((log, i) => (
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

                    </div>
                )}
            </div>
        </div>
    );
}

export default ScanPage;
