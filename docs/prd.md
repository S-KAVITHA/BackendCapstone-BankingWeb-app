# Product Requirements Document

**Covers two tasks:** Workflow 1 (single-agent Maven test validation) and Workflow 2 (multi-agent: Agent A runs Maven tests, Agent B runs Checkstyle). Each workflow section lists its own trigger, actions, decision events and acceptance criteria.

## Workflow 1 — Single-Agent Maven Validation
This workflow uses a single agent inside a Docker container to run project validation tasks and report the results.

## Trigger
The workflow starts when a developer requests Maven test validation for the project.

## Workflow Selection Justification
Maven test validation was chosen over the alternative of asking the agent to review the controller code for defects. A code review produces open-ended findings that depend on the reviewer, and two runs can disagree. `mvn test` produces the same objective outputs every run (an exit status and counts of passed, failed, errored and skipped tests), so a run can be checked against the Maven output and repeated with identical results.

## Decision Events
- If the build succeeds, record the successful build result.
- If tests fail, record the failing tests and required fixes.
- If the container environment prevents execution (for example, Maven, Java or the database is unavailable), the run is classified as **Blocked**, not Pass or Fail. The agent records the blocking error and does not attempt fixes or installs.

## Ordered Actions
1. Start the containerized development environment.
2. Execute the assigned validation command.
3. Collect command output and errors.
4. Evaluate results against acceptance criteria.
5. Record findings in the iteration log.

## Acceptance Criteria
- The agent executes mvn test, reports the exit status, and includes the number of passed and failed tests.
- The command output is captured and reviewed.
- The result can be classified as pass or fail using objective evidence.
- The workflow does not modify unrelated project files.
- Allowed file changes: only Maven build output under `target/` (compiled classes and `surefire-reports`), which is generated and git-ignored. Any change to tracked files (source, tests, `pom.xml`, configuration, docs) fails Scope Compliance. `git status` before and after the run shows the working tree is unchanged.

---

## Workflow 2 — Multi-Agent Project Validation
This workflow uses two agents in parallel, each running in its own Docker container and Git worktree. Agent A runs Maven tests, while Agent B runs Maven Checkstyle.

## Trigger
A developer requests project checkstyle validation.

## Workflow Selection Justification
Maven tests provide deterministic pass/fail results, while Checkstyle provides measurable code-quality violations. Both tasks can run independently in parallel.

## Isolation
Agent A and Agent B each use a separate branch, worktree, and Docker container. Each container mounts only its assigned worktree, and isolation is verified for both containers.

---

## Agent A — Maven Test

## Actions
Start Agent A's container, verify its worktree, run mvn test, capture the output and exit status, record total/passed/failed/errored/skipped tests, verify no unrelated files were modified, and record the results in the iteration log.

## Decision Events
If the build succeeds, record the successful test result. If tests fail, record the failing tests and errors. If the container prevents execution, document the blocking issue.

## Acceptance Criteria
Maven tests execute successfully or failures are documented; exit status and test counts are reported with command evidence; no unrelated files are modified.

---

## Agent B — Checkstyle

## Actions
Start Agent B's container, verify its worktree, run Maven Checkstyle, capture the output and exit status, record violations/categories/errors/warnings, save the summary to docs/lint-report.md, verify no unrelated files were modified, and record the results in the iteration log.

## Decision Events
If Checkstyle executes successfully, record the measured violations. If violations are found, document them without fixing them. If execution fails, document the blocking issue.

## Acceptance Criteria
Checkstyle produces measurable results; exit status and violations are reported; results are saved to docs/lint-report.md; violations are documented rather than fixed; no unrelated files are modified.

## Completion
The workflow is complete when both agents finish, results are documented, outputs are committed to their respective branches, and both branches are merged into main.