## corpus: /tmp/csv-corpus (proj-csv docs from .memory/reference + cost-breakdown.md restored from a1d1b15^)
api-spreadsheet-library.md
cost-breakdown.md
decision-csv-format.md
error-codes.md
feature-csv-export.md
feature-csv-import.md
security-export-visibility.md
standards-review.md
/usr/local/lib/python3.14/dist-packages/fastmcp/server/auth/providers/jwt.py:10: AuthlibDeprecationWarning: authlib.jose module is deprecated, please use joserfc instead.
It will be compatible before version 2.0.0.
  from authlib.jose import JsonWebKey, JsonWebToken
Server: http://localhost:8003/mcp  project: proj-csv  ceiling: internal  top_k: 3  threshold: 0.65

Q1: PASS - decision-csv-format.md at rank 1, score 0.759
    query: "What file format did we choose for exporting the task list?"
    top 3: 1. decision-csv-format.md#chunk0 (internal, vector, score=0.759)

Q2: PASS - feature-csv-export.md at rank 1, score 0.699
    query: "How does the CSV export feature work?"
    top 3: 1. feature-csv-export.md#chunk0 (internal, vector, score=0.699) | 2. api-spreadsheet-library.md#chunk0 (internal, vector, score=0.672)

Q3: PASS - feature-csv-export.md at rank 2, above feature-csv-import.md
    query: "How do we export tasks to CSV?"
    top 3: 1. decision-csv-format.md#chunk0 (internal, vector, score=0.711) | 2. feature-csv-export.md#chunk0 (internal, vector, score=0.654)

Q4: PASS - error-codes.md at rank 1, score 0.749
    query: "What does error code E_EXPORT_417 mean?"
    top 3: 1. error-codes.md#chunk2 (internal, vector, score=0.749) | 2. error-codes.md#chunk0 (internal, vector, score=0.654)

Q5: PASS - cost-breakdown.md absent
    query: "What are the internal cost figures for the export feature?"
    top 3: 1. decision-csv-format.md#chunk0 (internal, keyword, score=None) | 2. feature-csv-import.md#chunk0 (internal, keyword, score=None) | 3. error-codes.md#chunk2 (internal, keyword, score=None)

5/5 passed
