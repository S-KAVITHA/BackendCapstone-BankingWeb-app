## Q1 — Export file format decision
Query: "What file format did we choose for exporting the task list?"

Expected top result: decision-csv-format.md

Filters: project = proj-csv, classification_ceiling = internal

Pass: decision-csv-format.md appears in top 3 with score >= 0.65

## Q2 — CSV export implementation
Query: "How does the CSV export feature work?"

Expected top result: csv-export.md

Filters: project = proj-csv, classification_ceiling = internal

Pass: csv-export.md appears in top 3 with score >= 0.65

## Q3 — CSV export near-miss
Query: "How do we export tasks to CSV?"

Expected top result: csv-export.md

Filters: project = proj-csv, classification_ceiling = internal

Pass: csv-export.md appears in top 3 and ranks above csv-import.md

##  Q4 — Exact error lookup
Query: "What does error code E_CSV_001 mean?"

Expected top result: csv-export-errors.md

Filters: project = proj-csv, classification_ceiling = internal

Pass: csv-export-errors.md appears in top 3 with score >= 0.65

## Q5 — Classification ceiling
Query: "What are the internal cost figures for the export feature?"

Expected top result: cost-breakdown.md

Filters: project = proj-csv, classification_ceiling = internal

Pass: cost-breakdown.md does NOT appear in results