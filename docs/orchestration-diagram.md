# Orchestration Diagram - Banking Web App Bug Workflow

This workflow coordinates investigation, implementation, review, testing, and human approval for a bug in the BackendCapstone-BankingWeb-app.

```mermaid
graph TD
    P[Parent / Orchestrator<br/>Coordinates workflow and evaluates results]
    PL[Planner<br/>Analyzes bug and creates fix plan]
    IM[Implementer<br/>Implements approved fix]
    RV[Reviewer<br/>Reviews implementation]
    TS[Tester<br/>Verifies fix with tests]
    PM[Project Manager<br/>Updates issue tracker after approval]
    H[Human Approval<br/>Approves completed fix]

    P -->|Bug report + repository path| PL
    PL -->|Structured plan + files to change| P

    P -->|Plan + relevant context| IM
    IM -->|Modified files + implementation summary| P

    P -->|Modified files + acceptance criteria| RV
    RV -->|Review report| P

    P -->|Modified files + test requirements| TS
    TS -->|Test results| P

    P -->|Completed workflow summary| H
    H -->|Approval or rejection| P

    P -->|Approved issue update| PM
    PM -->|Issue tracker confirmation| P

```

## Handoff Summary

The Orchestrator sends the bug report and repository path to the Planner.
The Planner returns a structured fix plan and file list.
The Orchestrator sends the plan to the Implementer.
The Implementer returns modified files and an implementation summary.
The Reviewer checks the implementation against the acceptance criteria.
The Tester verifies the completed fix with tests.
If review or testing fails, the Orchestrator sends the work back for correction.
Once review and tests pass, the Orchestrator requests human approval.
Only after human approval does the Orchestrator invoke the Project Manager.
The Project Manager updates the issue tracker and reports confirmation.



