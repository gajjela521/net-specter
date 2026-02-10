# 🧪 API Testing with Swagger UI

## Overview
Swagger UI provides an interactive interface to test your API endpoints directly from the browser, bypassing the frontend. This is excellent for debugging "Registration failed" or authentication issues.

## 🔗 Accessing Swagger UI

Once deployed, access the Swagger UI at:

**[https://net-specter-1.onrender.com/swagger-ui.html](https://net-specter-1.onrender.com/swagger-ui.html)**
*(or sometimes `/swagger-ui/index.html` depending on the redirect)*

## 🛠️ Testing Registration

1.  **Open Swagger UI** in your browser.
2.  Locate the **Authentication Controller** section (usually `AuthController` or `auth-controller`).
3.  Find the `POST /api/auth/register` endpoint.
4.  Click **"Try it out"**.
5.  In the **Request body** JSON, enter your details:
    ```json
    {
      "email": "testuser_swagger@example.com",
      "password": "StrongPassword123!"
    }
    ```
6.  Click **"Execute"**.

### Expected Responses:
-   **200 OK**: Registration successful. You will see a `token` and `email` in the response body.
-   **400 Bad Request**:
    -   "Email already registered..." (If user exists)
    -   Validation errors (e.g., weak password, invalid email format)

## 🛠️ Testing Login

1.  Find the `POST /api/auth/authenticate` endpoint.
2.  Click **"Try it out"**.
3.  Enter the same credentials as above.
4.  Click **"Execute"**.

### Expected Responses:
-   **200 OK**: Login successful. You get a fresh `token`.
-   **400 Bad Request / 403 Forbidden**: Invalid credentials.

## 🔐 Using the JWT Token

To test protected endpoints (like `/api/scan`):
1.  Copy the `token` from the login response (without quotes).
2.  Scroll to the top of Swagger UI and click **"Authorize"** (if available/configured).
3.  Enter `Bearer <your_token>`.
4.  Now try the protected endpoints.

---

## 🚀 Troubleshooting with Swagger

If Swagger works but the Frontend doesn't:
-   **Issue is likely in the Frontend** (CORS, wrong URL, network blocked).
-   Refer to `TROUBLESHOOTING.md` for more details.

If Swagger FAILS:
-   **Issue is in the Backend** (Database, logic, crash).
-   Check Render logs for the backend service.
