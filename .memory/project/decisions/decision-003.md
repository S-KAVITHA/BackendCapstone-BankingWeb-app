# Decision 003 - Partial Updates

## Date
2026-08-25

## Review by
2026-11-23

## Status
Active

## Scope
Backend code changes in `src/` for this repository; applies to any agent.

## Decision
Update operations will first retrieve the existing record and update only the fields provided in the request.

## Rationale
This prevents omitted fields from overwriting existing database values and ensures updates are applied only to existing records.

## Alternatives rejected
Replacing the whole record with the request body was rejected because any field omitted from the request would overwrite the stored value with null or a default.

## How to apply
New update operations must preserve existing values for fields that are not included in the request and return an appropriate not-found response when the record does not exist.
