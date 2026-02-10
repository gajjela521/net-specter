# Render Service Configuration Reference

## Backend Service (Web Service - Docker)

**Service Type**: Web Service  
**Name**: `net-specter-backend`  
**Environment**: Docker  
**Region**: Oregon (US West) or any region  
**Branch**: `main`  

### Important Settings:
- **Root Directory**: `backend`
- **Dockerfile Path**: `Dockerfile` (relative to backend directory)
- **Docker Context**: Leave empty or set to `.`
- **Docker Build Context**: Leave as default

### Environment Variables:
```
GOOGLE_CLIENT_ID=your_google_client_id_here
GOOGLE_CLIENT_SECRET=your_google_client_secret_here
FRONTEND_URL=https://your-frontend-url.onrender.com
```

---

## Frontend Service (Static Site)

**Service Type**: Static Site  
**Name**: `net-specter-ui`  
**Region**: Oregon (US West) or any region  
**Branch**: `main`  

### Important Settings:
- **Root Directory**: `net-specter-ui`
- **Build Command**: `npm install && npm run build`
- **Publish Directory**: `dist`

### Environment Variables:
```
VITE_API_URL=https://your-backend-url.onrender.com
```

---

## Deployment Order

1. **Deploy Backend First** - Get the backend URL
2. **Deploy Frontend** - Use backend URL in VITE_API_URL
3. **Update Backend** - Add frontend URL to FRONTEND_URL env var

---

## Common Issues

### "Root directory does not exist"
- Verify Root Directory is set to `backend` for backend service
- Verify Root Directory is set to `net-specter-ui` for frontend service

### "Dockerfile not found"
- Ensure Dockerfile Path is just `Dockerfile` (not `backend/Dockerfile`)
- Root Directory should already be `backend`

### Build fails with "npm not found"
- Make sure you selected "Static Site" for frontend (not "Web Service")
