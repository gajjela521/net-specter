# NetSpecter Deployment Guide

## Manual Deployment on Render

Since the Blueprint approach is encountering validation issues, follow these steps to deploy manually:

### 1. Deploy Backend (Java Spring Boot)

1. Go to [Render Dashboard](https://dashboard.render.com/)
2. Click **"New +"** → **"Web Service"**
3. Connect your GitHub repository: `gajjela521/net-specter`
4. Configure the service:
   - **Name**: `net-specter-backend`
   - **Region**: Oregon (US West)
   - **Branch**: `main`
   - **Root Directory**: `backend`
   - **Environment**: `Docker`
   - **Dockerfile Path**: `backend/Dockerfile`
   - **Plan**: Free
5. Add Environment Variables:
   - `GOOGLE_CLIENT_ID`: (Get from Google Cloud Console)
   - `GOOGLE_CLIENT_SECRET`: (Get from Google Cloud Console)
   - `FRONTEND_URL`: (Will be your frontend URL after step 2)
6. Click **"Create Web Service"**
7. **Copy the service URL** (e.g., `https://net-specter-backend.onrender.com`)

### 2. Deploy Frontend (React Static Site)

1. Go to [Render Dashboard](https://dashboard.render.com/)
2. Click **"New +"** → **"Static Site"**
3. Connect your GitHub repository: `gajjela521/net-specter`
4. Configure the site:
   - **Name**: `net-specter-ui`
   - **Region**: Oregon (US West)
   - **Branch**: `main`
   - **Root Directory**: `net-specter-ui`
   - **Build Command**: `npm install && npm run build`
   - **Publish Directory**: `dist`
   - **Plan**: Free
5. Add Environment Variable:
   - **Key**: `VITE_API_URL`
   - **Value**: `https://net-specter-backend.onrender.com` (from step 1)
6. Click **"Create Static Site"**

### 3. Update Backend Environment

1. Go back to your backend service in Render
2. Navigate to **"Environment"** tab
3. Update `FRONTEND_URL` to your frontend URL (e.g., `https://net-specter-ui.onrender.com`)
4. Click **"Save Changes"** (this will trigger a redeploy)

### 4. Configure Google OAuth2 (Optional)

If you want to enable "Sign in with Google":

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable **Google+ API**
4. Go to **Credentials** → **Create Credentials** → **OAuth 2.0 Client ID**
5. Configure:
   - **Application Type**: Web application
   - **Authorized redirect URIs**: 
     - `https://net-specter-backend.onrender.com/login/oauth2/code/google`
6. Copy the **Client ID** and **Client Secret**
7. Add them to your backend service environment variables in Render

### 5. Access Your Application

Once both services are deployed:
- **Frontend URL**: `https://net-specter-ui.onrender.com`
- **Backend API**: `https://net-specter-backend.onrender.com/api`

**First-time access**:
1. Visit the frontend URL
2. You'll be redirected to `/login`
3. Click **"Initialize identity"** to create an account
4. Sign up with email/password
5. Start scanning targets!

---

## Local Development

### Backend
```bash
cd backend
./mvnw spring-boot:run
```
Access at: `http://localhost:8080`

### Frontend
```bash
cd net-specter-ui
npm install
npm run dev
```
Access at: `http://localhost:5173`

---

## Troubleshooting

### Backend won't start
- Check logs in Render dashboard
- Ensure Dockerfile builds successfully locally: `cd backend && docker build -t test .`

### Frontend can't connect to backend
- Verify `VITE_API_URL` is set correctly in Render
- Check CORS settings in `SecurityConfiguration.java`

### OAuth2 not working
- Verify redirect URI matches exactly in Google Console
- Ensure `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` are set
- Check that `FRONTEND_URL` is correct for the callback redirect
