# Project Memory Index

Last updated: 2026-11-16

## Active entries

- [decisions/decision-001.md](decisions/decision-001.md): tests remain under `src/test/java` using the existing Maven package structure. Review by 2026-11-26.
- [decisions/decision-002.md](decisions/decision-002.md): backend code remains compatible with Java 8. Review by 2026-11-23.
- [decisions/decision-003.md](decisions/decision-003.md): update operations retrieve the existing record and change only the fields provided; a missing record returns not-found. Review by 2026-11-23.
- [decisions/decision-004.md](decisions/decision-004.md): REST API exceptions are handled centrally with `@ControllerAdvice`. Review by 2026-11-23.
- [decisions/decision-005.md](decisions/decision-005.md): new and updated backend code uses SLF4J logging instead of `System.out.println`. Review by 2026-11-23.
- [slack-routing.md](slack-routing.md): the email-summarize workflow posts summaries to #zapier-test. Scoped to the email-summarize agent only.

## Scope

Decisions 001–005 apply to backend code changes in `src/` for this repository, by any agent. `slack-routing.md` applies only to the scope it declares.

## Archived entries

None.

## Pruning schedule

- Workflow-scoped entries: archived when the branch merges to main and the information is no longer needed.
- Project-scoped entries: reviewed when the decision or project priority changes, or when the review date passes.
