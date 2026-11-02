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

This repository supports running **two Claude Code agents in parallel** on the same codebase. Each agent works in its own **Git worktree** (a separate checkout on its own branch) and runs inside its own **Docker container**. Each agent's changes stay isolated until a human reviews and merges them into `main`.

```
                 main repository (main)
                 ├── .git/                      <- shared object store + worktree metadata
                 │
   git worktree add                git worktree add
        │                                 │
        ▼                                 ▼
 ../target-agent-a                 ../target-agent-b
 branch: feature/agent-a           branch: feature/agent-b
        │                                 │
  docker run (container A)          docker run (container B)
  Claude Code: Agent A              Claude Code: Agent B
  controllers + services            repositories + entities + tests
        │                                 │
        └──────── review → commit → merge into main ────────┘
```

### Prerequisites

- Git 2.5+ (with `git worktree` support)
- Docker Desktop or Docker Engine
- A clean working tree on `main` (`git status` shows nothing to commit)
- A Claude Code login. It's stored once in the shared `claude-auth` volume (see [Claude authentication](#module-2--launchcode-agentic-engineer)).

### 1. Worktree Setup

Run these commands on the **host**, from the root of the main repository. Create one worktree and one branch per agent, branching from an up-to-date `main`:

```bash
git checkout main
git pull                                    # if a remote is configured

git worktree add ../target-agent-a -b feature/agent-a
git worktree add ../target-agent-b -b feature/agent-b
```

This creates sibling directories next to the main repository:

| Directory | Branch | Used by |
|---|---|---|
| `../target-agent-a` | `feature/agent-a` | Agent A |
| `../target-agent-b` | `feature/agent-b` | Agent B |

Each worktree is a full checkout of the project. All of them share the main repository's `.git` object store, so you don't need to re-clone. A branch can be checked out in only one worktree at a time, which stops both agents from writing to the same branch.

> **Note: worktree `.git` files point to a host path.** Inside a worktree, `.git` is a *file* (for example `gitdir: /path/to/main-repo/.git/worktrees/target-agent-b`), not a directory. When you mount only the worktree into a container, that host path doesn't exist there, and `git` commands inside the container fail with `fatal: not a git repository`. Run all git commands (status, diff, commit, merge) **on the host**. The agents edit files, run builds, and run tests inside the container.

### 2. Agent Responsibilities

Work is split along the application's architectural layers so the two agents never write to the same files:

| Agent | Branch / Worktree | Write scope | Read-only scope |
|---|---|---|---|
| **Agent A** | `feature/agent-a` / `../target-agent-a` | REST controllers and service-layer logic:<br>`src/main/java/com/example/demo/controller/`<br>`src/main/java/com/example/demo/service/` | `model/`, `repository/`, `pom.xml`, `src/main/resources/` |
| **Agent B** | `feature/agent-b` / `../target-agent-b` | JPA entities, repositories, and unit/integration tests:<br>`src/main/java/com/example/demo/model/`<br>`src/main/java/com/example/demo/repository/`<br>`src/test/java/com/example/demo/` | `controller/`, `service/`, `pom.xml`, `src/main/resources/` |

