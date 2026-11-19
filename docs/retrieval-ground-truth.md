# Retrieval Ground Truth (`proj-csv`)

Corpus: the `proj-csv` documents from `.memory/reference/` (commit `d5ee4f9`).
Runner: `mcp/retrieval/run_ground_truth.py`. Results: `docs/retrieval-quality-report.md`.

## Reconciliation before execution (2026-11-19)

The queries and pass rules below are unchanged from `d5ee4f9`. Before the first executed run, only the names were corrected to match the documents actually committed in that same commit, so each test still checks what it was written to check:

| Query | Written as | Corrected to | Why |
|---|---|---|---|
| Q2, Q3 | `csv-export.md`, `csv-import.md` | `feature-csv-export.md`, `feature-csv-import.md` | No `csv-export.md` exists; the export/import documents are named `feature-*`. |
| Q4 | `csv-export-errors.md`, code `E_CSV_001` | `error-codes.md`, code `E_EXPORT_417` | No such file or code exists. `error-codes.md` defines `E_EXPORT_400`–`E_EXPORT_503`; `E_EXPORT_417` is the code the runner in `d5ee4f9` already used. The test is still an exact error-code lookup. |
| Q5 | `cost-breakdown.md` | unchanged | The confidential document was removed from `.memory/` in `a1d1b15` (memory policy). For this test it is restored from git into a **separate test corpus** outside `.memory/`, so the ceiling is tested against a real confidential document. |

No expected answer was changed after seeing results.

## Q1 — Export file format decision
Query: "What file format did we choose for exporting the task list?"

Expected top result: decision-csv-format.md

Filters: project = proj-csv, classification_ceiling = internal

Pass: decision-csv-format.md appears in top 3 with score >= 0.65

## Q2 — CSV export implementation
Query: "How does the CSV export feature work?"

Expected top result: feature-csv-export.md

Filters: project = proj-csv, classification_ceiling = internal

Pass: feature-csv-export.md appears in top 3 with score >= 0.65

## Q3 — CSV export near-miss
Query: "How do we export tasks to CSV?"

Expected top result: feature-csv-export.md

Filters: project = proj-csv, classification_ceiling = internal

Pass: feature-csv-export.md appears in top 3 and ranks above feature-csv-import.md

## Q4 — Exact error lookup
Query: "What does error code E_EXPORT_417 mean?"

Expected top result: error-codes.md

Filters: project = proj-csv, classification_ceiling = internal

Pass: error-codes.md appears in top 3 with score >= 0.65

## Q5 — Classification ceiling
Query: "What are the internal cost figures for the export feature?"

Expected top result: cost-breakdown.md

Filters: project = proj-csv, classification_ceiling = internal

Pass: cost-breakdown.md does NOT appear in results