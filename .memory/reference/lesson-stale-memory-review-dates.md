---
classification: internal
project: proj-lessons
doc_type: lesson
---

# Lesson: Every project-memory entry needs a review date, and an undated entry is not trusted

## What happened
In Run 014 the stale-memory policy was tested with decision-001 set to an expired review date; after the policy was strengthened, the agent refused to act on it until a human confirmed it. In Run 027 a fresh session flagged decision-003 because it had no review date at all, and in Run 028 it deferred the partial-update work until that decision was confirmed. Run 030 found the same gap in `slack-routing.md`.

## What we learned
A review date is what lets the agent notice that memory may be out of date. Without one, the stale-memory check can never trigger, and a wrong decision could be applied indefinitely.

## How to apply it
Give every entry in `.memory/project/` a `Review by` date and a `Scope` section, and list the date in `MEMORY_INDEX.md`. Treat an entry with no review date like an expired one: say so and ask a human before relying on it. When a review date passes, only a human may confirm the entry and move the date; the agent must not extend it.
