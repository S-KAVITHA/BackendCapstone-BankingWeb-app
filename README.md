Capstone Banking Web Application Backend => a Java-based RESTful service built using Spring Boot, Spring Data JPA/Hibernate, and MySQL.

Key Features & Architecture:
User & Role Management: Manages administrative and customer credentials with role-based access control (e.g., ADMIN, BANK_ADMIN, USER).

Customer Profile Service: Handles customer records, personal details, contact information, and account status tracking (ACTIVE, INACTIVE, PENDING).

Account Management: Provides APIs to perform operations on bank accounts, including balance updates, currency formatting, branch assignments, and linking accounts to customer profiles via primary/foreign key relationships.

Database Integration: Utilizes JPA annotations, sequence generators, and relational tables (customer, account, bank_admin) to ensure data integrity and transactional consistency.

## Parallel Multi-Agent Workflow

To speed up development, multiple Claude Code agents can work on different parts of the
codebase at the same time, each isolated in its own `git worktree` and its own container.
Every agent owns a distinct feature area, so their changes rarely touch the same files, which
keeps merge conflicts to a minimum.

### 1. Worktree Setup

Each agent gets its own working directory (worktree) and branch, created from an up-to-date
`main`, so agents never share uncommitted state:

```bash
git fetch origin
git worktree add ../capstone-agent-roles     -b agent/roles     origin/main
git worktree add ../capstone-agent-customers -b agent/customers origin/main
git worktree add ../capstone-agent-accounts  -b agent/accounts  origin/main
git worktree add ../capstone-agent-db        -b agent/db        origin/main
```

This produces one directory per agent (siblings of `capstone/`), each an independent checkout
that can be built, run, and committed to without affecting the others:

```
capstone/                      # main checkout
capstone-agent-roles/          # agent/roles branch
capstone-agent-customers/      # agent/customers branch
capstone-agent-accounts/       # agent/accounts branch
capstone-agent-db/             # agent/db branch
```

When an agent's work is merged (see below), remove its worktree and branch:

```bash
git worktree remove ../capstone-agent-roles
git branch -d agent/roles
```

### 2. Agent Responsibilities

Responsibilities are split by feature/module so each agent has clear, non-overlapping
ownership of the codebase:

| Agent               | Branch             | Owns                                                                                  |
|---------------------|---------------------|----------------------------------------------------------------------------------------|
| `agent/roles`        | User & role mgmt    | Admin/customer credentials, role-based access control (`ADMIN`, `BANK_ADMIN`, `USER`) |
| `agent/customers`    | Customer profiles   | Customer records, personal/contact details, status tracking (`ACTIVE`, `INACTIVE`, `PENDING`) |
| `agent/accounts`     | Account management  | Account APIs, balance updates, currency formatting, branch assignment, customer linking |
| `agent/db`           | Database integration| JPA entities/annotations, sequence generators, schema for `customer`, `account`, `bank_admin` |

Each agent is scoped to its own module and test package under `src/`, and is expected to keep
its branch buildable (`./mvnw verify`) before handing work off for review.

### 3. Container Execution

Every agent runs in its own container instance of the image built from the [`Dockerfile`](Dockerfile),
with its worktree mounted at `/workspace` and a per-agent Claude auth volume so sessions don't
collide:

```bash
docker build -t backend-banking-capstone-app .

docker run -it --rm \
  --name agent-roles \
  -v "$(pwd)/../capstone-agent-roles:/workspace" \
  -v "claude-auth-roles:/claude-auth" \
  backend-banking-capstone-app
```

Repeat with the corresponding worktree path, container `--name`, and `claude-auth-*` volume for
each agent (`agent-customers`, `agent-accounts`, `agent-db`, ...). Because each container only
mounts its own worktree, an agent can only read/write the files for its module — see
[`setup.md`](setup.md) for the filesystem/network isolation decisions this relies on. Containers
can run concurrently on the same host; use `--network none` for any agent that doesn't need
outbound access.

### 4. Merge Workflow

1. Inside its container, the agent commits its changes and pushes the branch:
   ```bash
   git add -A
   git commit -m "agent/accounts: add branch assignment API"
   git push -u origin agent/accounts
   ```
2. Open a pull request from the agent's branch into `main`.
3. A human (or a dedicated reviewer agent) reviews the diff, confirms `./mvnw verify` passes in
   CI, and checks for overlap with other in-flight agent branches.
4. Once approved, merge the PR into `main` (prefer a regular merge or squash merge over
   rebasing, so the branch history stays traceable to the agent that produced it).
5. All other agents rebase or merge `origin/main` into their worktree branch to pick up the
   change before continuing:
   ```bash
   git fetch origin
   git merge origin/main
   ```
6. After merging, remove the completed agent's worktree and branch (see Worktree Setup) to keep
   the working tree list clean.
