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

This repository uses a dual-agent workflow powered by Claude Code. Each agent runs in an isolated **Git worktree** and **Docker container** to allow parallel development without merge conflicts.

### 1. Worktree Setup

From the main repository root, create a worktree and branch for each agent:

```bash
git worktree add ../target-agent-a -b feature/agent-a
git worktree add ../target-agent-b -b feature/agent-b
```

Directories created:

- `../target-agent-a` (`feature/agent-a`)
- `../target-agent-b` (`feature/agent-b`)

### 2. Agent Responsibilities

Work is split by architectural layer:

| Agent | Branch | Designated Scope |
|---|---|---|
| Agent A | `feature/agent-a` | REST Controllers & Service-layer logic |
| Agent B | `feature/agent-b` | JPA Repositories, Entities, and Unit/Integration Tests |

### 3. Container Execution

Build the Docker image and run each worktree in its own container:

```bash
# Build base image
docker build -t backend-banking-capstone-app .

# Inside /path/to/target-agent-a
docker run -it --rm \
  -v "${PWD}:/workspace" \
  -v "claude-auth:/claude-auth" \
  backend-banking-capstone-app

# Inside /path/to/target-agent-b
docker run -it --rm \
  -v "${PWD}:/workspace" \
  -v "claude-auth:/claude-auth" \
  backend-banking-capstone-app
```

Both containers share the `claude-auth` volume for authentication.

### 4. Verification

Verify setup before running agent sessions:

- Worktrees (main repo): `git worktree list`
- Branches (inside each worktree): `git branch`
- Mounts (inside each container): `ls /workspace`

### 5. Session Guardrails

Every agent prompt must specify:

- **Objective**: Clear description of the task.
- **Write Scope**: Allowed files/folders.
- **Read Scope**: Reference files/folders (read-only).
- **Allowed Commands**: Build/test permissions.
- **Definition of Done**: Success criteria.
- Explicit instruction not to modify files outside assigned paths.

### 6. Review, Commit, and Merge

Review each worktree independently against `main`:

```bash
git status
git diff main
```

Commit accepted changes:

```bash
git add .
git commit -m "feat: complete agent task"
```

Merge and clean up from the main repository:

```bash
git checkout main
git merge feature/agent-a
git merge feature/agent-b

# Remove worktrees and branches
git worktree remove ../target-agent-a
git worktree remove ../target-agent-b
git branch -d feature/agent-a
git branch -d feature/agent-b
```
