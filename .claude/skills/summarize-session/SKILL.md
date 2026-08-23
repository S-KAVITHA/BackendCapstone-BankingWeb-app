---
name: summarize-session
description: >
  Produces a structured summary of the current session's decisions, rule
  changes, and outstanding work. Use at a natural workflow breakpoint before
  the context window fills, or when the user asks for a session summary.
---

# Skill: Summarize Session

## Source Integrity

- Use only information actually present in the conversation.
- Never reconstruct, infer, guess, repair, or fill gaps in truncated,
  corrupted, or missing text.
- Never treat an assistant's proposed reconstruction as an approved user
  decision unless the user explicitly confirmed it.
- Distinguish between user-provided rules, assistant proposals, user-approved
  decisions, and unresolved questions.
- If required information is missing, truncated, or corrupted, state that it
  cannot be verified instead of inventing or reconstructing the missing content.
- Preserve superseded rules in the "Rule Changes" section and clearly identify
  which rule is currently active.
- Do not claim that an artifact was edited, created, deleted, or finalized
  unless the conversation provides evidence that this happened.
- If the conversation contains conflicting statements, report the conflict
  rather than choosing one without evidence.

When this skill is invoked, produce a structured session summary using
exactly the format below.

Do not paraphrase acceptance criteria or rules
copy them verbatim from the conversation. Do not omit any modified
files or decisions. The summary will be used as the sole context for
the next phase of work, so it must be complete and accurate.

---

## SESSION SUMMARY

### Original Task and Acceptance Criteria
(Copy the original task description and all user-provided rules/criteria
verbatim. Do not paraphrase, shorten, repair, or reconstruct corrupted or
missing text.)

### Decisions Made So Far
(Numbered list. Each item states what was decided and the brief reason
why, if one was given.)

1.
2.
3.

### Rule Changes
(If any rules were updated, modified, or removed during the session,
list them here explicitly. State the original rule and what it changed
to. If no rules changed, write "None.")

### Current State of All Modified Artifacts
(For each file or document section that has been edited, provide the
filename or section name and a one-sentence description of its current
state. If the full current text is short enough to include, include it.)

### Outstanding Work Remaining
(Numbered list of the work that has not yet been done, in the order
it should be completed.)

1.
2.
3.

### Known Open Questions or Blockers
(Anything unresolved that the next phase needs to be aware of. If none,
write "None.")

---

After producing the summary, ask the user to review it for accuracy before proceeding. Do not continue with any task until the user confirms the summary is correct or provides corrections.
