# Context-Management Technique Plan

## Purpose
Preserve task intent, active requirements, artifact state, and decisions in multi-phase workflows to prevent requirement drift, repeated work, and context loss.

---

## 1. Explicit Context Boundaries
* **Where used:** At major phase transitions (analysis → execution, rule updates, session changes).
* **Format:** State completed work, next focus, active rules, changed rules, and prior key context.
* **Why:** Prevents rule-mixing during updates.

---

## 2. Proactive Summarization
* **Where used:** Before context reaches ~60% capacity or when starting new phases/sessions.
* **Contents:** Current goal, active rules, key decisions, artifact state, open questions, exact next action.
* **Why:** Preserves critical context without relying solely on chat history.

---

## 3. Context Verification
* **Where used:** When resuming work from a summary.
* **Action:** Inspect project files and config to verify actual state before making edits.
* **Why:** Grounding actions in actual files ensures higher reliability than memory.

---

## 4. Handoff Boundaries
* **Where used:** When work moves across sessions or agents.
* **Contents:** Goal, completed/remaining work, active/changed rules, decisions, file state, next action.
* **Why:** Ensures seamless session transitions without context loss.

---

## 5. Compaction Policy
* **Where used:** Secondary measure when context reaches ~60% capacity.
* **Process:** Create a summary **before** compaction. Review summary and verify files **after** compaction.
* **Why:** Offsets recall loss from compaction by anchoring to file state.

---

## 6. Expected Outcomes
* Eliminates requirement drift.
* Prevents duplicated work.
* Standardizes cross-session handoffs.
* Uses real project state as the source of truth.
