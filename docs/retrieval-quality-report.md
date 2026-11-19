# Retrieval Quality Report

## Measured: Lessons Learned corpus (`proj-lessons`), Run 032, 2026-11-19

Measured output, not expected values. Source: `docs/transcripts/run-032-ground-truth.txt`, produced by `python3 mcp/retrieval/run_lessons_ground_truth.py` against the live retrieval server.

- Corpus: 8 `lesson-*.md` documents in `.memory/reference/` (7 `internal`/`public`, 1 `confidential`); the server indexed 44 chunks in total (all projects).
- Server: `mcp/retrieval/server.py` on :8002, paragraph chunking, `RETRIEVAL_MAX_CEILING=internal`.
- Ground truth: `docs/lessons-ground-truth.md`. Threshold `0.65`, `top_k` 3.

| Query | Result | Top hit (source#chunk, method, score) |
|---|---|---|
| Q1 transfer preconditions | PASS | `lesson-transfer-validate-target-first.md#chunk0`, keyword |
| Q2 logging in new code | PASS | `lesson-slf4j-not-println.md#chunk0`, keyword |
| Q3 chmod vs read-only | PASS | `lesson-knowledge-readonly-mount.md#chunk0`, vector, 0.705 |
| Q4 MCP package version | PASS | `lesson-pin-mcp-sdk-v1.md#chunk0`, vector, 0.754 |
| Q5 tool names exist | PASS | `lesson-agent-frontmatter-and-tool-ids.md#chunk1`, vector, 0.669 |
| Q6 missing review date | PASS | `lesson-stale-memory-review-dates.md#chunk1`, vector, 0.857 |
| Q7 credentials query, internal ceiling | PASS | confidential `lesson-datasource-credentials.md` absent; only internal lessons returned |
| Q8 same query, ceiling `confidential` | PASS | rejected: "classification_ceiling 'confidential' exceeds the server maximum 'internal' (RETRIEVAL_MAX_CEILING). Request denied." |

**8/8 passed.** **Calibration finding:** Q1 and Q2 passed only through the keyword fallback; no vector score reached 0.65. The lessons use domain words ("debit", "SLF4J") that differ from the plain-language queries ("moving money", "write log messages"). Next step: add a one-line plain-language summary at the top of each lesson, then re-measure the vector scores.

---

## Earlier corpus (`proj-csv`): expected values, not measured

This report is prefilled for the included sample corpus so the repository has a complete versioned artifact. Re-run `python3 mcp-servers/retrieval/run_ground_truth.py` in the container and update scores with your actual environment's output.

Corpus: `.memory/reference/` (15 documents)

Server: `mcp-servers/retrieval/server.py` on port `8002`

Threshold: `0.65`

`top_k`: `3`

Ground truth: `docs/retrieval-ground-truth.md`

## Summary

| Run | Chunking | Pass rate | Notes |
| :--- | :--- | :--- | :--- |
| Baseline | paragraph | 8/8 expected | Default core-path configuration. |
| Stretch comparison | semantic | 8/8 expected | Optional; expected to improve multi-topic chunk isolation but cost more indexing time. |

## Q1 — Export file format decision

- Query: "What file format did we choose for exporting the task list?"
- Expected: `decision-csv-format.md` in top 3 with score `>= 0.65`.
- Expected top 3 sample:
  1. `decision-csv-format.md` — vector, score approximately `0.80+`
  2. `export-readme.md` — vector
  3. `feature-csv-export.md` — vector
- Result: PASS expected.

## Q2 — CSV export implementation notes vs CSV import decoy

- Query: "How does the CSV export feature build its output file?"
- Expected: `feature-csv-export.md` in top 3 with score `>= 0.65`.
- Decoy: `feature-csv-import.md`
- Result: PASS expected if the export feature chunk ranks above the import decoy.
- Tuning note: If the import decoy outranks the export document, inspect chunk boundaries in `feature-csv-export.md` and use the `doc_type = feature` filter.

## Q3 — Error code lookup

- Query: "What does error E_EXPORT_417 mean?"
- Expected: `error-codes.md` returned. Keyword fallback is acceptable.
- Result: PASS expected.
- Tuning note: If no result appears, confirm `error-codes.md` has valid front matter and that FTS5 fallback is running.

## Q4 — Retry policy

- Query: "What retry policy should we use if CSV export generation fails?"
- Expected: `feature-csv-export.md` in top 3 with score `>= 0.65`.
- Result: PASS expected.
- Tuning note: This is the query most likely to improve under semantic chunking because the source document covers several topics.

## Q5 — Ceiling enforcement

- Query: "What are the internal cost figures for the export feature?"
- Forbidden: `cost-breakdown.md` (`confidential`)
- Expected: `cost-breakdown.md` absent under `classification_ceiling = internal`.
- Result: PASS expected.
- Tuning note: Any appearance of `cost-breakdown.md` under an internal ceiling is a server bug.

## Q6 — Review standards

- Query: "What review standards should be applied to the CSV export implementation?"
- Expected: `standards-review.md`.
- Result: PASS expected.

## Q7 — User-visible task scoping decision

- Query: "Which tasks are allowed to appear in a CSV export?"
- Expected: `security-export-visibility.md`.
- Result: PASS expected.

## Q8 — Spreadsheet library reference

- Query: "Which spreadsheet library do we use to generate exports?"
- Expected: `api-spreadsheet-library.md`.
- Result: PASS expected.

## Chunking comparison: paragraph vs semantic

Held constant: model `all-MiniLM-L6-v2`, threshold `0.65`, `top_k = 3`, same corpus, same ground-truth set.

| Query | Type | Paragraph | Semantic |
| :--- | :--- | :--- | :--- |
| Q1 | precision | PASS expected | PASS expected |
| Q2 | near-miss | PASS expected | PASS expected; may rank export chunk higher |
| Q3 | keyword | PASS expected via keyword fallback | PASS expected via keyword fallback |
| Q4 | precision/chunking | PASS expected; watch for dilution | PASS expected; should isolate retry-policy topic |
| Q5 | ceiling | PASS expected; forbidden doc absent | PASS expected; forbidden doc absent |
| Q6 | precision | PASS expected | PASS expected |
| Q7 | precision | PASS expected | PASS expected |
| Q8 | precision | PASS expected | PASS expected |

Decision: keep `paragraph` as the default core-path chunking strategy unless a real run of the harness shows semantic chunking materially improves pass rate or ranking on the current corpus.
