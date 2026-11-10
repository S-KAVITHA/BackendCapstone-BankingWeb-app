Capstone project is the Online Internet Banking Web Application, a Java-based backend banking application designed to provide customers with online banking services and administrators with tools to manage and authorize banking activities. The application uses Spring Boot, Java, Spring Data JPA/Hibernate, and MySQL for the backend and database layers, with a web-based frontend supporting customer and administrator portals.

**Key Features and Functionality:**

Customer Registration and Login: Customers can register for online banking, log in, and access their banking activities.

Account Management: Customers can open savings and current accounts, view account information, and check account balances.

Banking Transactions: Customers can perform deposits and withdrawals and view their account transactions.

Funds Transfers: Customers can transfer funds between accounts and manage transfer recipients.

Cheque Book Requests: Customers can submit cheque book requests for their accounts.

Customer Profile Management: Customers can view their profile information and log out of the application.

Administrative Management: Administrators can register and log in, approve or reject customer registrations, account requests, transactions, fund transfers, and cheque book requests, as well as block users when necessary.

Database Integration: The application uses MySQL with JPA/Hibernate to persist customer, account, transaction, transfer, and cheque request information and maintain relationships between application entities.

**Architecture and Code Structure:**

The backend follows a layered Spring Boot architecture, with controllers responsible for REST endpoints, services containing business logic, repositories providing database access through Spring Data JPA, and model/entity classes representing the application's relational data. The application separates customer and administrator functionality, allowing banking operations and administrative approval workflows to be developed and maintained independently.

## Parallel Multi-Agent Workflow

This repository supports running **two Claude Code agents in parallel** on the same codebase. Each agent works in its own **Git worktree** (a separate checkout on its own branch) and runs inside its own **Docker container**. Changes stay isolated on each agent's branch until a human reviews them and merges them into `main`.

```
                 main repository (branch: main)
                 └── .git/            <- shared object store + worktree metadata
                        │
        ┌───────────────┴────────────────┐
  git worktree add                 git worktree add
        │                                │
        ▼                                ▼
 ../target-agent-a                ../target-agent-b
 branch: feature/agent-a          branch: feature/agent-b
        │                                │
 docker run  (container A)        docker run  (container B)
 Claude Code: Agent A             Claude Code: Agent B
 controller/ + service/           model/ + repository/ + src/test/
        │                                │
        └──── human review → commit → merge into main ────┘
```

Why this layout:

- **Worktrees** give each agent its own files and branch, but they share one Git history. You don't need to clone twice, and the branches can be merged directly.
- **Containers** give each agent its own process space, Claude Code session, and MariaDB instance. Agents can't step on each other's running servers or databases.
- **Layer-based scopes** keep the two write sets disjoint. That keeps merge conflicts rare and easy to attribute.

### Prerequisites

