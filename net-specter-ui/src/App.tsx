import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import ScanPage from './ScanPage';
import LoginPage from './LoginPage';
import SignupPage from './SignupPage';
import OAuth2Callback from './OAuth2Callback';
import ForgotPasswordPage from './ForgotPasswordPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />
        <Route path="/oauth/callback" element={<OAuth2Callback />} />
        <Route path="/" element={<ScanPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
