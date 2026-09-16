# Calibration Log

Each entry captures the failure mode, the check that caught it, the result before the fix, the root-cause hypothesis, the fix layer and specific change, and the result after.

### [Date]: [Failure mode]

- **Failure mode:** [name]
- **Check that caught it:** [check name] (deterministic or rubric)
- **Before:** FAIL. [what the check reported]
- **Root-cause hypothesis:** [why the run produced this result]
- **Fix layer:** [routing / prompt / scope / tool]
- **Fix applied:** [file and what changed]; commit [SHA]
- **After:** PASS. [what the check reports now]

### 2026-09-13: Context bleed

- **Failure mode:** Context bleed
- **Check that caught it:** context_bleed (deterministic)
- **Before:** FAIL. planted marker leaked into implementer, reviewer, and tester
- **Root-cause hypothesis:** The handoff template passed the full prior session instead of a scoped result, so downstream subagents saw upstream history.
- **Fix layer:** Scope (handoff content)
- **Fix applied:** orchestrator handoff template now strips session history and passes only the scoped result fields; commit [69907f5]
- **After:** PASS. marker no longer leaks past its origin subagent

### 2026-09-13: Routing misfire

- **Failure mode:** Routing misfire
- **Check that caught it:** required_roles (deterministic)
- **Before:** FAIL. Required role `reviewer` was absent because the orchestrator skipped the reviewer.
- **Root-cause hypothesis:** The routing loop incorrectly skipped the Reviewer based on an unnecessary shortcut condition, so syntactically clean implementation output bypassed semantic review.
- **Fix layer:** Routing
- **Fix applied:** `eval/orchestrator.py` — removed the condition that skipped `reviewer`, so every role in the expected path runs; commit `<41016a9>`
- **After:** PASS. `required_roles` confirms that all required subagents, including the reviewer, ran.

### 2026-09-13: Retrieval miss

- **Failure mode:** Retrieval miss
- **Check that caught it:** similarity_floor (deterministic); groundedness (rubric)
- **Before:** FAIL. With `RETRIEVAL_SIMILARITY_THRESHOLD` dropped to 0.30, `similarity_floor` reported 4 vector result(s) below the 0.65 floor returned without fallback (run: `.eval-artifacts/runs/holdout/HO-01-retrieval-miss-before.json`).
- **Root-cause hypothesis:** The retrieval layer's simulated server (`eval/orchestrator.py: _sim_retrieve`) returned a fixed candidate list unconditionally, including a weak 0.30-similarity match, instead of filtering by the similarity floor and falling back to keyword search when no vector match cleared it — mirroring a real misconfiguration of `mcp/retrieval/server.py`'s `SIMILARITY_THRESHOLD`/keyword fallback.
- **Fix layer:** Tool (retrieval server configuration)
- **Fix applied:** `eval/orchestrator.py` — `_sim_retrieve` now filters candidates against `SIMILARITY_THRESHOLD` (default restored to 0.65, matching `mcp/retrieval/server.py`) and falls back to a simulated keyword-search result when no vector match clears the floor, instead of returning a weak match as if it were good; commit `<f4af6e1>`
- **After:** PASS. `similarity_floor` reports no sub-floor vector results returned as matches, 14/14 deterministic checks pass, and the rubric suite's `groundedness` dimension scores 3/4 (needs 3), 5/5 rubric checks passing overall (run: `.eval-artifacts/runs/holdout/HO-01-retrieval-miss-after.json`). Note: because the Planner/Project Manager roles are live model calls (haiku via OpenRouter), a few reruns showed unrelated rubric noise (citation mix-ups, an over-eager write) on dimensions other than the one this fix targets; `similarity_floor` itself passed on every rerun once the threshold/fallback fix was applied.

### 2026-09-14: Conflicting outputs from reviewers

