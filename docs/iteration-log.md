# Rubric: Single-Agent Build Verification Workflow

## Dimension 1: Build Execution

**Definition:**  
Measures whether the agent correctly runs the required Maven build command and captures the result.

### Score 1: Does not meet
Example: Agent does not run `mvn clean test` or provides no build result.

### Score 2: Partially meets
Example: Agent runs a Maven command but uses the wrong command or provides incomplete output.

### Score 3: Meets
Example: Agent runs `mvn clean test` and reports whether the build succeeds or fails.

### Score 4: Exceeds
Example: Agent runs the correct command, captures relevant output, and clearly summarizes the build status.

---

## Dimension 2: Error Reporting

**Definition:**  
Measures whether the agent accurately identifies and explains build failures.

### Score 1: Does not meet
Example: Agent reports failure without identifying the cause.

### Score 2: Partially meets
Example: Agent mentions an error but does not connect it to the Maven output.

### Score 3: Meets
Example: Agent identifies the failing step and summarizes the error from the build output.

### Score 4: Exceeds
Example: Agent identifies the root cause and provides a clear explanation based on the build logs.

---

## Dimension 3: Workflow Compliance

**Definition:**  
Measures whether the agent follows the required workflow without modifying project files.

### Score 1: Does not meet
Example: Agent modifies source files or skips required workflow steps.

### Score 2: Partially meets
Example: Agent follows some steps but makes unnecessary changes.

### Score 3: Meets
Example: Agent runs the verification workflow and does not modify source code.

### Score 4: Exceeds
Example: Agent completes the workflow, confirms no files were changed, and provides a clear verification summary.

---

## Dimension 4: Result Communication

**Definition:**  
Measures how clearly the agent communicates the final verification result.

### Score 1: Does not meet
Example: Output is missing or unclear.

### Score 2: Partially meets
Example: Output contains a result but lacks important details.

### Score 3: Meets
Example: Output clearly states whether the build passed or failed.

### Score 4: Exceeds
Example: Output includes build status, important logs, and a concise summary useful for review.

---

## Pass Threshold

The workflow passes if:
- The average score is at least **3.0**, and
- No individual rubric dimension scores below **3**.
