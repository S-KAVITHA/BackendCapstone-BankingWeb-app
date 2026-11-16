# Decision 002 - Java 8 Compatibility

## Date
2026-08-25

## Review by
2026-11-23

## Status
Active

## Scope
Backend code changes in `src/` for this repository; applies to any agent.

## Decision
Backend code will remain compatible with Java 8.

## Rationale
The project's Maven configuration currently targets Java 8. Maintaining this compatibility prevents new backend changes from requiring an unplanned Java version upgrade.

## Alternatives rejected
Using newer Java language features was rejected because they may not compile with the project's current Java 8 configuration.

## How to apply
New backend code must use Java 8-compatible language features.