- Git 2.5+ (with `git worktree` support)
- Docker Desktop or Docker Engine
- A clean working tree on `main` (`git status` shows nothing to commit)
- A Claude Code login, stored once in the shared `claude-auth` named volume (see the **Claude authentication** note under [Quick Start](#quick-start))

### 1. Worktree Setup

Run these from the **main repository root on the host**:

```bash
git checkout main
git pull                                   # start both agents from the same commit

git worktree add ../target-agent-a -b feature/agent-a
git worktree add ../target-agent-b -b feature/agent-b

git worktree list                          # confirm all three checkouts
```

Result:

| Directory | Branch | Used by |
|---|---|---|
| `.` (main repo) | `main` | Human reviewer / merges |
| `../target-agent-a` | `feature/agent-a` | Agent A |
| `../target-agent-b` | `feature/agent-b` | Agent B |

Rules for worktrees:

- A branch can be checked out in only one worktree at a time. Don't check out `feature/agent-a` or `feature/agent-b` in the main repo while their worktrees exist.
- Each worktree's `.git` is a **file** (not a directory). It points back to `<main-repo>/.git/worktrees/<name>` using an **absolute host path**. Inside a container that mounts only the worktree, that path doesn't exist, so `git` commands fail with `fatal: not a git repository`. Run Git commands (status, diff, commit, merge) **on the host**, or see [Git inside the container](#git-inside-the-container-optional).

### 2. Agent Responsibilities

Work is split by architectural layer. Each agent has a disjoint **write scope**:

| Agent | Branch | Write scope | Read-only scope |
|---|---|---|---|
| **Agent A** | `feature/agent-a` | `src/main/java/com/example/demo/controller/`<br>`src/main/java/com/example/demo/service/` | `model/`, `repository/`, `src/main/resources/`, `pom.xml` |
| **Agent B** | `feature/agent-b` | `src/main/java/com/example/demo/model/`<br>`src/main/java/com/example/demo/repository/`<br>`src/test/java/` | `controller/`, `service/`, `src/main/resources/`, `pom.xml` |

Shared files such as `pom.xml`, `application.properties`, `README.md`, and `.claude/` are **read-only for both agents**. If an agent needs a change to one of these files, it reports the need in its summary, and a human makes the change on `main`.

#### Coordinating across layers

Agent A's controllers and services call Agent B's entities and repositories, so changes can depend on each other:

- Agree on interface changes **before** starting. For example, a new repository method signature or a new entity field.
- If Agent A needs a method Agent B hasn't written yet, Agent A codes against the agreed signature and notes the dependency. The branches compile together only after both are merged, so merge Agent B first (see [step 6](#6-review-commit-and-merge)).
- Both agents follow the project conventions: Java 8 language level (`<java.version>1.8</java.version>` in `pom.xml`), tests under `src/test/java`, SLF4J logging instead of `System.out.println`, and centralized `@ControllerAdvice` exception handling.

#### Session guardrails (prompt template)

Every agent prompt must state the following:

```text
Objective:          <one-sentence task>
Write scope:        <paths this agent may create/modify>
Read scope:         <paths it may read for reference only>
Allowed commands:   ./mvnw -q compile, ./mvnw test, git diff (read-only)
Not allowed:        git commit / merge / push, editing files outside write scope,
                    changing pom.xml or application.properties
Definition of done: <observable success criteria, e.g. "./mvnw test passes and
                    the new endpoint returns 200 for a valid request">
Report:             files changed, commands run + results, open dependencies
```

### 3. Container Execution

Build the image once from the main repository root:

```bash
docker build -t backend-banking-capstone-app .
```

The image (see `Dockerfile` and `docker-entrypoint.sh`) provides:

- Java 8 JDK (`eclipse-temurin:8-jdk`), Maven, Git, Node.js, and Claude Code
- A **per-container MariaDB server**. The entrypoint starts it and creates the `db_world` database with the `root`/`root` login that `application.properties` expects. Each agent gets its own isolated database.
- Restoring your Claude login from the `claude-auth` volume, so onboarding is skipped after the first run

Start one container per worktree, each in its own terminal:

```bash
# Terminal 1 — Agent A
cd ../target-agent-a
docker run -it --rm --name agent-a \
  -v "${PWD}:/workspace" \
  -v claude-auth:/claude-auth \
  backend-banking-capstone-app

# Terminal 2 — Agent B
cd ../target-agent-b
docker run -it --rm --name agent-b \
  -v "${PWD}:/workspace" \
  -v claude-auth:/claude-auth \
  backend-banking-capstone-app
```

On Windows PowerShell, replace the trailing `\` with a backtick (`` ` ``). `"${PWD}:/workspace"` works as written.

Inside each container, start Claude Code and paste that agent's guardrailed prompt:

```bash
claude
```

Container notes:

- **Both containers share `claude-auth`.** Log in once in either container and the credential is reused. Don't log out in one container while the other is running.
- **Running the app in both containers:** the app listens on port `8080` (`server.port` in `application.properties`). To reach it from the host, publish a **different host port** per container, for example `-p 8081:8080` for Agent A and `-p 8082:8080` for Agent B. Two containers can't bind the same host port.
- **A second shell into a running agent container:** `docker exec -it agent-a /bin/bash`
- Files the agent edits under `/workspace` are written straight to that worktree on the host. They persist after the container exits (`--rm` removes only the container).

#### Git inside the container (optional)

By default, do all Git work on the host. If you want the agent to run read-only Git commands such as `git diff`, mount the main repository's `.git` directory at the **same absolute path** the worktree's `.git` file points to:

```bash
cat ../target-agent-a/.git      # e.g. gitdir: /home/me/banking/.git/worktrees/target-agent-a
docker run -it --rm --name agent-a \
  -v "${PWD}:/workspace" \
  -v "/home/me/banking/.git:/home/me/banking/.git" \
  -v claude-auth:/claude-auth \
  backend-banking-capstone-app
```

This doesn't work for Windows host paths (`C:\...` or `/mnt/c/...` from WSL), because the path can't be reproduced inside a Linux container. Use host-side Git in that case.

### 4. Verification

Before you start the agent sessions, check each item:

| Check | Where | Command | Expected |
|---|---|---|---|
| Worktrees exist | Host, main repo | `git worktree list` | 3 entries: `main`, `feature/agent-a`, `feature/agent-b` |
| Correct branch | Host, each worktree | `git -C ../target-agent-a branch --show-current` | `feature/agent-a` (or `-b`) |
| Workspace mounted | Each container | `ls /workspace` | `pom.xml`, `src/`, … from that worktree |
| Database ready | Each container | Startup log | `Database ready: db_world` |
| Build works | Each container | `./mvnw -q compile` | Exits 0 |
| Claude logged in | Each container | `claude` | No login prompt (after the first run) |

### 5. Running the Agents in Parallel

1. Give each agent its prompt from the [guardrail template](#session-guardrails-prompt-template).
2. Let both agents work at the same time. Each one edits only its own worktree.
3. Watch for scope violations as they work. From the host, `git -C ../target-agent-a status --short` should list only paths inside Agent A's write scope. Do the same check for Agent B.
4. When an agent reports done, ask it to run `./mvnw test` and include the results in its summary. Then exit the container.

### 6. Review, Commit, and Merge

All steps below run **on the host**.

**a. Review each worktree against `main`**

```bash
cd ../target-agent-a
git status
git diff main --stat          # confirm only write-scope paths changed
git diff main                 # read the actual changes
./mvnw test                   # or run inside the container before exiting
```

Reject or revert any file outside the agent's write scope:

```bash
git restore --source=main -- <path>
```

**b. Commit accepted changes on each feature branch**

```bash
# in ../target-agent-a
git add src/main/java/com/example/demo/controller src/main/java/com/example/demo/service
git commit -m "feat(api): <summary of Agent A's change>"

# in ../target-agent-b
git add src/main/java/com/example/demo/model src/main/java/com/example/demo/repository src/test/java
git commit -m "feat(data): <summary of Agent B's change>"
```

Stage by path instead of using `git add .`. That way, stray files from outside the write scope can't get into the commit.

**c. Merge into `main`, data layer first**

Merge Agent B first. Agent A's controllers and services usually depend on Agent B's entities and repositories, so this order keeps `main` compiling after each merge.

```bash
cd <main-repo>
git checkout main
git merge --no-ff feature/agent-b -m "Merge Agent B: entities, repositories, tests"
./mvnw test

git merge --no-ff feature/agent-a -m "Merge Agent A: controllers, services"
./mvnw test
```

`--no-ff` keeps a merge commit for each agent, so you can trace or revert an agent's work as one unit with `git revert -m 1 <merge-commit>`.

**d. Resolve conflicts (if any)**

With disjoint write scopes, conflicts should only happen when an agent edited outside its scope. If `git merge` reports a conflict:

1. `git status` to list the conflicted files.
2. For each file, keep the version from the agent that **owns** that path. Edit the file to resolve, then `git add <file>`.
3. `git commit` to finish the merge, then `./mvnw test`.
4. To abandon the merge instead, run `git merge --abort`.

**e. Clean up**

```bash
git worktree remove ../target-agent-a
git worktree remove ../target-agent-b
git branch -d feature/agent-a feature/agent-b
git worktree prune                         # drop stale metadata, if any
```

`git worktree remove` refuses to remove a worktree that has uncommitted changes. Commit or discard those changes first, or use `--force` if you're sure the changes aren't needed.

### Quick Reference

| Step | Where | Command |
|---|---|---|
| Create worktrees | Host | `git worktree add ../target-agent-{a,b} -b feature/agent-{a,b}` |
| Build image | Host | `docker build -t backend-banking-capstone-app .` |
| Run agent | Host, in worktree | `docker run -it --rm --name agent-a -v "${PWD}:/workspace" -v claude-auth:/claude-auth backend-banking-capstone-app` |
| Start Claude | Container | `claude` |
| Review | Host, in worktree | `git diff main --stat` |
| Commit | Host, in worktree | `git add <scope paths> && git commit` |
| Merge | Host, main repo | `git merge --no-ff feature/agent-b` then `feature/agent-a` |
| Clean up | Host, main repo | `git worktree remove …` and `git branch -d …` |

```
# Module 2 — LaunchCode Agentic Engineer

Docker development environment for LaunchCode's **Agentic Programming** course, Module 2. Extends the Module 1 environment with:

- **Slack MCP server** — lets Claude Code prompts read and post to Slack
- **Gmail MCP server** — lets Claude Code prompts read and send email
- **Pre-configured skills** — custom slash commands available inside Claude Code
- **Pre-configured agents** — autonomous sub-agents Claude Code can invoke for specialized tasks

## Quick Start

Build and run locally:

```bash
cd module_3
docker build -t agentic_engineer_3 .
docker run -it --rm \
  --network agent-internal \
  -p 8501:8501 -p 8502:8502 \
  -p 6274:6274 -p 3000:3000 \
  -p 8001:8001 -p 8002:8002 \
  -e SLACK_BOT_TOKEN=xoxb-your-token \
  -e SLACK_TEAM_ID=T0123456 \
  -v claude-auth:/claude-auth \
  -v "$PWD":/workspace \
  -v "$PWD/.memory":/memory \
  agentic_engineer_3
```

Or pull the pre-built image from DockerHub:

```bash
docker run -it --rm \
  --network agent-internal \
  -p 8501:8501 -p 8502:8502 \
  -p 6274:6274 -p 3000:3000 \
  -p 8001:8001 -p 8002:8002 \
  -e SLACK_BOT_TOKEN=xoxb-your-token \
  -e SLACK_TEAM_ID=T0123456 \
  -v claude-auth:/claude-auth \
  -v "$PWD":/workspace \
  -v "$PWD/.memory":/memory \
  us-central1-docker.pkg.dev/hire-human/hire-human-ai/agentic_engineer_3:latest
```

Full setup with Slack and Gmail (reads credentials from your shell environment):

```bash
docker run -it --rm \
  --network agent-internal \
  -p 8501:8501 -p 8502:8502 \
  -p 6274:6274 -p 3000:3000 \
  -p 8001:8001 -p 8002:8002 \
  -e OPENROUTER_API_KEY=$OPENROUTER_API_KEY \
  -e SLACK_BOT_TOKEN=$SLACK_BOT_TOKEN \
  -e SLACK_TEAM_ID=$SLACK_TEAM_ID \
  -v claude-auth:/claude-auth \
  -v "$PWD":/workspace \
  -v "$PWD/.memory":/memory \
  -v "$HOME/.gmail-mcp":/root/.gmail-mcp \
  us-central1-docker.pkg.dev/hire-human/hire-human-ai/agentic_engineer_3:latest
```

Place `credentials.json` (from Google Cloud Console) in `$PWD` before running. The Gmail MCP will trigger OAuth on first use and persist the token in `~/.gmail-mcp/`.

> **Claude authentication:** The `-v claude-auth:/claude-auth` mount is a shared named volume that persists your Claude Code login across container runs — and across every course module. You go through Claude's first-run onboarding and log in once; every later container restores your credential and skips onboarding. Only your credential is stored on the volume — skills, agents, settings, the status line, and each image's MCP servers stay baked into the image.

## Build

```bash
docker build -t agentic_engineer_3 .
```

Force a complete rebuild (no cached layers):

```bash
docker build --no-cache -t agentic_engineer_3 .
```

## Run

### With a local workspace (recommended)

```bash
# macOS / Linux
docker run -it --rm \
  --network agent-internal \
  -p 8501:8501 -p 8502:8502 \
  -p 6274:6274 -p 3000:3000 \
  -p 8001:8001 -p 8002:8002 \
  -e SLACK_BOT_TOKEN=xoxb-your-token \
  -e SLACK_TEAM_ID=T0123456 \
  -v claude-auth:/claude-auth \
  -v "$PWD":/workspace \
  -v "$PWD/.memory":/memory \
  agentic_engineer_3

# Windows (PowerShell)
docker run -it --rm `
  --network agent-internal 
  -p 8501:8501 -p 8502:8502 `
  -p 6274:6274 -p 3000:3000 `
  -p 8001:8001 -p 8002:8002 `
  -e SLACK_BOT_TOKEN=xoxb-your-token `
  -e SLACK_TEAM_ID=T0123456 `
  -v claude-auth:/claude-auth `
  -v "${PWD}:/workspace" `
  -v "${PWD}/.memory":/memory `
  agentic_engineer_3
```

Files created or edited inside `/workspace` are saved to your local folder and persist after the container exits.

**Port reference:**

| Port | Purpose |
| ---- | ------- |
| `8501` / `8502` | Streamlit apps |
| `6274` | MCP Inspector web UI |
| `3000` | MCP Inspector proxy server |
| `8001` | MCP storage server (connect Inspector directly without proxy: transport = Streamable HTTP, URL = `http://localhost:8001/mcp`) |
| `8002` | MCP retrieval server (transport = Streamable HTTP, URL = `http://localhost:8002/mcp`) |

### Opening a second shell in a running container

To open an additional terminal into an already-running container (useful for inspecting logs, running commands, or testing MCP servers while the main session is active):

```bash
# If you used the default image name
docker exec -it $(docker ps -q -f ancestor=agentic_engineer_3) /bin/bash

# Or look up the container ID/name first, then connect
docker ps
docker exec -it <container_id_or_name> /bin/bash
```

### Without a local workspace

```bash
docker run -it --rm \
  --network agent-internal \
  -p 8501:8501 \
  -p 6274:6274 -p 3000:3000 \
  -p 8001:8001 -p 8002:8002 \
  -v claude-auth:/claude-auth \
  agentic_engineer_3
```

Any files created inside the container will be lost when it exits.

---

## MCP Servers

MCP (Model Context Protocol) servers extend Claude Code so that prompts can take real-world actions — posting to Slack, reading email, etc. — without writing any extra code. Both servers are pre-installed as global npm packages and pre-configured in `/root/.claude/settings.json` inside the image.

### Slack MCP Server

**Package:** `@modelcontextprotocol/server-slack`

**Required environment variables** (pass with `-e` at `docker run`):

| Variable          | Description                                           |
| ----------------- | ----------------------------------------------------- |
| `SLACK_BOT_TOKEN` | Bot User OAuth Token from your Slack App (`xoxb-…`)   |
| `SLACK_TEAM_ID`   | Your Slack workspace ID (found in workspace settings) |

**Getting a Slack Bot Token:**

1. Go to [api.slack.com/apps](https://api.slack.com/apps) and create a new app.
2. Under **OAuth & Permissions**, add Bot Token Scopes: `channels:read`, `chat:write`, `channels:history`, `users:read`.
3. Install the app to your workspace and copy the **Bot User OAuth Token**.
4. Copy your workspace's **Team ID** from the workspace URL or settings.

**What it enables:** Claude Code prompts can list channels, read message history, post messages, and look up users in your Slack workspace.

### Gmail MCP Server

**Package:** `@gongrzhe/server-gmail-autoauth-mcp`

**Setup (OAuth):**

1. Go to [Google Cloud Console](https://console.cloud.google.com/) and create a project.
2. Enable the **Gmail API**.
3. Create OAuth 2.0 credentials (Desktop App type) and download `credentials.json`.
4. Place `credentials.json` in your workspace directory (mounted at `/workspace`).
5. On first use, the MCP server will open an OAuth flow and save a token. To persist the token across container restarts, mount the credentials directory:

```bash
docker run -it --rm \
  --network agent-internal \
  -p 8501:8501 \
  -p 6274:6274 -p 3000:3000 \
  -p 8001:8001 -p 8002:8002 \
  -v claude-auth:/claude-auth \
  -v "$PWD":/workspace \
  -v "$PWD/.memory":/memory \
  -v "$HOME/.gmail-mcp":/root/.gmail-mcp \
  agentic_engineer_3
```

**What it enables:** Claude Code prompts can read, search, and send Gmail messages on behalf of the authenticated user.

---

## Skills

Skills are custom slash commands invoked by typing `/skill-name` inside a Claude Code session. Claude Code supports two ways to define skills, and this image uses both.

### Pre-installed Skills

| Skill                 | Description                                                 |
| --------------------- | ----------------------------------------------------------- |
| `/send-slack-message` | Send a message to a Slack channel via the Slack MCP server  |
| `/check-gmail`        | Retrieve and summarize recent unread Gmail messages         |
| `/send-email`         | Draft and send an email via the Gmail MCP server            |
| `/summarize-session`  | Generate a bullet-point summary of the current work session |

---

### SKILL.md files

Each skill lives in its own Markdown file at `.claude/skills/<skill-name>/SKILL.md`. Inside the container these are placed at `/root/.claude/skills/`.

```
skills/
  send-slack-message/SKILL.md
  check-gmail/SKILL.md
  send-email/SKILL.md
  summarize-session/SKILL.md
```

**When to use this method:**

- The skill prompt is more than a sentence or two.
- The skill has multiple steps, conditional logic, or examples that benefit from formatting.
- You want each skill in its own file for easier editing and version control.
- You are building skills that will be shared or maintained over time.

To add a skill, create a new directory and SKILL.md file:

```bash
mkdir -p skills/my-skill
cat > skills/my-skill/SKILL.md << 'EOF'
Description of what this skill does.

Steps:
1. First step Claude should take.
2. Second step.
3. Confirm result to the user.
EOF
```

Then rebuild the image so the file is copied into `/root/.claude/skills/`:

```bash
docker build -t agentic_engineer_3 .
```

**Adding a skill at runtime (no rebuild required):**

You can also add a skill directly inside a running container without rebuilding the image. The full path inside the container is:

```
/root/.claude/skills/<skill-name>/SKILL.md
```

For example:

```bash
mkdir -p /root/.claude/skills/my-skill
nano /root/.claude/skills/my-skill/SKILL.md
```

Note: since the container is run with `--rm`, any skills added this way will be lost when the container exits. To persist them, add them to the `skills/` directory in this repo and rebuild.

---

## Agents

Agents are autonomous sub-agents that Claude Code can spin up to handle specialized tasks. Unlike skills (which are slash commands you invoke), agents are specialists that Claude Code invokes automatically when the task matches, or that you can request explicitly in a prompt.

Agents are defined as Markdown files with YAML frontmatter and stored in `.claude/agents/` (inside the container: `/root/.claude/agents/`). Source files live in `agents/` in this repo and are copied in at image build time.

### Pre-installed Agents

| Agent           | Trigger description                                                                           |
| --------------- | --------------------------------------------------------------------------------------------- |
| `code-reviewer` | Reviews recent git changes for quality, security, and maintainability without modifying files |

### Running an Agent

**Explicitly — ask Claude Code to use it:**

```
Review my recent changes using the code-reviewer agent.
```

**Automatically** — Claude Code will invoke the agent on its own when the task matches the agent's description. For example, after you ask Claude to write a new feature, it may proactively run the code-reviewer agent.

### How the code-reviewer Agent Works

When invoked, `code-reviewer`:

1. Runs `git diff` to find recent changes.
2. Reads each modified file.
3. Classifies every issue as **Critical**, **Warning**, or **Suggestion**.
4. Provides a concrete fix example for every Critical and Warning item.

It checks for:

- Code clarity and naming conventions
- Duplicated logic
- Error handling and input validation
- Exposed secrets or API keys
- Test coverage for new behavior
- Performance implications

The agent never edits or creates files — it only reports.

### Adding a New Agent

1. Create a Markdown file in `agents/` with a YAML frontmatter block:

```markdown
---
name: my-agent
description: >
  One or two sentences describing when Claude Code should invoke this agent.
tools: Read, Grep, Glob, Bash
model: inherit
permissionMode: default
---

Your agent prompt goes here.
```

2. Rebuild the image:

```bash
docker build -t agentic_engineer_3 .
```

**Adding an agent at runtime (no rebuild required):**

```bash
mkdir -p /root/.claude/agents
nano /root/.claude/agents/my-agent.md
```

Note: agents added this way are lost when the container exits (since it runs with `--rm`). Add them to `agents/` in this repo and rebuild to persist them.

### Agent File Format

| Field            | Description                                                         |
| ---------------- | ------------------------------------------------------------------- |
| `name`           | Identifier used to reference the agent                              |
| `description`    | Tells Claude Code when to invoke the agent (be specific)            |
| `tools`          | Comma-separated list of tools the agent may use                     |
| `model`          | Model to use (`inherit` uses the same model as the parent session)  |
| `permissionMode` | `default` respects normal permission prompts; `allowAll` skips them |

---

## Gmail API Credentials (Python SDK)

In addition to the MCP server, the Python `google-api-python-client` library is available for direct API use in your code.

Place `credentials.json` (from Google Cloud Console) in your workspace directory. On first run it triggers OAuth and saves `token.json`. Reference it in code as:

```python
from google_auth_oauthlib.flow import InstalledAppFlow

flow = InstalledAppFlow.from_client_secrets_file("credentials.json", SCOPES)
```

Keep both files out of source control:

```
# .gitignore
credentials.json
token.json
```

---

## Running Streamlit Apps

From inside the container:

```bash
streamlit run app.py
```

Then open [http://localhost:8501](http://localhost:8501) in your browser.

---

## Images

Images are built and published automatically by a GitHub Action whenever the main repository (not forks) changes — you don't need to build or push manually.

### Pulling the image (students)

Once published, students can run the image directly without cloning the repo or building anything:

```bash
docker run -it --rm \
  --network agent-internal \
  -p 8501:8501 -p 8502:8502 \
  -p 6274:6274 -p 3000:3000 \
  -p 8001:8001 -p 8002:8002 \
  -e SLACK_BOT_TOKEN=xoxb-your-token \
  -e SLACK_TEAM_ID=T0123456 \
  -v claude-auth:/claude-auth \
  -v "$PWD":/workspace \
  -v "$PWD/.memory":/memory \
  us-central1-docker.pkg.dev/hire-human/hire-human-ai/agentic_engineer_3:latest
```
