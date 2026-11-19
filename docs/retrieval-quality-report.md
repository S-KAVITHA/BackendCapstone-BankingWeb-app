# Retrieval Quality Report

## Measured: Lessons Learned corpus (`proj-lessons`), Run 032, 2026-11-19

Measured output, not expected values. Source: `docs/transcripts/run-032-ground-truth.txt`, produced by `python3 mcp/retrieval/run_lessons_ground_truth.py` against the live retrieval server.

- Corpus: 8 `lesson-*.md` documents in `.memory/reference/` (7 `internal`/`public`, 1 `confidential`); the server indexed 44 chunks in total (all projects). After this run, the confidential lesson was moved to `test-fixtures/retrieval/` to keep confidential material out of `.memory/`; re-runs use a test corpus that combines both folders.
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

## Measured: `proj-csv` ground truth, Run 033, 2026-11-19

Measured output, replacing the earlier prefilled "PASS expected" values. Source: `docs/transcripts/run-033-csv-ground-truth.txt`, produced by `RETRIEVAL_URL=http://localhost:8003/mcp python3 mcp/retrieval/run_ground_truth.py`.

- Queries: the five in `docs/retrieval-ground-truth.md`. Only document names were corrected before execution (see the reconciliation table there); queries and pass rules are unchanged.
- Corpus: a separate test corpus in `/tmp/csv-corpus`: the seven `proj-csv` documents from `.memory/reference/` plus `cost-breakdown.md` (`confidential`), restored from git (`a1d1b15^`) so that Q5 tests a real confidential document without putting it back into `.memory/`.
- Server: `mcp/retrieval/server.py` on :8003, paragraph chunking, model `all-MiniLM-L6-v2`, `RETRIEVAL_MAX_CEILING=internal`. Ceiling `internal`, `top_k` 3, threshold `0.65`.

| Query | Pass rule | Result | Returned (source#chunk, method, score) |
|---|---|---|---|
| Q1 format decision | `decision-csv-format.md` in top 3, score ≥ 0.65 | **PASS** | 1. `decision-csv-format.md#chunk0`, vector, 0.759 |
| Q2 export implementation | `feature-csv-export.md` in top 3, score ≥ 0.65 | **PASS** | 1. `feature-csv-export.md#chunk0`, vector, 0.699 · 2. `api-spreadsheet-library.md#chunk0`, vector, 0.672 |
| Q3 export near-miss | `feature-csv-export.md` in top 3, above `feature-csv-import.md` | **PASS** | 1. `decision-csv-format.md#chunk0`, vector, 0.711 · 2. `feature-csv-export.md#chunk0`, vector, 0.654 (import not returned) |
| Q4 exact error code `E_EXPORT_417` | `error-codes.md` in top 3, score ≥ 0.65 | **PASS** | 1. `error-codes.md#chunk2`, vector, 0.749 · 2. `error-codes.md#chunk0`, vector, 0.654 |
| Q5 confidential cost figures | `cost-breakdown.md` absent | **PASS** | 1. `decision-csv-format.md#chunk0` · 2. `feature-csv-import.md#chunk0` · 3. `error-codes.md#chunk2` (all keyword fallback) |

**5/5 passed.**

### What the results show

- **Thin margins.** Q3 passed with the export chunk at 0.654, only 0.004 above the threshold, and ranked second behind the format *decision* (0.711). In Q2 the library reference (0.672) scored close to the expected document (0.699). Paragraph chunks that mix several topics put related documents close together.
- **Exact codes worked by vector.** Q4 was expected to need the keyword fallback, but paragraph chunking put the `E_EXPORT_417` paragraph in its own chunk (`#chunk2`, 0.749), so the vector match succeeded.
- **The ceiling held, with one limitation.** In Q5, no allowed document reached 0.65, so the server fell back to keyword search and returned three unrelated internal chunks; the confidential document was never returned. The transcript proves its absence, not its rank. The server-side rejection of a `confidential` ceiling is shown separately in Run 032 (Q8).

### Chunking comparison: not run

Semantic chunking was not measured. The Q3 margin (0.654) is the evidence for trying it next: if semantic chunking separates the "how export works" paragraph from the format discussion, the export chunk should score higher and rank first. Keep the answer key unchanged and re-run Q2–Q3 with `--chunking semantic`.
