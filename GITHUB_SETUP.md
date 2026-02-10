# GitHub Actions CI/CD Setup Guide

## 1. Create Environments
To support the Development -> Acceptance -> Production workflow with manual approvals, you must configure **Environments** in your GitHub repository settings.

1.  Go to your GitHub Repository -> **Settings** -> **Environments**.
2.  Create the following environments:
    *   `development`
    *   `acceptance`
    *   `production`

## 2. Configure Production Approval
1.  Click on the `production` environment you just created.
2.  Enable **"Required reviewers"**.
3.  Search for your GitHub username and add yourself (or other team members) as a required reviewer.
4.  Click **Save protection rules**.
    *   *Now, any deployment to production will wait for your manual approval.*

## 3. Set Up Render Deploy Hooks (Secrets)
You need to generate Deploy Hooks in Render for each environment service and add them as Secrets in GitHub.

### In Render Dashboard:
1.  Go to your **Development** backend service.
    *   Settings -> Deploy Hook -> Copy the URL.
2.  Go to your **Acceptance** backend service (if you have one, or create a new web service for it).
    *   Settings -> Deploy Hook -> Copy the URL.
3.  Go to your **Production** backend service.
    *   Settings -> Deploy Hook -> Copy the URL.

### In GitHub Repository:
1.  Go to **Settings** -> **Secrets and variables** -> **Actions**.
2.  Click **New repository secret**.
3.  Add the following secrets:
    *   `RENDER_DEPLOY_HOOK_DEV`: Paste the Development Deploy Hook URL.
    *   `RENDER_DEPLOY_HOOK_ACCEPTANCE`: Paste the Acceptance Deploy Hook URL.
    *   `RENDER_DEPLOY_HOOK_PROD`: Paste the Production Deploy Hook URL.

## 4. How the Pipeline Works
1.  **Build & Test**: On every push to `main`, the code is built and unit tests are run.
2.  **Deploy Dev**: If tests pass, it automatically triggers the `development` deployment.
3.  **Deploy Acceptance**: If Dev deployment succeeds, it triggers the `acceptance` deployment.
4.  **Deploy Prod**: The pipeline pauses and waits for manual approval. Once approved in the GitHub Actions UI, it triggers the `production` deployment.
