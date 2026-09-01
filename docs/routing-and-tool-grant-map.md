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
