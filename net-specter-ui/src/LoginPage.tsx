import { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import { Shield, Mail, Lock, Eye, EyeOff } from 'lucide-react';
import './App.css';

function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        try {
            // FORCE CORRECT URL - Bypass environment variables
            const apiUrl = 'https://net-specter-1.onrender.com';

            const response = await axios.post(`${apiUrl}/api/auth/authenticate`, {
                email,
                password
            });

            localStorage.setItem('token', response.data.token);
            navigate('/');
        } catch (err: any) {
            if (err.response?.data?.error) {
                setError(err.response.data.error);
            } else {
                setError('Login failed. Please check your credentials and try again.');
            }
        }
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100vh', gap: '2rem' }}>
            <div style={{ textAlign: 'center' }}>
                <Shield size={64} color="#58a6ff" style={{ margin: '0 auto' }} />
                <h1 style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>NetSpecter</h1>
                <p style={{ opacity: 0.7 }}>Secure Access Portal</p>
            </div>

            <div className="card" style={{ width: '100%', maxWidth: '400px', padding: '2rem' }}>
                <h2 style={{ marginTop: 0, textAlign: 'center', marginBottom: '1.5rem' }}>Sign In</h2>

                {error && <div style={{ color: 'var(--danger)', marginBottom: '1rem', textAlign: 'center' }}>{error}</div>}

                <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    <div style={{ position: 'relative' }}>
                        <Mail size={18} style={{ position: 'absolute', top: '14px', left: '12px', opacity: 0.5 }} />
                        <input
                            type="email"
                            placeholder="Email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            style={{ width: '100%', paddingLeft: '40px', boxSizing: 'border-box' }}
                            required
                        />
                    </div>

                    <div style={{ position: 'relative' }}>
                        <Lock size={18} style={{ position: 'absolute', top: '14px', left: '12px', opacity: 0.5 }} />
                        <input
                            type={showPassword ? "text" : "password"}
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            style={{ width: '100%', paddingLeft: '40px', paddingRight: '40px', boxSizing: 'border-box' }}
                            required
                        />
                        <button
                            type="button"
                            onClick={() => setShowPassword(!showPassword)}
                            style={{
                                position: 'absolute',
                                top: '12px',
                                right: '12px',
                                background: 'none',
                                border: 'none',
                                cursor: 'pointer',
                                padding: '0',
                                opacity: 0.5
                            }}
                        >
                            {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                        </button>
                    </div>

                    <button type="submit" style={{ marginTop: '1rem' }}>Access Console</button>
                </form>

                <div style={{ textAlign: 'center', marginTop: '1rem', fontSize: '0.9rem' }}>
                    <Link to="/forgot-password" style={{ color: 'var(--accent)', opacity: 0.8 }}>Forgot password?</Link>
                </div>

                <div style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '0.9rem' }}>
                    New operative? <Link to="/signup" style={{ color: 'var(--accent)' }}>Initialize identity</Link>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;
