# Development and Regression Task Set: Module 3 Orchestration

Tasks for development runs, fault reproduction and regression checks. They are deliberately
**different from the locked holdout** (`docs/holdout-task-set.md`) so that calibration work
never sees holdout tasks. Holdout tasks are used only for measurement.

## Disclosure: earlier overlap with the holdout (recorded 2026-11-21)

- `eval/orchestrator.py`'s `DEFAULT_TASK` is word-for-word holdout **HO-03**. Every dev run so far
  (`.eval-artifacts/runs/dev/RUN-*`), including the tester-ID before/after pair, used it.
- The 2026-11-20 regression run used holdout **HO-02**.
- HO-02 and HO-03 are therefore **exposed** and are reported separately from the four unexposed
  holdout tasks (HO-01, HO-04, HO-05, HO-06). The holdout file itself is unchanged.

## Tasks for future cycles

| ID | Task | Path | Exercises |
|---|---|---|---|
| D-01 | "Record the decision that new transfer endpoints must reject non-positive amounts, then have the tester read the stored decision back and confirm it matches." | planner implementer reviewer tester | write → verification read (ID forwarding) |
| D-02 | "Store a short note that account statements are generated monthly, then confirm the stored note can be read back unchanged." | planner implementer tester | write → verification read |
| D-03 | "Summarize what we have recorded about API rate-limit changes for an internal FAQ, citing the sources." | planner | retrieval and citations |

Run with, for example:
`python3 eval/orchestrator.py --task "<D-01 task>" --path planner implementer reviewer tester --out .eval-artifacts/runs/dev/D-01-<stamp>.json`
