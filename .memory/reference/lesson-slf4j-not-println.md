---
classification: internal
project: proj-lessons
doc_type: lesson
---

# Lesson: New code logs with SLF4J, even though the controllers still use println

## What happened
`CustomerController` and `AdminController` contain 16 `System.out.println` calls, eight in each. A fresh agent session that learns conventions only from the code would copy that pattern. Project decision-005 records that new and updated backend code must use SLF4J instead, and in Run 028 the agent followed the decision rather than the code.

## What we learned
When the code and a recorded decision disagree, the existing code shows history and the decision shows direction. Copying the dominant pattern in a file is not a safe default. Output from `println` has no level, cannot be filtered in production, and is easy to lose.

## How to apply it
In any class you create or change, declare `private static final Logger log = LoggerFactory.getLogger(<Class>.class);` and use `log.info`, `log.warn` or `log.error` with placeholders instead of string concatenation. Do not rewrite untouched classes just to remove println. Convert them when you are already changing that class for another reason, and keep the change Java 8 compatible (decision-002).
