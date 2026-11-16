---
name: write-handoff
description: >
  Produces a complete, self-contained handoff document for ending a session
  and briefing a fresh one. Use when a session is ending and its work needs
  to continue in a new session with no access to prior context.
---

# Skill: Write Handoff Document

When this skill is invoked, produce a structured handoff document
using exactly the format below. This document will be the only context
given to the next session. It must be complete enough that the next
session can continue the work correctly without access to any prior
conversation.

Do not summarize or paraphrase rules, acceptance criteria, or file
contents. Copy them verbatim. The next session has no way to check
your work against the original, so accuracy here is critical.

## Source Integrity

- Use only information present in the conversation or verified in the
  repository (file contents, `git status`, `git log`, `git diff`). If the
  conversation and the repository disagree, report the conflict under
  "Open Questions and Blockers" instead of choosing one silently.
- Never reconstruct, infer, guess, repair, or fill gaps in missing,
  truncated, or corrupted text. Write "Cannot be verified: <what is
  missing>" instead.
- Label the source of every rule, constraint, and acceptance criterion:
  **user** (quote the message), **document** (give path and line numbers),
  or **assistant-inferred**. Never present an assistant-inferred
  constraint or an unapproved proposal as a user rule.
- Label every decision as **user-approved** or **assistant-proposed**.
- Never treat an assistant's proposed reconstruction as an approved user
  decision unless the user explicitly confirmed it.
- Do not claim an artifact was created, edited, deleted, or committed
  unless the conversation or the repository shows it.
- Never drop a superseded rule. A fresh session can only reject a stale
  rule if it knows that rule was replaced. Superseded rules go in
  "Requirement Transitions", never in "Rules and Constraints in Effect".
- Do not claim rules "have never changed" beyond what you can verify.
  Scope any such statement to this session.

---

## HANDOFF DOCUMENT

**Provenance:** Run <run ID> · base commit <SHA> · source files: <paths>
(Run ID as stated by the user; base commit from `git rev-parse --short
HEAD` when the handoff is written, noting any uncommitted changes; source
files are every file this handoff quotes or describes. Write "Cannot be
verified" for any value you cannot confirm.)

### Overall Task
(One paragraph describing what this workflow is trying to achieve
and why. Written so that someone with no prior context understands
the goal.)

### Work Completed So Far
(What was accomplished before this handoff, phase by phase if the
work had phases. Include what was produced and what decisions were
made, each labelled user-approved or assistant-proposed. Be specific.)

### Current State of All Artifacts
(For each file or document section that was created, modified, or
deleted, give:
- Path, or section name if it is not a file.
- Status: created / modified / deleted / unchanged; committed (SHA)
  or uncommitted.
- Verified by: file read, `git diff`, `git log`, or "Unverified --
  conversation only".
- The full current text, verbatim. Do not summarize it. If a file is
  too long to include, give its path and commit SHA, quote every
  changed section verbatim, and say that the rest was omitted.)

### Requirement Transitions
(Record every point in the session where a requirement changed, in
order. For each transition give:
- Boundary: when it took effect (phase boundary, user message, or
  commit).
- Changed: the old requirement verbatim -> the new requirement
  verbatim, with sources.
- Why: the reason given. If no reason was given, write "No reason
  given."
- Still active: earlier requirements that remain in force, verbatim.
- Superseded: requirements that no longer apply, verbatim, each
  marked SUPERSEDED.
- Decisions still relevant: earlier decisions that still hold under
  the new requirement.
- Artifacts to revisit: files or sections that must be rechecked under
  the new requirement.
If no requirement changed, write "None -- no requirement changed
during this session.")

### Rules and Constraints in Effect
(The complete set of rules the next session must follow, copied
verbatim, each with its source. Include only rules that are active
now; superseded rules appear only in "Requirement Transitions".)

**Active rules:**

**Assistant-inferred working constraints (not user rules):**
(Write "None." if there are none.)

### Next Session Instructions
(Numbered list of exactly what the next session needs to do, in
the order it should do it. Be specific. If a step depends on an
unresolved item, point to it in "Open Questions and Blockers"
instead of guessing an answer.)

1.
2.
3.

### Acceptance Criteria for the Next Session
(Verbatim from the original task specification, with source. If no
acceptance criteria were given, write "None given." List any checks
you derived yourself separately, under the heading "Assistant-proposed
checks (not user criteria)".)

### Open Questions and Blockers
(Anything unresolved: missing or unverifiable information,
unapproved proposals, conflicts between sources, and tool or
environment blockers. The next session should raise these with the
user, not resolve them by assumption. If there are none, write
"None.")

### Known Constraints and Gotchas
(Anything the next session should be aware of that is not obvious
from the instructions above. Edge cases, prior decisions that
constrain future choices, things that were tried and did not work.)

---

Before presenting the handoff, check that:
- No rule marked SUPERSEDED appears in "Rules and Constraints in Effect".
- Every artifact listed under "Artifacts to revisit" also appears in
  "Current State of All Artifacts".
- Every rule and criterion has a source label.

After producing the handoff document, ask the user to review it for completeness and accuracy before closing the session. The user may edit it before using it to start the next session.
