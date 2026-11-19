# Routing and Tool Grant Map

Project:  `BackendCapstone-BankingWeb-app`

This map defines the delegation flow and tool boundaries for the banking bug-fix
workflow. Agent definitions in `.claude/agents/` should match this map.

## Routing

| From | To | Input | Expected output |
|---|---|---|---|
| Orchestrator | `planner` | Bug report + repository path | Fix plan + affected files |
| Orchestrator | `implementer` | Bug report + approved plan + file list | Modified files + implementation summary |
| Orchestrator | `reviewer` | Bug report + plan + modified files | Review findings + approval/rejection |
| Orchestrator | `tester` | Bug report + modified files + review result | Test pass/fail report |
| Orchestrator | `project-manager` | Final result + human approval | Ticket status update |

If the Planner result is incomplete, the Orchestrator sends it back to the
Planner. If the Reviewer finds problems, the Orchestrator sends the findings
back to the Implementer. If tests fail because of implementation changes, the
Orchestrator sends the failure back to the Implementer.

The Project Manager is invoked only after testing and required human approval.

## Tool Grants

| Role | Granted tools | Intentionally denied |
|---|---|---|
| `planner` | `file_read`, `codebase_search` | `file_write`, `shell`, `test_runner`, `task_tracker` |
| `implementer` | `file_read`, `file_write`, `codebase_search` | `shell`, `test_runner`, `task_tracker` |
| `reviewer` | `file_read`, `codebase_search` | `file_write`, `shell`, `test_runner`, `task_tracker` |
| `tester` | `file_read`, `test_runner` | `file_write`, `codebase_search`, `shell`, `task_tracker` |
| `project-manager` | `task_tracker` | `file_read`, `file_write`, `codebase_search`, `shell`, `test_runner` |

## Storage and Retrieval Grants (Lessons Learned workflow)

Servers: `storage` (`mcp/storage/server.py`, :8001) and `retrieval` (`mcp/retrieval/server.py`, :8002).
Project for all calls: `proj-lessons`. Tool IDs: `mcp__storage__<op>` and `mcp__retrieval__retrieve`.

| Role | Granted operations | Classification ceiling | Denied operations | Why |
|---|---|---|---|---|
| `planner` | `retrieve` | `internal` | all storage ops | Must look up prior lessons **before** proposing an approach; it plans and records nothing. |
| `implementer` | `write_entry` | `internal` (writes accepted only at `public`/`internal`) | `read_entry`, `list_entries`, `update_entry`, `delete_entry`, `retrieve` | Records what it implemented as a new entry for later review. It works from the planner's plan, so it does not need retrieval, and it cannot change or delete history. |
| `reviewer` | `retrieve`, `read_entry`, `list_entries` | `internal` | `write_entry`, `update_entry`, `delete_entry` | Checks the change against retrieved lessons and reads the implementer's entry. Read-only, so a review cannot alter what it reviews. |
| `tester` | none | — | all storage ops, `retrieve` | Runs tests; prior lessons and stored entries are not needed to report results. |
| `project-manager` | none | — | all storage ops, `retrieve` | Updates tickets only (`task_tracker`). |
| `orchestrator` | none | — | all storage ops, `retrieve` | Delegates; reads only handoff documents via `file_read`. |

### How these limits are enforced

- **Grants:** each role's `tools` and `disallowedTools` in `.claude/agents/<role>.md` list exactly the operations above. Claude Code exposes only the granted tools to that subagent.
- **Ceiling:** the retrieval server rejects any `classification_ceiling` above `RETRIEVAL_MAX_CEILING` (default `internal`), so no role can request `confidential` or `secret` results. The storage server rejects writes classified above `internal`.
- **No direct reads:** the `coursetools` `file_read` tool refuses `.memory/reference/`, `.memory/storage.db` and `.memory/storage-audit.log`, so agents reach the corpus and stored entries only through these MCP servers, where the ceiling and audit log apply.
- **Audit:** every `write_entry` appends a line to `/memory/storage-audit.log` with `calling_role`; no tool can edit that log.

## Human Approval

Human approval is required after the Tester reports passing tests and before
the `project-manager` updates the issue tracker.

## Boundary Principles

- Planner plans; it does not modify code.
- Implementer modifies code; it does not test or manage tickets.
- Reviewer reviews; it does not modify code.
- Tester tests; it does not modify code.
- Project Manager updates the ticket; it does not access source code.
- No role receives tools that are unnecessary for its single responsibility.
