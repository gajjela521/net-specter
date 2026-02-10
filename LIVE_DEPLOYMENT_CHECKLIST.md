# NetSpecter Live Deployment Checklist

## ✅ Backend Service Configuration

### Service Settings
- [ ] **Service Name**: `net-specter-backend`
- [ ] **Service Type**: Web Service
- [ ] **Environment**: Docker
- [ ] **Root Directory**: `backend`
- [ ] **Dockerfile Path**: `Dockerfile`
- [ ] **Branch**: `main`

### Environment Variables (REQUIRED)
```
PORT=8080
GOOGLE_CLIENT_ID=your_actual_google_client_id
GOOGLE_CLIENT_SECRET=your_actual_google_client_secret
FRONTEND_URL=https://net-specter-ui.onrender.com
```

**Note**: Get Google OAuth credentials from [Google Cloud Console](https://console.cloud.google.com/):
1. Create OAuth 2.0 Client ID
2. Add authorized redirect URI: `https://YOUR-BACKEND-URL.onrender.com/login/oauth2/code/google`

---

## ✅ Frontend Service Configuration

### Service Settings
- [ ] **Service Name**: `net-specter-ui`
- [ ] **Service Type**: Web Service (NOT Static Site - we're using Vite preview)
- [ ] **Root Directory**: `net-specter-ui`
- [ ] **Branch**: `main`

### Build & Start Commands
- [ ] **Build Command**: `npm install; npm run build`
- [ ] **Start Command**: `npm start`

### Environment Variables (REQUIRED)
```
VITE_API_URL=https://net-specter-backend.onrender.com
```
Replace with your actual backend URL from Render.

---

## 🔄 Deployment Flow

### Step 1: Deploy Backend First
1. Create backend web service in Render
2. Wait for it to deploy successfully
3. **Copy the backend URL** (e.g., `https://net-specter-backend.onrender.com`)

### Step 2: Configure Google OAuth (Optional but Recommended)
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create/Select project
3. Enable Google+ API
4. Create OAuth 2.0 credentials
5. Add redirect URI: `https://YOUR-BACKEND-URL.onrender.com/login/oauth2/code/google`
6. Copy Client ID and Secret
7. Add to backend environment variables in Render

### Step 3: Deploy Frontend
1. Create frontend web service in Render
2. Set `VITE_API_URL` to your backend URL
3. Wait for deployment
4. **Copy the frontend URL** (e.g., `https://net-specter-ui.onrender.com`)

### Step 4: Update Backend with Frontend URL
1. Go back to backend service in Render
2. Add/Update `FRONTEND_URL` environment variable
3. Set it to your frontend URL
4. Save (this will trigger a redeploy)

---

## 🧪 Testing the Live Application

### 1. Test Frontend Access
- Visit: `https://net-specter-ui.onrender.com`
- Should see: Login page
- ✅ If you see login page, frontend is working!

### 2. Test Backend API
- Visit: `https://net-specter-backend.onrender.com/api/scan?target=google.com`
- Should see: JSON response with scan results
- ✅ If you see JSON, backend is working!

### 3. Test Registration
1. Click "Initialize identity" on login page
2. Enter email and password
3. Click "Register"
4. Should redirect to login
5. ✅ If registration works, database is working!

### 4. Test Login
1. Enter your registered email/password
2. Click "Access Console"
3. Should see the scan dashboard
4. ✅ If you see dashboard, JWT auth is working!

### 5. Test Scanning
1. Enter a target (e.g., `google.com`)
2. Click "Launch Scan"
3. Should see scan results with threat score
4. ✅ If scan works, full flow is operational!

### 6. Test Google OAuth (if configured)
1. Click "Sign in with Google"
2. Should redirect to Google login
3. After login, should redirect back to dashboard
4. ✅ If OAuth works, full authentication is operational!

---

## 🐛 Common Issues & Fixes

### "Blocked request" error
- **Fix**: Already fixed in latest code (vite.config.ts has allowedHosts)
- **Verify**: Latest commit is deployed

### "Cannot connect to backend"
- **Fix**: Check `VITE_API_URL` in frontend environment variables
- **Verify**: Should be `https://YOUR-BACKEND-URL.onrender.com` (no trailing slash)

### "403 Forbidden" on scan
- **Fix**: Already fixed - `/api/scan` is now public
- **Verify**: Latest backend code is deployed

### Google OAuth not working
- **Fix**: Verify redirect URI in Google Console matches exactly
- **Format**: `https://YOUR-BACKEND-URL.onrender.com/login/oauth2/code/google`
- **Verify**: `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` are set

### Backend won't start
- **Check**: Render logs for errors
- **Common**: Missing environment variables
- **Fix**: Ensure all required env vars are set

---

## 📊 Final Verification

Once everything is deployed:

1. ✅ Frontend loads at your Render URL
2. ✅ Can register a new account
3. ✅ Can login with email/password
4. ✅ Can perform scans and see results
5. ✅ Google OAuth works (if configured)
6. ✅ Logout redirects to login page

**All systems operational! 🚀**

---

## 📝 URLs to Share

- **Live Application**: `https://net-specter-ui.onrender.com`
- **API Documentation**: `https://net-specter-backend.onrender.com/api`
- **GitHub Repository**: `https://github.com/gajjela521/net-specter`
