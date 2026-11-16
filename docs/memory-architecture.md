# Memory Architecture

This document describes the memory system as submitted for the BankingProject_Backend Target Codebase. It covers the three layers in `.memory/`, who may update each one, when entries are reviewed or removed, and what is deliberately kept out of memory.

---

## 0. The memory need

**Workflow:** backend changes and bug fixes in the Spring Boot banking API (`src/main/java/com/example/demo`), carried out by Claude Code and by the bug-fix agents (orchestrator, planner, implementer, tester).

**What a fresh session needs to know but cannot reliably infer from the repository:**
- Decisions about how new code must be written, and why. For example, the code still uses `System.out.println`, but decision-005 says new code must use SLF4J. Reading the code alone would lead a session to copy the old pattern.
- Alternatives that were rejected, so they are not reopened: per-controller exception handling, newer Java language features, moving the tests.
- When each decision should be reviewed again.
- Which entries apply to which workflow. For example, the Slack channel in `slack-routing.md` applies only to `email-summarize`.

**What the repository already preserves, so memory does not repeat it:**
- The source code, tests, `pom.xml` (including `<java.version>1.8</java.version>`), and git history.
- Run results and scores, which are kept in `docs/iteration-log.md`.

**What changes over time:** decisions, priorities, deferred work, and review dates. These go in Project Memory.

**What stays stable until a human changes it:** coding conventions and handoff formats. These go in Knowledge.

**What must never be stored:** see §4.

---

## 1. Project Memory — `.memory/project/`

* **Belongs here:** decisions, current priorities, known limitations, deferred work, and unresolved questions that a future session needs. Each entry states its decision, rationale, rejected alternatives, status, review date, and scope.
* **Why here:** this information changes as the project moves on. It is loaded at startup through `.memory/project/MEMORY_INDEX.md`, so a fresh session gets it without being told.
* **Current entries:** `decisions/decision-001.md` through `decision-005.md`, and `slack-routing.md`. All are listed in `MEMORY_INDEX.md`.
* **Who may update:** the agent may create and update entries when a significant decision is made or project state changes. It must update an existing entry rather than create a duplicate, and keep the index in step. Humans may edit any entry.
* **Review triggers:**
  * The entry's review date has passed. The agent must not act on the entry until a human confirms it (CLAUDE.md, "Stale memory policy").
  * The decision or the project priority changes. The entry is then replaced or marked superseded.
  * Workflow- or branch-scoped entries are archived when the branch merges to main and the information is no longer needed.
* **Excluded:** run logs and scores (they belong in the Iteration Log), code, and temporary session notes.

---

## 2. Knowledge — `.memory/knowledge/`

* **Belongs here:** stable rules that change agent behavior across many tasks. Current files:
  * `coding-standards.md`: conventions taken from the actual code.
  * `handoff-orchestrator-to-subagent.md` and `handoff-subagent-to-orchestrator.md`: handoff formats for the bug-fix agents.
* **Why here:** these rules hold until a human changes them. Keeping them apart from Project Memory stops an agent from treating a standard as a decision it can revise.
* **Who may update:** humans only. The agent has read-only access. This is enforced by deny rules in `.claude/settings.json` (`Edit(.memory/knowledge/**)` and `Write(.memory/knowledge/**)`) and stated in CLAUDE.md. The agent must not change these permissions.
* **Review triggers:** a project-wide rule changes, or the code no longer matches what a file describes. Knowledge files have no review dates; humans maintain them directly.
* **Excluded:** decisions that may change (these go in Project Memory) and step-by-step procedures (these go in skills).

---

## 3. Indexed References — `.memory/reference/`

* **Belongs here:** larger documents that are needed only for specific tasks. The agent finds them through an index and queries by keyword. They are never loaded automatically, and the agent never reads the whole directory.
* **Current state:**
  * The banking backend workflow does not need any reference documents yet.
  * The directory holds 7 `proj-csv` documents. These were added in `d5ee4f9` as the corpus for the retrieval exercise (see `docs/retrieval-ground-truth.md`). They belong to that exercise, not to the banking workflow.
  * `.memory/reference/MEMORY_INDEX.md` indexes the layer. It records that the banking workflow has no reference documents yet, and lists the `proj-csv` documents with keywords and classifications.
* **Future condition for adding banking references:** a large document that only some tasks need, such as an API specification for the `Customer` and `Admin` endpoints or a database schema. Such a document would be too big to load at startup and would not change behavior on every task.
* **Who may update:** humans only. Agent writes are blocked by deny rules in `.claude/settings.json`.
* **Review triggers:** the referenced system changes, or the document is no longer used by any task.
* **Allocation error, corrected:** `cost-breakdown.md` (classified `confidential`) was in this layer, against the CLAUDE.md rule that confidential material must never be stored in any memory layer. A human removed it from `.memory/` on 2026-11-16 during the Step 3 allocation review.

---

## 4. What Does Not Belong in Memory

| Data type | Where it belongs instead | Why |
| :--- | :--- | :--- |
| Source code, tests, build config | Repository (`src/`, `pom.xml`) | Already preserved and versioned. Copying it into memory would go out of date. |
| Run results, scores, observations | `docs/iteration-log.md` | A history of events, not context for the next task. |
| Step-by-step procedures | Claude skills (`.claude/skills/`) | Procedures are run on demand, not remembered as facts. |
| Temporary session details | Current session only | Out of date as soon as the session ends. Use `summarize-session` or `write-handoff` for continuity. |
| Customer, account, and transaction data | Database | Personal and financial data. |
| Passwords, API keys, tokens; Confidential or Secret material | Secret manager / restricted storage | Must never be written to any memory layer (CLAUDE.md, "Write policy"). |

---

## 5. Allocation Rule

| Information type | Target location |
| :--- | :--- |
| A decision or priority that may change | Project Memory (`.memory/project/`) |
| A stable rule a human owns | Knowledge (`.memory/knowledge/`) |
| A large document needed only for some tasks | Indexed References (`.memory/reference/`) |
| Code, tests, config | Repository |
| History of runs | Iteration Log |
| Procedures | Skills |
| Customer data | Database |
| Secrets | Secret manager |

---

## 6. Allocation Examples

* "New code must use SLF4J" belongs in Project Memory (decision-005). It is a decision that departs from the current code and may be revisited.
* "Entities write explicit getters and setters, with no Lombok" belongs in Knowledge (`coding-standards.md`). It is a stable convention owned by a human.
* A full API specification would belong in Indexed References. It is large and only some tasks need it.
* "Java target is 1.8" stays in `pom.xml`. Decision-002 records only the rule about new code, not the version number itself.

---

> **Key takeaway:** memory holds only what a fresh session cannot reliably get from the repository. Changing context goes in Project Memory, stable human-owned rules go in Knowledge, and large task-specific documents go in Indexed References. Anything sensitive stays out entirely.
