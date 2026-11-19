# Lessons Learned — Retrieval Ground Truth

Corpus: `.memory/reference/lesson-*.md` (7 internal/public lessons), plus the confidential test fixture `test-fixtures/retrieval/lesson-datasource-credentials.md` for Q7–Q8. Confidential material is not kept in `.memory/` (CLAUDE.md write policy), so exclusion tests run against a test corpus that combines both folders.
Project: `proj-lessons` for every query
Ceiling: `internal` for every query (the server maximum, `RETRIEVAL_MAX_CEILING=internal`)
Pass threshold: expected document in the top 3, with `similarity_score >= 0.65` (or returned by the keyword fallback)

| # | Query | Expected source_document | Pass condition |
|---|---|---|---|
| Q1 | "What should I check before moving money between two accounts?" | `lesson-transfer-validate-target-first.md` | In top 3 |
| Q2 | "How should new backend code write log messages?" | `lesson-slf4j-not-println.md` | In top 3 |
| Q3 | "Why can't chmod make the knowledge folder read-only for the agent?" | `lesson-knowledge-readonly-mount.md` | In top 3 |
| Q4 | "The coursetools MCP server fails with connection closed. Which Python package version do I need?" | `lesson-pin-mcp-sdk-v1.md` | In top 3 |
| Q5 | "How do I make sure an agent's granted tool names actually exist on the server?" | `lesson-agent-frontmatter-and-tool-ids.md` | In top 3 |
| Q6 | "What should the agent do when a memory entry has no review date?" | `lesson-stale-memory-review-dates.md` | In top 3 |
| Q7 (exclusion) | "Where are the database username and password configured?" | none (`lesson-datasource-credentials.md` is `confidential`) | `lesson-datasource-credentials.md` is **not** in any result |
| Q8 (enforced ceiling) | Same as Q7, with `classification_ceiling = confidential` | none | The server **rejects** the request: "exceeds the server maximum 'internal'" |

Q7 tests the ceiling filter. Q8 tests that the ceiling is enforced by the server, not left to the caller.
