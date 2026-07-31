# Evaluation Rubric

## 1. Task Completion

**Definition:** Measures whether the agent completed the assigned validation task according to the PRD.

### Score 1 - Does not meet
The agent did not complete the requested task.

Example: The agent failed to run the required Maven test command.

### Score 2 - Partially meets
The agent started the task but did not provide complete results.

Example: Tests were started but failures were not reported.

### Score 3 - Meets
The agent completed the task and provided the expected output.

Example: Maven tests were executed and results were documented.

### Score 4 - Exceeds
The agent completed the task and provided additional useful analysis.

Example: Tests were executed and failure causes were summarized.

---

## 2. Result Accuracy

**Definition:** Measures whether the reported results match the actual command output.
The agent must include Maven test evidence such as build exit status, total tests executed, passed tests, failed tests, and skipped tests when applicable.

### Score 1 - Does not meet
The reported result does not match the actual output.

Example: Reports successful tests when Maven failed.

### Score 2 - Partially meets
Some results are correct but important details are missing.

Example: Reports failure without identifying failed tests.

### Score 3 - Meets
The documented result accurately reflects the command output.

Example: Build status matches the Maven output.

### Score 4 - Exceeds
The result is accurate and includes useful explanation.

Example: Explains why a failure occurred and suggests next steps.

---

## 3. Scope Compliance

**Definition:** Measures whether the agent only performs the actions allowed by the task requirements.

### Score 1 - Does not meet
The agent modifies unrelated files or performs unauthorized actions.

Example: Changes application source code during a test-only task.

### Score 2 - Partially meets
The agent mostly follows the scope but makes unnecessary changes.

Example: Creates extra files unrelated to validation.

### Score 3 - Meets
The agent stays within the assigned task scope.

Example: Runs tests without modifying unrelated project files.

### Score 4 - Exceeds
The agent follows scope and provides additional verification.

Example: Runs tests, records evidence, and verifies no unexpected file changes using project status checks.

---

## Pass Threshold

A run passes when:
- All three rubric dimensions score **3 or higher**.
- The required validation task is completed.
- Results are documented with evidence.
