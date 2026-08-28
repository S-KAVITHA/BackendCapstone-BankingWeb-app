# Decision 001 - Test Location

## Date
2026-08-25

## Review by
2026-11-26

## Status
Active

## Decision
Tests are maintained under `src/test/java`, following the project's existing Maven package structure.

## Rationale
This follows the Maven project structure and allows the existing build configuration to discover and run tests without additional configuration. It keeps the current test organization consistent.

## Alternatives rejected
Changing the existing test organization was rejected because it would introduce unnecessary project changes.

## The decision I want to record is
Tests will remain under `src/test/java` using the existing package structure.
