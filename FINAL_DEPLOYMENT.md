# 🚀 NetSpecter - Final Live Deployment Guide

## ✅ Current Status
All code is pushed to GitHub and ready for live deployment. **No localhost references remain in the code.**

---

## 📋 Render Configuration (Step-by-Step)

### 🔧 Step 1: Deploy Backend Service

1. Go to [Render Dashboard](https://dashboard.render.com/)
2. Click **"New +" → "Web Service"**
3. Connect to `gajjela521/net-specter`
4. Configure:

```yaml
Name: net-specter-backend
Environment: Docker
Root Directory: backend
Dockerfile Path: Dockerfile
Branch: main
Region: Oregon (or your preferred region)
```

5. **Environment Variables** (Click "Add Environment Variable"):

```bash
# Required for OAuth (Get from Google Cloud Console)
GOOGLE_CLIENT_ID=your_google_client_id_here
GOOGLE_CLIENT_SECRET=your_google_client_secret_here

# Will be set after frontend is deployed
FRONTEND_URL=https://net-specter-ui.onrender.com
```

6. Click **"Create Web Service"**
7. **Wait for deployment** (5-10 minutes)
8. **Copy the backend URL**: `https://net-specter-backend-XXXX.onrender.com`

---

### 🎨 Step 2: Deploy Frontend Service

1. Click **"New +" → "Web Service"** (NOT Static Site)
2. Connect to `gajjela521/net-specter` again
3. Configure:

```yaml
Name: net-specter-ui
Environment: Node
Root Directory: net-specter-ui
Build Command: npm install; npm run build
Start Command: npm start
Branch: main
Region: Oregon (same as backend)
```

4. **Environment Variable**:

```bash
# Use your actual backend URL from Step 1
VITE_API_URL=https://net-specter-backend-XXXX.onrender.com
```

5. Click **"Create Web Service"**
6. **Wait for deployment** (3-5 minutes)
7. **Copy the frontend URL**: `https://net-specter-ui-XXXX.onrender.com`

---

### 🔄 Step 3: Update Backend with Frontend URL

1. Go back to **net-specter-backend** service
2. Click **"Environment"** tab
3. Update `FRONTEND_URL`:

```bash
FRONTEND_URL=https://net-specter-ui-XXXX.onrender.com
```

4. Click **"Save Changes"** (triggers automatic redeploy)

---

### 🔐 Step 4: Configure Google OAuth (Optional)

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create/Select a project
3. Navigate to **"APIs & Services" → "Credentials"**
4. Click **"Create Credentials" → "OAuth 2.0 Client ID"**
5. Configure:

```yaml
Application Type: Web application
Name: NetSpecter
Authorized JavaScript origins:
  - https://net-specter-ui-XXXX.onrender.com
Authorized redirect URIs:
  - https://net-specter-backend-XXXX.onrender.com/login/oauth2/code/google
```

6. Copy **Client ID** and **Client Secret**
7. Add to backend environment variables in Render
8. Save (triggers redeploy)

---

## 🧪 Testing Your Live Application

### 1. Access the Application
Visit: `https://net-specter-ui-XXXX.onrender.com`

**Expected**: Login page loads

### 2. Create an Account
1. Click **"Initialize identity"**
2. Enter email and password
3. Click **"Register"**

**Expected**: Redirects to login page

### 3. Login
1. Enter your credentials
2. Click **"Access Console"**

**Expected**: Dashboard loads with scan interface

### 4. Perform a Scan
1. Enter target: `google.com`
2. Click **"Launch Scan"**

**Expected**: Scan results appear with threat score

### 5. Test Google OAuth (if configured)
1. Logout
2. Click **"Sign in with Google"**
3. Complete Google authentication

**Expected**: Redirects back to dashboard

### 6. Test Health Endpoint
Visit: `https://net-specter-backend-XXXX.onrender.com/actuator/health`

**Expected**: JSON response with status "UP"

### 7. Test Metrics
Visit: `https://net-specter-backend-XXXX.onrender.com/actuator/metrics`

**Expected**: JSON with available metrics

---

## 🎯 All Live Endpoints

### Frontend
- **Main App**: `https://net-specter-ui-XXXX.onrender.com`
- **Login**: `https://net-specter-ui-XXXX.onrender.com/login`
- **Signup**: `https://net-specter-ui-XXXX.onrender.com/signup`

### Backend API
- **Health Check**: `https://net-specter-backend-XXXX.onrender.com/actuator/health`
- **Metrics**: `https://net-specter-backend-XXXX.onrender.com/actuator/metrics`
- **Prometheus**: `https://net-specter-backend-XXXX.onrender.com/actuator/prometheus`
- **Scan API**: `https://net-specter-backend-XXXX.onrender.com/api/scan?target=google.com`
- **Register**: `https://net-specter-backend-XXXX.onrender.com/api/auth/register`
- **Login**: `https://net-specter-backend-XXXX.onrender.com/api/auth/authenticate`
- **Google OAuth**: `https://net-specter-backend-XXXX.onrender.com/oauth2/authorization/google`

---

## ✨ Enterprise Features Active

### Security
- ✅ JWT Authentication
- ✅ OAuth2 (Google)
- ✅ Rate Limiting (100 req/min per IP)
- ✅ Secure session cookies
- ✅ CORS protection

### Monitoring
- ✅ Health checks (`/actuator/health`)
- ✅ Metrics (`/actuator/metrics`)
- ✅ Prometheus export (`/actuator/prometheus`)
- ✅ Request/Response logging
- ✅ Distributed tracing (Request IDs)

### Performance
- ✅ Stateless architecture
- ✅ Connection pooling
- ✅ In-memory caching
- ✅ Optimized Docker builds

---

## 🐛 Troubleshooting

### Frontend shows "Blocked request"
**Status**: ✅ Fixed in latest code
**Solution**: Already configured in `vite.config.ts`

### Backend returns 403 on scan
**Status**: ✅ Fixed in latest code
**Solution**: `/api/scan` is now public

### Google OAuth not working
**Check**:
1. Redirect URI matches exactly in Google Console
2. `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` are set
3. `FRONTEND_URL` is correct in backend

### Can't connect to backend
**Check**:
1. `VITE_API_URL` in frontend matches backend URL
2. Backend service is running (check Render logs)
3. No typos in URLs

### Rate limit errors
**Normal**: You're making >100 requests/minute
**Solution**: Wait 1 minute or adjust limit in `RateLimitingFilter.java`

---

## 📊 Monitoring Your Application

### Grafana Dashboard (Optional)
1. Set up Grafana Cloud (free tier)
2. Add Prometheus data source
3. Point to: `https://net-specter-backend-XXXX.onrender.com/actuator/prometheus`
4. Import dashboard for Spring Boot applications

### Render Logs
- View real-time logs in Render Dashboard
- Filter by service (backend/frontend)
- Search for errors or specific request IDs

---

## 🎉 Success Criteria

Your application is **LIVE** when:

- ✅ Frontend loads without errors
- ✅ Can register new users
- ✅ Can login with email/password
- ✅ Can perform security scans
- ✅ Google OAuth works (if configured)
- ✅ Health endpoint returns "UP"
- ✅ No localhost references anywhere

---

## 📝 Final Checklist

- [ ] Backend deployed on Render
- [ ] Frontend deployed on Render
- [ ] `VITE_API_URL` set in frontend
- [ ] `FRONTEND_URL` set in backend
- [ ] Google OAuth configured (optional)
- [ ] Tested registration flow
- [ ] Tested login flow
- [ ] Tested scan functionality
- [ ] Verified health endpoint
- [ ] Checked logs for errors

---

## 🚀 You're Live!

**Share your application**:
- Frontend: `https://net-specter-ui-XXXX.onrender.com`
- API Docs: See `ENTERPRISE_FEATURES.md`
- GitHub: `https://github.com/gajjela521/net-specter`

**All flows are working with cloud URLs - no localhost anywhere!** 🎊
