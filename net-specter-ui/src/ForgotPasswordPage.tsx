import { useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { Shield, Mail, ArrowLeft } from 'lucide-react';
import './App.css';

function ForgotPasswordPage() {
    const [email, setEmail] = useState('');
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setMessage('');

        try {
            let apiUrl = import.meta.env.VITE_API_URL || 'https://net-specter-1.onrender.com';
            if (!apiUrl.startsWith('http')) apiUrl = `https://${apiUrl}`;

            const response = await axios.post(`${apiUrl}/api/auth/forgot-password`, {
                email
            });

            setMessage(response.data.message || 'Password reset instructions sent!');
        } catch (err: any) {
            setError('Failed to process request. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100vh', gap: '2rem' }}>
            <div style={{ textAlign: 'center' }}>
                <Shield size={64} color="#58a6ff" style={{ margin: '0 auto' }} />
                <h1 style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>NetSpecter</h1>
                <p style={{ opacity: 0.7 }}>Password Recovery</p>
            </div>

            <div className="card" style={{ width: '100%', maxWidth: '400px', padding: '2rem' }}>
                <h2 style={{ marginTop: 0, textAlign: 'center', marginBottom: '1.5rem' }}>Forgot Password</h2>

                {error && <div style={{ color: 'var(--danger)', marginBottom: '1rem', textAlign: 'center', padding: '0.75rem', background: 'rgba(220, 38, 38, 0.1)', borderRadius: '4px' }}>{error}</div>}
                {message && <div style={{ color: 'var(--success)', marginBottom: '1rem', textAlign: 'center', padding: '0.75rem', background: 'rgba(34, 197, 94, 0.1)', borderRadius: '4px' }}>{message}</div>}

                <p style={{ textAlign: 'center', opacity: 0.8, marginBottom: '1.5rem', fontSize: '0.9rem' }}>
                    Enter your email address and we'll send you instructions to reset your password.
                </p>

                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    <div style={{ position: 'relative' }}>
                        <Mail size={18} style={{ position: 'absolute', top: '14px', left: '12px', opacity: 0.5 }} />
                        <input
                            type="email"
                            placeholder="Your Email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            style={{ width: '100%', paddingLeft: '40px', boxSizing: 'border-box' }}
                            required
                        />
                    </div>

                    <button type="submit" style={{ marginTop: '1rem' }} disabled={loading}>
                        {loading ? 'Sending...' : 'Send Reset Instructions'}
                    </button>
                </form>

                <div style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '0.9rem' }}>
                    <Link to="/login" style={{ color: 'var(--accent)', display: 'inline-flex', alignItems: 'center', gap: '0.5rem' }}>
                        <ArrowLeft size={16} />
                        Back to Login
                    </Link>
                </div>
            </div>
        </div>
    );
}

export default ForgotPasswordPage;
