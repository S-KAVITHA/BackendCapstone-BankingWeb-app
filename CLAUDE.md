# Agent Context Boundary Policy

## Purpose
This file defines how context boundaries are managed in this project.
At the start of each new phase of work, the agent must follow the
procedure below before taking any action.

## Context Boundary Procedure
At the start of each new phase, before doing any editing or analysis:

1. Restate the current task goal in one sentence.
2. List the rules currently in effect (verbatim, not paraphrased).
3. State explicitly which prior rules are no longer in effect, if any.
4. Identify the specific artifact being worked on in this phase.
5. Then proceed with the requested work.

Every handoff must carry the requirement-transition record (rules still active, rules superseded with old → new, why, decisions still relevant, artifacts to revisit), using the Requirement Transitions section of `.claude/skills/write-handoff/SKILL.md`.

## Why This Matters
Rules and requirements change during long sessions. This procedure ensures the agent is always operating from the current version of the rules, not a prior version buried in conversation history.

## Compaction Policy

Compaction is a last resort. Proactive summarization (see .claude/skills/summarize-session/SKILL.md) should be triggered before the context window exceeds 60% capacity to avoid relying on compaction.

Observations from testing (Run 011, manual compaction probe, 2026-08-23):
- Compaction reliably preserves: general task intent, the major style-guide rules, and high-level workflow context. Probes for the current Introduction section state and for the remaining work were fully correct after compaction.
- Compaction may lose or distort: detailed conversational history, exact rule wording, and specific edited file states. The probe asking for the original task and full style guide was incorrect, and the probe for rule changes since the start was only partially correct.
- Manual compaction should be triggered at: around 60% context capacity, and only after a proactive summary has been written. After compaction, verify the current files before continuing instead of relying on recall.

# Agent Instructions

## Memory Configuration

At the start of every session, read .memory/project/MEMORY_INDEX.md
to orient yourself. Then read any active entries listed there that
are relevant to the current task.

Before making any significant decision or observing something worth
remembering across sessions, check the index for an existing entry
on the same topic. Update existing entries rather than creating
duplicates.

### Memory layers

- .memory/project/ — Read on startup via MEMORY_INDEX.md. You may
 write new entries here when a significant decision is made or
 project state changes.

- .memory/knowledge/ — Read-only. Consult before making any decision
 that touches coding standards or architectural constraints. Never
 attempt to write to this directory.

- .memory/reference/ — Read-only. Query by keyword for relevant
 excerpts when you need background context. Do not read the entire
 directory.

### Write policy

Before writing a new memory entry, check MEMORY_INDEX.md for an
existing entry on the same topic. Update existing entries rather
than creating new ones. Never write anything classified as
Confidential or Secret to any memory layer.

### Permission denials

.memory/knowledge/ is protected in two ways: deny rules in
.claude/settings.json block the Edit and Write tools, and the
container mounts the directory read-only, so any shell write fails
with "Read-only file system".

If any write to .memory/knowledge/ or .memory/reference/ is denied
(by a permission rule, a tool refusal, or a filesystem error):
1. Stop the attempted write. Do not retry it.
2. Do not work around the restriction: no other tool, shell
  redirection, copy-and-replace, chmod, remount, or edits to
  .claude/settings.json.
3. Report the restriction: name the file, the change you intended,
  and the exact denial message.
4. Ask a human to make the update. Propose the exact text so they
  can apply it themselves.

### Stale memory policy

Before acting on any memory entry, check its review date.
If the review date has passed:
1. Do not act on that entry until a human confirms it
  is still accurate
2. State clearly in your response: "Memory entry
  [filename] has a review date of [date], which has
  passed. Please confirm this is still current before
  I proceed."
3. Wait for confirmation before using the entry

This applies to all entries in .memory/project/.
Knowledge files do not have review dates and are
maintained by humans directly.

### Scope verification

Read SCOPE.md at the root of .memory/ on startup. If it does not
match this project, halt and report the mismatch before doing
anything else.

### Memory Scope Policy

Every project-memory entry must identify the agent, workflow, or task scope for which it is authoritative.
Before applying a memory entry, verify that its declared scope matches the current agent and task.
An agent must not apply a memory entry to a different agent, workflow, or task unless the entry explicitly declares that broader scope.
If the scope is missing, ambiguous, or does not match the current task, treat the entry as non-authoritative and request confirmation before using it.
