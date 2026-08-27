# Decision 003: Partial Updates

Date: 2026-08-25
Status: Active

## Decision

Update operations will first retrieve the existing record and update only
the fields provided in the request.

## Why

This prevents omitted fields from overwriting existing database values
and ensures updates are applied only to existing records.

## How to apply

New update operations must preserve existing values for fields that are
not included in the request and return an appropriate not-found response
when the record does not exist.