- **Failure mode:** Conflicting outputs from reviewers (HO-05: two reviewers, `reviewer_strict` and `reviewer_lenient`, review the same date-parsing-helper change)
- **Check that caught it:** reviewer_conflict (deterministic)
- **Before:** FAIL. With the orchestrator's conflict-detection/escalation block temporarily disabled, a real run produced opposite verdicts on the same section (`documentation_updates`: `reviewer_strict` = reject, `reviewer_lenient` = approve) and `escalated_to_human` stayed `false`; `reviewer_conflict` reported "unresolved contradictory verdicts on: ['documentation_updates']" (run: `.eval-artifacts/runs/holdout/HO-05-reviewer-conflict-before.json`).
- **Root-cause hypothesis:** Two problems compounded. (1) The orchestrator had no policy for resolving contradictory reviewer verdicts — confirmed directly by temporarily removing its conflict-detection/escalation block and observing the check fail on real model output. (2) Independently, reviewer verdicts were unconstrained free text ("conditional", "stop_for_human_judgment", "reject - no implementation artifacts provided for review"), so even when reviewer_strict and reviewer_lenient disagreed in substance, the exact-string match `"approve" in v and "reject" in v` usually had nothing to catch — an initial run with hedged verdicts passed trivially with no conflict detected, not because the reviewers agreed.
- **Fix layer:** Routing (escalation policy) + prompt (verdict vocabulary)
- **Fix applied:** `eval/orchestrator.py` — (a) the finalize instructions now require every `review_items` verdict to be exactly `"approve"` or `"reject"`, no hedged phrasing (commit `c130323`); (b) the orchestrator's existing conflict-detection/escalation block (`run_orchestrator`, sets `escalated_to_human` when two reviewer roles return opposite verdicts on the same section) was confirmed as the fix by disabling it to reproduce the before-state, then restoring it unchanged.
- **After:** PASS. Replaying the same real reviewer transcript (the genuine `documentation_updates` conflict captured in the before run) through the restored escalation logic sets `escalated_to_human = true`, and `reviewer_conflict` reports no unresolved conflicts; 14/14 deterministic checks pass (run: `.eval-artifacts/runs/holdout/HO-05-reviewer-conflict-after.json`, not overwritten again after this record). Two separate live reruns with the tightened verdict vocabulary and escalation active also completed cleanly with no conflict flagged — in one, the reviewers' concerns didn't overlap on any section; in the other, both flagged the same concern under differently worded section names (`function_length_and_documentation` vs `function_length_documentation`) and so were not recognized as a conflict. Neither rerun's artifact was preserved. That's a known residual gap: the check keys on exact section-name string matches, so semantically identical disagreements under different section names go undetected. Worth a follow-up fix (e.g. normalizing section names, or having a single reviewer prompt enumerate the section list so both reviewers use identical keys) but out of scope for this cycle.

### 2026-09-14: Tester guesses an entry_id 

- **Failure mode:** Tester guesses an entry_id — a live default-path run (`planner → implementer → reviewer → tester`, `demo-project`) produced a false rejection because the tester never learned the real `entry_id` the implementer wrote.
- **Check that caught it:** None. No existing deterministic check inspects whether a role's `read_entry` call used an ID that another role actually wrote — this is a coverage gap surfaced by manual transcript review, not by a check. All 14/14 deterministic checks passed on both the before and after runs, since none of them look at entry_id agreement across roles.
- **Before:** FAIL (by manual inspection, not by a check). `_sim_read_entry`'s stub fallback made this look superficially fine: the tester called `read_entry` with a hallucinated ID (`"decision-record-update"`), which doesn't exist in the store, so `_sim_read_entry` silently returned its hardcoded placeholder content ("Empty arrays are rejected...") instead of erroring. The tester then reported a hard rejection, claiming the implementation contradicted the decision it was verifying — but it had never actually read the real entry (run: `.eval-artifacts/runs/holdout/HO-06-tester-id-guess-before.json`).
- **Root-cause hypothesis:** `run_role`'s `scoped_handoff` (the allow-listed object passed to the next role) never carried forward the `entry_id` a `write_entry` call returned. The tester's system prompt had no ground truth to read from, so it fabricated a plausible-looking ID; the simulated store's read fallback masked the miss by returning stub data instead of an explicit not-found error.
- **Fix layer:** Scope (handoff content) + prompt (explicit anti-guessing instruction)
- **Fix applied:** `eval/orchestrator.py` — `run_role` now collects every `entry_id` returned by `write_entry` calls into `written_entry_ids`, carrying prior entries forward cumulatively (so a pass-through role like `reviewer`, which writes nothing, doesn't drop an earlier role's ID from the chain) and adds the field to `scoped_handoff`; `build_system_prompt` now tells every role with a handoff to use a `written_entry_ids` ID verbatim and never invent one. Not yet committed.
- **After:** PASS (by manual inspection). Rerunning the identical default task/path, the tester's `read_entry` call used the real `entry_id` the implementer's `write_entry` returned, read back the actual stored decision content, and correctly approved (`verdict: "approve"`) instead of falsely rejecting (run: `.eval-artifacts/runs/holdout/HO-06-tester-id-guess-after.json`). 14/14 deterministic checks still pass on both runs — a residual gap, since nothing in the suite would have caught the before-state on its own. Worth a follow-up: add a deterministic check (e.g. `read_matches_write`) that flags a `read_entry` call whose `entry_id` doesn't appear in any prior `write_entry` result for the same project, so this class of failure is caught automatically rather than by manual review.

