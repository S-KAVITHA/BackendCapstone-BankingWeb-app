# Setup & Execution Guide

This document contains the commands required to build, run, and verify the application.

## 1. Build the Docker Image

```bash
docker build -t backend-banking-capstone-app .
```

-----------------------------------------------------------------------------------------------------------------

## 2. Run the Container

### Standard Run 

```bash
docker run -it --rm \
  -v "${PWD}:/workspace" \
  -v "claude-auth:/claude-auth" \
  backend-banking-capstone-app
```
-----------------------------------------------------------------------------------------------------------------

## 3. Verify Network Egress

Check whether the container can reach the network.

```bash
curl -I https://www.google.com
```

Actual result:

<img width="943" height="375" alt="image" src="https://github.com/user-attachments/assets/27cb63ee-1fef-4ef0-a037-2e929a651662" />

-----------------------------------------------------------------------------------------------------------------

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

<img width="947" height="74" alt="image" src="https://github.com/user-attachments/assets/b2bd6870-d5f4-4fc2-b688-ea5751fee844" />

-----------------------------------------------------------------------------------------------------------------
## 4. Smoke Test

After entering the container, I launched the claude CLI by running:
  ```bash
  claude
```

  ![img_9.png](img_9.png)
  
Prompt executed in the claude CLI:

The first prompt:

“List the files in /workspace and write a short summary of what this repo does to a file called summary.txt.”

Actual Result:

![img_11.png](img_11.png)

completed successfully, confirming that Claude could access the mounted  /workspace directory and create summary.txt within the mounted project.

After exiting the container, I verified that summary.txt existed in the project directory on the host (ls summary.txt), confirming that writes inside /workspace persisted through the bind mount.

The second prompt:

“List the files in /Users/user and write a short summary of what this repo does to a file called summary.txt.”

Actual Result:

![img_10.png](img_10.png)

This prompt failed because /Users/user was not mounted into the container. This confirmed that directories outside the mounted project were inaccessible.

These checks verified that:

only the mounted project directory was accessible
file writes were limited to /workspace
host directories outside the mount were inaccessible

Smoke Test:

After confirming the filesystem boundaries, I executed the "run smoke test" 
prompt in the Claude CLI.

The smoke test completed successfully, demonstrating that the containerized environment, mounted project, and Claude CLI were functioning correctly together and that the repository could be accessed and analyzed within the intended execution environment.
Actual Result:

![img_12.png](img_12.png)

------------------------------------------------------------------------------------------------------------------
## 5. Project-Specific Security Decisions

### 1. Filesystem Access

**Decision:** Only the project directory is mounted as `/workspace`.

**Reason:** Only the BackendCapstone-BankingWeb-app directory is mounted into the
container.

Configuration files, Downloads, Documents, SSH keys, and other host directories
remain inaccessible.

This prevents accidental modification of unrelated files while claude edits the
banking project.


**Risk Prevented:** Without this boundary, a misbehaving agent or attacker could access unrelated host files, read sensitive documents, or modify files outside the banking application.


### 2. Authentication Storage

**Decision:** claude authentication is stored in the `claude-auth` Docker volume.

**Reason:** Keeps authentication data separate from the project files and allows authentication to persist across container runs.


### 3. Network Access

**Decision:** The container uses Docker's default bridge network.

**Reason:** Allows outbound network access required for the claude CLI to communicate with the Anthropic API.

**Risk Prevented:** Without controlled network access, a compromised agent could make unauthorized outbound requests, leak application data, or communicate with untrusted external services.


### 4. Network Isolation

**Decision:** A separate container run uses `--network none`.

**Reason:** Verifies that the application can operate in a fully isolated environment where external network access is disabled.


### 5. Container Lifecycle

**Decision:** The container is started with the `--rm` option.

**Reason:** Automatically removes the container after it exits, preventing unused containers from accumulating.

### 6. Filesystem Isolation

**Decision:** Only explicitly mounted directories are accessible inside the container.

**Reason:** The filesystem verification prompt confirmed that /workspace was accessible while /Users/user was inaccessible because it was not mounted into the container.

### 7. Least Privilege

**Decision:** The container runs without privileged mode or additional Linux capabilities.

**Reason:** Reduces the attack surface by granting only the permissions required to run the banking application.


-----------------------------------------------------------------------------------------------------------------

## 6. What Risks Remain?

### Application Dependency Risk

**Risk:** The banking application depends on external Maven libraries that may contain security vulnerabilities.

**Mitigation:** Regularly scan Maven dependencies, review security advisories, and update vulnerable dependencies before deployment.


### Application Secret Exposure Risk

**Risk:** Sensitive configuration values, such as database credentials or API keys, could be accidentally exposed if stored in source files or committed to the repository.

**Mitigation:** Store secrets using environment variables or a secure secret manager and review configuration files before deployment.


### Agent Modification Risk

**Risk:** claude can modify files inside the mounted `/workspace` directory, which could introduce unintended changes to the banking application.

**Mitigation:** Review generated code changes before committing, use version control for rollback, and run automated tests after agent-assisted changes.
