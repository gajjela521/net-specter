# 🔧 Registration Troubleshooting Guide

## Issue: "Registration failed. Please try again."

### Possible Causes:

1. **Frontend hasn't redeployed yet with new features**
2. **Backend CORS issue**
3. **Backend not running**
4. **Wrong backend URL**

---

## ✅ Quick Fixes:

### 1. Check Render Deployment Status

**Frontend (net-specter-ui):**
1. Go to https://dashboard.render.com/
2. Click on `net-specter-ui` service
3. Check if it's deploying the latest commit `4005d32`
4. Wait for "Live" status (green)

**Backend (net-specter-backend):**
1. Click on `net-specter-1` service  
2. Check if it's running
3. Verify the URL matches your `VITE_API_URL`

### 2. Verify Environment Variables

**In `net-specter-ui` service:**
```bash
VITE_API_URL=https://net-specter-1.onrender.com
```

**In `net-specter-backend` service:**
```bash
FRONTEND_URL=https://net-specter-ui.onrender.com
```

### 3. Test Backend Directly

Open your browser console and run:

```javascript
fetch('https://net-specter-1.onrender.com/api/auth/register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'test@example.com',
    password: 'Test123!'
  })
})
.then(r => r.json())
.then(console.log)
.catch(console.error)
```

**Expected responses:**
- ✅ Success: `{token: "...", email: "test@example.com"}`
- ❌ Email exists: `{error: "Email already registered..."}`
- ❌ CORS error: Check backend CORS configuration

---

## 🎯 What Should Happen After Latest Deployment:

### Signup Page Features:
1. ✅ Email input
2. ✅ Password input with **eye icon** to show/hide
3. ✅ **Confirm Password** input with eye icon
4. ✅ Passwords must match validation
5. ✅ Clear error messages

### Current Issue:
Your screenshot shows the **old version** without:
- ❌ No "Confirm Password" field
- ❌ No eye icon to show/hide password

This means **the frontend hasn't updated yet**.

---

## 🚀 Force Redeploy (If Needed):

If Render didn't auto-deploy:

1. Go to `net-specter-ui` service
2. Click **"Manual Deploy"** button
3. Select **"Deploy latest commit"**
4. Wait 2-3 minutes for deployment

---

## 🔍 Check Browser Console for Errors:

1. Open your browser DevTools (F12)
2. Go to **Console** tab
3. Try to register again
4. Look for errors like:
   - `CORS policy` → Backend CORS issue
   - `Failed to fetch` → Backend not running
   - `404 Not Found` → Wrong URL
   - `400 Bad Request` → Check error message

---

## 📊 Expected vs Current State:

### Current (Old Version):
```
Email: [input]
Password: [input] (no eye icon)
[Register button]
```

### Expected (New Version):
```
Email: [input]
Password: [input] 👁️ (eye icon to toggle)
Confirm Password: [input] 👁️ (eye icon to toggle)
[Register button]
```

---

## 🎯 Immediate Action:

**Option 1: Wait for Auto-Deploy**
- Render should auto-deploy in 1-2 minutes
- Refresh the page after deployment completes

**Option 2: Manual Deploy**
- Go to Render Dashboard
- Click `net-specter-ui`
- Click "Manual Deploy" → "Deploy latest commit"
- Wait for completion
- Refresh your browser

**Option 3: Test Locally First**
```bash
cd net-specter/net-specter-ui
npm install
npm run dev
```
Then visit http://localhost:5173/signup

---

## 🔐 Backend Compatibility:

The NetSpecter frontend is designed to work with **any backend** that has these endpoints:

- `POST /api/auth/register` → `{email, password}` → `{token, email}`
- `POST /api/auth/authenticate` → `{email, password}` → `{token, email}`
- `POST /api/auth/forgot-password` → `{email}` → `{message}`

**Your Nexus AI backend needs to have these endpoints implemented.**

If the Nexus backend doesn't have the NetSpecter auth endpoints, you have two options:

1. **Deploy the NetSpecter backend** separately on Render
2. **Add the auth endpoints** to your Nexus backend

---

## ✅ Verification Checklist:

- [ ] Frontend shows "Confirm Password" field
- [ ] Eye icons appear on password fields
- [ ] Clicking eye icon shows/hides password
- [ ] Error message is specific (not generic)
- [ ] Backend URL is correct in environment variables
- [ ] Backend is running and accessible
- [ ] CORS allows your frontend domain

---

## 🆘 Still Not Working?

**Check these:**

1. **Clear browser cache**: Ctrl+Shift+R (hard refresh)
2. **Check Render logs**: View backend logs for errors
3. **Verify backend code**: Is the NetSpecter backend deployed?
### 4. Curl Test
If browser fails, try terminal:

```bash
curl -X POST https://net-specter-1.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!"}'
```

---

## 📝 Summary:

**The code is correct and deployed to GitHub.**

**The issue is:** Your browser is showing the **old frontend** that hasn't redeployed yet.

**Solution:** Wait for Render to deploy commit `4005d32` or manually trigger deployment.

Once deployed, you'll see:
- ✅ Password confirmation field
- ✅ Show/hide password toggles
- ✅ Better error messages
