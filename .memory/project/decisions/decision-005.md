# Decision 005 - Structured Logging

## Date
2026-08-25

## Review by
2026-11-23

## Status
Active

## Decision
Backend application logging will use SLF4J through Spring Boot's logging framework instead of `System.out.println`.

## Rationale
Structured logging provides appropriate log levels and is more suitable for production application monitoring and troubleshooting. It also makes backend logs easier to manage and diagnose.

## Alternatives rejected
Continuing to use `System.out.println` was rejected because it does not provide appropriate logging levels or production-friendly logging capabilities.

## The decision I want to record is
New and updated backend code must use appropriate SLF4J log levels such as `info`, `warn`, and `error` instead of `System.out.println`.
