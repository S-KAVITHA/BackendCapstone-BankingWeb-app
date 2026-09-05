# Persistent Storage MCP Server Schema

Server name: `storage`

Default endpoint: `http://localhost:8001/mcp`

Runtime files:

- Database: `/memory/storage.db`
- Audit log: `/memory/storage-audit.log`

The server exposes five named operations. It does **not** expose raw SQL.

## Classification rules

Allowed classification vocabulary:

- `public`
- `internal`
- `confidential`
- `secret`

This storage server accepts writes only at `public` or `internal`. Attempts to write `confidential` or `secret` entries are rejected by the server.

## Project scoping

Every operation takes `project_id`. The project identifier must match:

```text
^[A-Za-z0-9-]+$
```

Every read, list, update, and delete operation applies the project filter in the server query.

## Audit rules

Every write-class operation appends one JSON line to `/memory/storage-audit.log`:

- `timestamp`
- `operation`
- `project_id`
- `entry_id`
- `classification`
- `calling_role`

The server exposes no tool for editing or erasing the audit log.

## `write_entry`

Writes a new entry to the store.

Parameters:

- `project_id` (`str`, required): letters, numbers, and hyphens only.
- `entry_type` (`str`, required): caller-defined category, such as `decision`, `plan`, `review`, or `test-result`.
- `title` (`str`, required): short human-readable title.
- `content` (`str`, required): full entry text.
- `classification` (`str`, required): one of `public`, `internal`, `confidential`, `secret`; this server rejects `confidential` and `secret` writes.
- `calling_role` (`str`, optional): role name supplied by the caller; defaults to `unknown`.

Validation:

- `project_id` must match `^[A-Za-z0-9-]+$`.
- `entry_type`, `title`, and `content` must be non-empty strings.
- `classification` must be one of the four allowed values.
- `classification` must be `public` or `internal` to be stored.

Returns:

```json
{ "entry_id": "<uuid>" }
```

Audit:

- Appends a `write_entry` line to `storage-audit.log`.

Example:

```text
write_entry(
  "proj-csv",
  "decision",
  "Use streaming CSV writer",
  "Chosen to keep memory low on large exports.",
  "internal",
  "implementer"
)
```

## `read_entry`

Reads a single live entry by `project_id` and `entry_id`.

Parameters:

- `project_id` (`str`, required)
- `entry_id` (`str`, required)

Validation:

- `project_id` must match `^[A-Za-z0-9-]+$`.
- The entry must exist, belong to the project, and not be soft-deleted.

Returns:

```json
{
  "entry_id": "<uuid>",
  "project_id": "proj-csv",
  "entry_type": "decision",
  "title": "Use streaming CSV writer",
  "content": "Chosen to keep memory low on large exports.",
  "classification": "internal",
  "deleted": 0,
  "last_updated": "<iso-8601 timestamp>"
}
```

Audit:

- No audit line is appended for reads.

## `list_entries`

Lists live entries for one project. Returns metadata only; never returns full `content`.

Parameters:

- `project_id` (`str`, required)
- `entry_type` (`str`, optional): narrows the list to one category.

Validation:

- `project_id` must match `^[A-Za-z0-9-]+$`.
- If provided, `entry_type` must be non-empty.

Returns:

```json
[
  {
    "entry_id": "<uuid>",
    "title": "Use streaming CSV writer",
    "entry_type": "decision",
    "classification": "internal",
    "last_updated": "<iso-8601 timestamp>"
  }
]
```

Audit:

- No audit line is appended for list operations.

## `update_entry`

Updates the `content` of an existing live entry. The classification is preserved.

Parameters:

- `project_id` (`str`, required)
- `entry_id` (`str`, required)
- `content` (`str`, required)
- `calling_role` (`str`, optional): defaults to `unknown`.

Validation:

- `project_id` must match `^[A-Za-z0-9-]+$`.
- The entry must exist, belong to the project, and not be soft-deleted.
- `content` must be non-empty.

Returns:

```json
{ "success": true }
```

Audit:

- Appends an `update_entry` line with the entry's existing classification.

## `delete_entry`

Soft-deletes an existing live entry by marking `deleted = 1`. It does not remove the row.

Parameters:

- `project_id` (`str`, required)
- `entry_id` (`str`, required)
- `calling_role` (`str`, optional): defaults to `unknown`.

Validation:

- `project_id` must match `^[A-Za-z0-9-]+$`.
- The entry must exist, belong to the project, and not already be soft-deleted.

Returns:

```json
{ "success": true }
```

Audit:

- Appends a `delete_entry` line with the entry's existing classification.

## Design notes

- Raw query access is intentionally absent.
- `list_entries` is intentionally metadata-only.
- Deletes are soft deletes so record history and audit evidence survive.
- The server records `calling_role` supplied by the caller. The orchestrator or harness should ensure every subagent passes its role name.

## retrieve

Searches the reference corpus by meaning, scoped to one project and capped
at a classification ceiling. Falls back to keyword search when no vector
match clears the confidence threshold.

Parameters:
- query (str, required): the search text, phrased as the agent would ask it
- project_id (str, required): only this project's documents are searched
- top_k (int, optional, default 3): maximum results to return
- classification_ceiling (str, optional, default "internal"): the highest
  classification a result may carry; one of public, internal, confidential, secret
- metadata_filters (dict, optional): extra narrowing, e.g. {"doc_type": "decision"}

Returns: a list of up to top_k results. Each result always includes:
- source_document (str): the file the chunk came from   [citation]
- chunk_index (int): the chunk's position in that file   [citation]
- excerpt (str): the chunk text
- classification (str): the chunk's classification, never above the ceiling
- similarity_score (float or null): cosine similarity for vector results;
  null for keyword results
- retrieval_method (str): "vector" or "keyword"

Confidence threshold: 0.65. If no vector result reaches it, keyword fallback runs.
Classification: results above classification_ceiling are never returned.
Citation: source_document and chunk_index are present on every result.

Example: retrieve("What file format did we choose for the export?",
"proj-csv", classification_ceiling="internal")