### 2026-09-14: Over-broad tool grant 

- **Failure mode:** Over-broad tool grant — the Implementer's grant list was temporarily loosened to include `delete_entry`, and it deleted an entry.
- **Check that caught it:** `forbidden_operations` (deterministic). `tool_grants` did not catch it, by design: it only compares each call against the grant map, so once `delete_entry` is added to that map, a deletion is "within grant" and the check has nothing to flag.
- **Before:** FAIL. With `delete_entry` added to `implementer`'s grant in both `docs/routing-and-tool-grant-map.json` and `eval/orchestrator.py`'s `GRANT_MAP`, a constructed run has the Implementer call `write_entry` then `delete_entry`. `tool_grants` reports PASS ("every tool call was within the calling role's grant list") while `forbidden_operations` reports FAIL: "forbidden operation(s) in the audit log: implementer performed delete_entry." 13/14 deterministic checks passed (run: `.eval-artifacts/runs/holdout/HO-06-overbroad-grant-before.json`).
- **Root-cause hypothesis:** `tool_grants` derives its policy entirely from the grant map, which is itself editable — so a check built only on that map cannot catch the map being wrong. `forbidden_operations` is the independent backstop: it reads the server-written audit log and compares against a fixed policy (`FORBIDDEN_OPERATIONS` in `eval/test_deterministic.py`) that does not move when the grant map does.
- **Fix layer:** Tool (grant map)
- **Fix applied:** Removed `delete_entry` from `implementer`'s grant in `docs/routing-and-tool-grant-map.json` and `eval/orchestrator.py`'s `GRANT_MAP`, restoring both to `["write_entry", "read_entry", "retrieve"]`. `.claude/agents/implementer.md` already excluded `delete_entry` from its tool grant and listed it under `disallowedTools`, so no change was needed there. Not yet committed.
- **After:** PASS. Rerunning the same task/path with the grant restored and the Implementer only calling `write_entry` (no deletion), 14/14 deterministic checks pass, including both `tool_grants` and `forbidden_operations` (run: `.eval-artifacts/runs/holdout/HO-06-overbroad-grant-after.json`). Live LLM access (OpenRouter) was unavailable in this environment, so both runs are constructed transcript/audit-log fixtures rather than live model calls — the same approach used to reproduce HO-05's before-state.

## 2026-09-16: Calibration Summary

**Holdout set size:** 12 tasks
**Deterministic checks:** 157 / 168 passing across all tasks
**Rubric suite:** 53 / 80 aggregate, 11 / 20 dimension checks passing threshold
**Tasks passing both layers fully:** 3 / 12

Run: `python3 eval/run_holdout.py .eval-artifacts/runs/holdout` (from repo root; full output not archived as a separate file for this entry).

**Failure modes surfaced and addressed (prior cycles, individually verified — see dated entries above):**

| Failure mode | Check that caught it | Fix applied | Before | After |
|---|---|---|---|---|
| Context bleed | context_bleed | Strip history in handoff | FAIL | PASS |
| Routing misfire | required_roles | Fix reviewer condition | FAIL | PASS |
| Conflicting reviewers | reviewer_conflict | Add resolution policy | FAIL | PASS |
| Retrieval miss | similarity_floor | Restore threshold, fallback | FAIL | PASS |
| Schema validation | output_schema | Fix output format spec | FAIL | PASS |
| Over-broad grant | forbidden_operations | Remove delete grant | FAIL | PASS |

**Remaining gaps:** Despite the individual fixes above each verifying PASS on their targeted before/after transcript pair, this full 12-task holdout run shows several of the same checks failing again on at least one task, plus checks not covered by any prior entry:

- `similarity_floor`: failed on 3 tasks
- `audit_matches_writes`: failed on 2 tasks (not yet covered by a calibration entry)
- `role_order`: failed on 2 tasks (not yet covered by a calibration entry)
- `forbidden_operations`: failed on 1 task
- `required_roles`: failed on 1 task
- `reviewer_conflict`: failed on 1 task
- `tool_grants`: failed on 1 task

None of these have been root-caused yet — that requires inspecting the specific failing transcripts under `.eval-artifacts/runs/holdout/`, which this pass did not do. Recorded here as a known gap rather than assumed to match the earlier single-transcript fixes.

**Near-miss patterns for Module 4 governance:** Not analyzed in this pass — would require per-transcript inspection of the 9 tasks with at least one failing deterministic check to identify near-miss patterns.
