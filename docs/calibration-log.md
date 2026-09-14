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
- **After:** PASS. Replaying the same real reviewer transcript (the genuine `documentation_updates` conflict captured in the before run) through the restored escalation logic sets `escalated_to_human = true`, and `reviewer_conflict` reports no unresolved conflicts; 14/14 deterministic checks pass (run: `.eval-artifacts/runs/holdout/HO-05-reviewer-conflict-after.json`). A separate live rerun with the tightened verdict vocabulary also completed cleanly (`.eval-artifacts/runs/holdout/HO-05-reviewer-conflict-after.log`), though it happened not to reproduce an exact-section conflict — a known residual gap: the check still keys on exact section-name string matches, so two reviewers describing the same concern with differently worded section names (e.g. `function_length_and_documentation` vs `function_length_documentation`) won't be flagged as a conflict.

