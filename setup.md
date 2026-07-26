# Setup & Execution Guide

This document contains the commands required to build, run, and verify the application.

## 1. Build the Docker Image

```bash
docker build -t backend-banking-capstone-app .
```
------------------------------------------------------------------------------------------------------------------

## 2. Run the Container

### Standard Run 

```bash
docker run -it --rm \
  -v "${PWD}:/workspace" \
  -v "claude-auth:/claude-auth" \
  backend-banking-capstone-app
```
------------------------------------------------------------------------------------------------------------------

## 3. Verify Network Egress

Check whether the container can reach the network.

```bash
curl -I https://www.google.com
```

Actual result:

<img width="1881" height="619" alt="image" src="https://github.com/user-attachments/assets/e8cc824a-f5ed-4475-9727-b4ad6ce56d97" />

ai-course:/workspace# curl -I https://www.google.com
**HTTP/2 200**
content-type: text/html; charset=ISO-8859-1
content-security-policy-report-only: object-src 'none';base-uri 'self';script-src 'nonce-4N3eo9Frm-bSWLOD89arrw' 'strict-dynamic' 'report-sample' 'unsafe-eval' 'unsafe-inline' https: http:;report-uri https://csp.withgoogle.com/csp/gws/other-hp
accept-ch: Sec-CH-Prefers-Color-Scheme
p3p: CP="This is not a P3P policy! See g.co/p3phelp for more info."
date: Sun, 26 Jul 2026 18:32:38 GMT
server: gws
x-xss-protection: 0
x-frame-options: SAMEORIGIN
expires: Sun, 26 Jul 2026 18:32:38 GMT
cache-control: private
set-cookie: __Secure-STRP=ANmZwa35RMQq5Cq7ulVoij6kSHphQyvUHKM_-XBSTqWzrgwmT4ozjFTQMAnl3IqBA4bdNaz2doBkehBHecmiDD5n3D42YrMCQ19d; expires=Sun, 26-Jul-2026 18:37:38 GMT; path=/; domain=.google.com; Secure; SameSite=strict
set-cookie: AEC=AdJVEavsVhlBDtifaLd8oaDayJjq-6Oj_DIjErKih_nGigz22TLnO_ogmRI; expires=Fri, 22-Jan-2027 18:32:38 GMT; path=/; domain=.google.com; Secure; HttpOnly; SameSite=lax
set-cookie: NID=533=Js4L5VHHqkshjC5LnnVEjyWbV77qJoUtvy_eh_1UAeFsuolZp-LADN6GjcFlNqDQnPidxEqPv_LCCllGC-Gj9kU0ALupJWF6-6iTXcEWRCNIBBq-XB68WuUMKrjBHiiNSnf1pkDQYCW3sI1rX6IXdzW9uOAGFr9zep39KKJYoIXUPaXrfjP0MfTwgXP8_QBQDrq2yKgZjVssy6-xypxHWZpORw; expires=Mon, 25-Jan-2027 18:32:38 GMT; path=/; domain=.google.com; HttpOnly
alt-svc: h3=":443"; ma=2592000,h3-29=":443"; ma=2592000
ai-course:/workspace#

------------------------------------------------------------------------------------------------------------------

### Isolated Run (No Network Access)

Useful for security or offline testing.

```bash
docker run -it --rm \
  --network none \
  -v "${PWD}:/workspace" \
  backend-banking-capstone-app
```

Check whether the container can reach the network.

Actual result:

user@DESKTOP-A52GAEQ:/mnt/c/Users/user/downloads/AgenticEngineer/BackendCapstone-BankingWeb-app$ docker run -it --rm --network none -v "${PWD}:/workspace" backend-banking-capstone-app
ai-course:/workspace# curl -I https://www.google.com
curl: (6) Could not resolve host: www.google.com
ai-course:/workspace#

------------------------------------------------------------------------------------------------------------------
## 4. Smoke Test


After entering the container, I launched the Claude CLI by running:
  bash
  claude
  
Prompt executed in the Claude CLI:
  
“List the files in /workspace and write a short summary of what this repo does to a file called summary.txt.”

Actual Result:

ai-course:/workspace#
ai-course:/workspace# ls
Dockerfile  README.md  docker-entrypoint.sh  mvnw  mvnw.cmd  pom.xml  settings.json  setup.md  src  statusline.sh  summary.txt  target  test.txt
ai-course:/workspace#

“List the files in /Users/user and write a short summary of what this repo does to a file called summary.txt.”

Actual Result:

/Users/user doesn't exist on this system — this is a Linux environment, and that's a macOS-style path. There's no such directory here.

To verify that the container and repository were functioning correctly, I entered the "run smoke test" prompt in the Claude CLI.

Actual Result:

 run smoke test
 Ran 1 shell command
 Smoke test passed again: BUILD SUCCESS, 16/16 tests passed, no failures or errors.


------------------------------------------------------------------------------------------------------------------
## 5. Project-Specific Security Decisions

### 1. Filesystem Access

**Decision:** Only the project directory is mounted as `/workspace`.

**Reason:** Limits Claude's access to the banking application source code only.

**Risk Prevented:** Without this boundary, a misbehaving agent or attacker could access unrelated host files, read sensitive documents, or modify files outside the banking application.

---

### 2. Authentication Storage

**Decision:** Claude authentication is stored in the `claude-auth` Docker volume.

**Reason:** Keeps authentication data separate from the project files and allows authentication to persist across container runs.

---

### 3. Network Access

**Decision:** The container uses Docker's default bridge network.

**Reason:** Allows outbound network access required for the Claude CLI to communicate with the Anthropic API.

**Risk Prevented:** Without controlled network access, a compromised agent could make unauthorized outbound requests, leak application data, or communicate with untrusted external services.

---

### 4. Network Isolation

**Decision:** A separate container run uses `--network none`.

**Reason:** Verifies that the application can operate in a fully isolated environment where external network access is disabled.

---

### 5. Container Lifecycle

**Decision:** The container is started with the `--rm` option.

**Reason:** Automatically removes the container after it exits, preventing unused containers from accumulating.

---

### 6. Filesystem Isolation

**Decision:** Only explicitly mounted directories are accessible inside the container.

**Reason:** The smoke test confirmed that `/workspace` was accessible while `/Users/user` was unavailable, demonstrating the intended filesystem boundary.

---

### 7. Least Privilege

**Decision:** The container runs without privileged mode or additional Linux capabilities.

**Reason:** Reduces the attack surface by granting only the permissions required to run the banking application.


-----------------------------------------------------------------------------------------------------------------

## 6. What Risks Remain?

### Application Dependency Risk

**Risk:** The banking application depends on external Maven libraries that may contain security vulnerabilities.

**Mitigation:** Regularly scan Maven dependencies, review security advisories, and update vulnerable dependencies before deployment.

---

### Application Secret Exposure Risk

**Risk:** Sensitive configuration values, such as database credentials or API keys, could be accidentally exposed if stored in source files or committed to the repository.

**Mitigation:** Store secrets using environment variables or a secure secret manager and review configuration files before deployment.

---

### Agent Modification Risk

**Risk:** Claude can modify files inside the mounted `/workspace` directory, which could introduce unintended changes to the banking application.

**Mitigation:** Review generated code changes before committing, use version control for rollback, and run automated tests after agent-assisted changes.
