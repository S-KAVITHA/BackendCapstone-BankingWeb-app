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
### 3. Network Access

**Decision:** The standard container uses Docker's default bridge network.

**Reason:** The Claude CLI requires outbound network connectivity to communicate
with its service. Network access is therefore enabled during normal agent-assisted
development.

**Verification:** Running `curl -I https://www.google.com` inside the standard
container returned `HTTP/2 200`, confirming that outbound network access was
available.

**Risk Prevented:** Network access is not required when performing offline
validation. A compromised or misbehaving agent with unrestricted network access
could potentially send project information to an external service.

**Mitigation:** When network access is not required, the container is run with
`--network none`. The isolated test produced a DNS/network failure, confirming
that outbound network access was disabled.

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

```bash
curl -I https://www.google.com
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

Only the project directory was mounted from the host at /workspace. Other host directories, such as /Users/user, were not exposed to the container.
file writes were limited to /workspace
host directories outside the mount were inaccessible

Smoke Test:

After confirming the filesystem boundaries, I executed the "run smoke test" 
prompt in the Claude CLI.

The smoke test completed successfully, demonstrating that the containerized environment, mounted project, and Claude CLI were functioning correctly together and that the repository could be accessed and analyzed within the intended execution environment.

Actual Result:

![img_12.png](img_12.png)

![img_1.png](img_1.png)

### What the Smoke Test Proved

The smoke test verified three important boundaries for this Target Codebase:

1. **Filesystem boundary:** Claude could read and write the banking project
   through `/workspace`, while an unmounted host directory was inaccessible.

2. **Persistence boundary:** `summary.txt` created inside `/workspace` was
   visible on the host after the container exited, confirming that only the
   intended project directory was bind-mounted.

3. **Application boundary:** The agent was able to build and start the actual
   Spring Boot banking backend, verify the health endpoint, and execute a
   real database-backed customer query without requiring access to unrelated
   host directories.

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

## 7. Target-Codebase Tool Requirements

The Docker environment was configured based on the requirements of the Online Internet Banking Web Application backend.

- **Java 8 JDK:** The project targets Java 8 and requires Java to compile and run the Spring Boot backend.
- **Maven:** The repository contains a `pom.xml` and uses Maven to build and test the application.
- **Git:** Required for version control and reviewing agent-generated changes.
- **curl:** Used to verify network egress and container network isolation.
- **Claude CLI:** Required for the agentic engineering workflow.

No unrelated language runtimes or development toolchains are installed because the backend does not require them for the planned engineering tasks.