Shared files such as `pom.xml`, `application.properties`, `Dockerfile`, and `README.md` are **read-only for both agents**. If either agent needs one of them changed, a human makes the change on `main`, and then each worktree picks it up (see [Syncing with `main`](#syncing-a-worktree-with-main)).

Both agents must follow the project decisions recorded in `.memory/project/`:

- Code must stay **Java 8 compatible** (`java.version` is `1.8` in `pom.xml`).
- Tests stay under `src/test/java` and follow the existing package structure.
- Use SLF4J logging, not `System.out.println`.
- Handle exceptions centrally through `@ControllerAdvice`.

#### Session task definition

Fill in a session block in `session_tasks.md` for each agent **before** you start it, and paste that block into the agent's first prompt. Every session must specify:

- **Branch name** and **Worktree directory**
- **Task / Objective**: a clear description of what the agent should do
- **Write scope**: the files and folders the agent may modify
- **Read scope**: the files and folders the agent may read but not modify
- **Allowed commands**: for example `./mvnw compile`, `./mvnw test`
- **Definition of done**: concrete success criteria, for example "all tests pass and the new endpoint returns 200"
- An explicit instruction: *"Do not modify any file outside your write scope. If a change outside your scope is required, stop and report it."*

### 3. Container Execution

Build the image once from the main repository root:

```bash
docker build -t backend-banking-capstone-app .
```

The image is based on `eclipse-temurin:8-jdk` and includes Maven, git, Node.js, and Claude Code. During the build it pre-fetches the Maven dependencies from `pom.xml`.

Start one container per worktree, each in its own terminal. Name the containers so you can tell them apart:

```bash
# Terminal 1 — Agent A
cd ../target-agent-a
docker run -it --rm \
  --name agent-a \
  -e SLACK_BOT_TOKEN="${SLACK_BOT_TOKEN:-}" \
  -e SLACK_TEAM_ID="${SLACK_TEAM_ID:-}" \
  -v "$PWD:/workspace" \
  -v claude-auth:/claude-auth \
  backend-banking-capstone-app

# Terminal 2 — Agent B
cd ../target-agent-b
docker run -it --rm \
  --name agent-b \
  -e SLACK_BOT_TOKEN="${SLACK_BOT_TOKEN:-}" \
  -e SLACK_TEAM_ID="${SLACK_TEAM_ID:-}" \
  -v "$PWD:/workspace" \
  -v claude-auth:/claude-auth \
  backend-banking-capstone-app
```

Windows (PowerShell):

```powershell
cd ..\target-agent-a
docker run -it --rm `
  --name agent-a `
  -e SLACK_BOT_TOKEN="$env:SLACK_BOT_TOKEN" `
  -e SLACK_TEAM_ID="$env:SLACK_TEAM_ID" `
  -v "${PWD}:/workspace" `
  -v claude-auth:/claude-auth `
  backend-banking-capstone-app
```

What each piece does:

| Option | Purpose |
|---|---|
| `-v "$PWD:/workspace"` | Mounts **only this worktree** into the container, so the agent can't see or edit the other agent's checkout. |
| `-v claude-auth:/claude-auth` | Shared named volume. `docker-entrypoint.sh` restores the saved Claude credential from it and saves any refreshed credential back. You log in once and both containers reuse that login. |
| `-e SLACK_BOT_TOKEN` / `-e SLACK_TEAM_ID` | The entrypoint runs with `set -u` and reads these variables when it registers the Slack MCP server. They must be **defined**, even as empty strings, or the container exits on startup. |
| `--rm` | Removes the container on exit. Your code changes persist because they live in the mounted worktree. |

Inside each container, start Claude Code and paste that agent's session task block:

```bash
claude
```

The agent can build and test its own worktree in isolation:

```bash
./mvnw -q compile
./mvnw test
```

If both agents need to run the Spring Boot app at the same time, publish its port (`8085`) to a **different host port** for each container, for example `-p 8085:8085` for Agent A and `-p 8086:8085` for Agent B.

#### Verifying the setup

Before you start the agent sessions, confirm the isolation is correct:

| Check | Where | Command | Expected |
|---|---|---|---|
| Worktrees exist | Host, main repo | `git worktree list` | Three entries: main, `target-agent-a`, `target-agent-b` |
| Correct branch | Host, in each worktree | `git branch --show-current` | `feature/agent-a` or `feature/agent-b` |
| Correct mount | Inside each container | `ls /workspace` | That worktree's files only |
| Running containers | Host | `docker ps --filter name=agent-` | `agent-a` and `agent-b` |

To open a second shell in a running agent container, run `docker exec -it agent-a /bin/bash`.

### 4. Review, Commit, and Merge Workflow

When both agents report that their definition of done is met, exit the containers and do the rest **on the host**.

#### Step 1: Review each worktree independently

```bash
cd ../target-agent-a
git status
git diff main --stat          # confirm only files inside Agent A's write scope changed
git diff main                 # read the full change
```

Repeat in `../target-agent-b`. Reject or revert any change outside the agent's write scope:

```bash
git restore path/to/out-of-scope-file
```

You can also ask the `code-reviewer` agent to review the diff before you commit.

#### Step 2: Verify the build and tests in each worktree

```bash
./mvnw test
```

#### Step 3: Commit on each feature branch

Stage specific paths rather than `git add .`, so stray files don't get committed:

```bash
# in ../target-agent-a
git add src/main/java/com/example/demo/controller src/main/java/com/example/demo/service
git commit -m "feat(agent-a): <summary of controller/service change>"

# in ../target-agent-b
git add src/main/java/com/example/demo/model src/main/java/com/example/demo/repository src/test/java
git commit -m "feat(agent-b): <summary of entity/repository/test change>"
```

#### Step 4: Merge into `main`

Go back to the main repository and merge in dependency order. Merge Agent B first, because the persistence layer and its tests are what Agent A's services build on:

```bash
cd /path/to/main-repo
git checkout main

git merge --no-ff feature/agent-b -m "Merge feature/agent-b: entities, repositories, tests"
./mvnw test                   # verify main after the first merge

git merge --no-ff feature/agent-a -m "Merge feature/agent-a: controllers, services"
./mvnw test                   # verify the integrated result
```

The write scopes don't overlap, so these merges should be conflict-free. If you do hit a conflict, it usually means an agent wrote outside its scope. Resolve it manually, then run `git add <file>` and `git commit`. Run `./mvnw test` again before you continue.

#### Step 5: Clean up

```bash
git worktree remove ../target-agent-a
git worktree remove ../target-agent-b
git branch -d feature/agent-a
git branch -d feature/agent-b
git worktree prune             # clears metadata for any worktree deleted by hand
```

`git branch -d` refuses to delete a branch that hasn't been merged, which is a useful safety check. Use `-D` only when you intend to discard an agent's work.

### Syncing a worktree with `main`

If `main` changes while the agents are still working (for example, after a human updates `pom.xml`), bring the change into a worktree before you continue:

```bash
cd ../target-agent-a
git merge main                 # or: git rebase main (only if the branch is not shared)
```

### Troubleshooting

| Symptom | Cause | Fix |
|---|---|---|
| `fatal: not a git repository` inside the container | The worktree's `.git` file points to a host path that isn't mounted | Run git commands on the host |
| `fatal: 'feature/agent-a' is already checked out` | The branch is already in use by another worktree | Use a different branch name, or remove the old worktree |
| Container exits immediately with `SLACK_BOT_TOKEN: unbound variable` | The entrypoint uses `set -u` | Pass `-e SLACK_BOT_TOKEN="" -e SLACK_TEAM_ID=""` |
| Claude asks you to log in again | The `claude-auth` volume wasn't mounted | Add `-v claude-auth:/claude-auth` |
| `port is already allocated` | Both containers publish the same host port | Map each container to a different host port |

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
