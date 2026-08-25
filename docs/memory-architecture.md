Memory Architecture
1. Project Memory

Location: .memory/project/

Stores current tasks, open issues, temporary decisions, and project state.
Scope: Project-specific; applies only to this repository.
Write: Agent can create and update entries.
Pruning: Feature/branch entries are archived after merge. Project-wide entries are reviewed every 90 days.
2. Knowledge

Location: .memory/knowledge/

Stores stable banking, security, authorization, and testing rules.
Scope: Project-specific and intended to remain useful across tasks.
Write: Human-controlled; agent has read-only access.
Pruning: Review when a project-wide rule changes or becomes outdated.
3. Reference Documents

Location: docs/reference/

Stores detailed API, database, workflow, and architecture documentation.
Scope: Project-specific technical references.
Write: Human-controlled.
Pruning: Update or remove when the referenced technical information changes.
4. What Does Not Belong in Memory
Source code and tests → Repository
Customer/account/transaction data → Database
Passwords, API keys, and tokens → Secret Manager
5. Allocation Rule

Current work → Project Memory
Stable rules → Knowledge
Detailed information → Reference Documents

The separation prevents temporary project information from being confused with stable rules and avoids unnecessary duplication of repository or sensitive data.
