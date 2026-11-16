# Reference Memory Index

Last updated: 2026-11-16
Layer: `.memory/reference/` (indexed references)
Maintained by: humans. Agents have read-only access (deny rules in `.claude/settings.json`). Query by keyword and read only the excerpts relevant to the task. Never read the whole directory, and never write to this layer (see `CLAUDE.md`, "Memory layers").

## Purpose

This index lets an agent locate a reference document by keyword without reading the whole directory. Reference documents are not loaded at session start. The startup index is `.memory/project/MEMORY_INDEX.md`.

## Banking backend workflow

No reference documents are currently needed for the banking backend workflow. Add one when a large document is needed only for specific tasks, such as an API specification for the `Customer` and `Admin` endpoints or a database schema. See `docs/memory-architecture.md` §3.

## Retrieval-exercise corpus (`proj-csv`)

These documents were added in commit `d5ee4f9` (2026-09-05) as the corpus for the retrieval exercise (`docs/retrieval-ground-truth.md`). They describe a CSV export/import feature, not the banking backend. Use them only for tasks that name `proj-csv` or the retrieval workflow.

| File | doc_type | Classification | Keywords | Summary |
|---|---|---|---|---|
| [feature-csv-export.md](feature-csv-export.md) | feature | internal | export, streaming, batch 500, retry, backoff | How the CSV export streams tasks in batches of 500, and the retry policy (2s/4s/8s, 3 attempts). |
| [feature-csv-import.md](feature-csv-import.md) | feature | internal | import, validation, 10 MB, 5,000 rows, duplicates | How CSV import validates rows, its size and row limits, and how it detects duplicates by external ID. |
| [api-spreadsheet-library.md](api-spreadsheet-library.md) | reference | internal | csv-stream-writer, library, headers, escaping | How the export uses the csv-stream-writer 2.x library. |
| [error-codes.md](error-codes.md) | reference | internal | error codes, E_EXPORT_*, remediation | Error codes from the export service and how to fix each one. |
| [decision-csv-format.md](decision-csv-format.md) | decision | internal | format, CSV vs JSON/XLSX | Why CSV was chosen as the export format. |
| [security-export-visibility.md](security-export-visibility.md) | decision | internal | permissions, visibility, access control | Exports include only tasks the user can already read. |
| [standards-review.md](standards-review.md) | standard | internal | code review, streaming, dependencies | Review standards for pull requests that touch the export pipeline. |
| [cost-breakdown.md](cost-breakdown.md) | finance | **confidential** | — | **Restricted. Agents must not read, quote, or summarize it.** Listed only so that its presence is visible. A human should move it out of `.memory/` (CLAUDE.md, "Write policy"). |

## Retention

- Update or remove an entry when the information it references changes (`docs/memory-architecture.md`, §3).
- Add a row here whenever a document is added to this layer, and remove the row when the document is removed.
