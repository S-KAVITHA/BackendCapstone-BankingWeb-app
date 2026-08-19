# # Parallel Agent Session Tasks

This document describes the parallel agent workflow for the target Codebase. Each agent runs in its own Git worktree, feature branch, and container mounted only to its assigned directory. This isolation prevents agents from overwriting each other's changes, reduces merge conflicts, and keeps ownership of changes clear during review.

## Pre-Run Baseline

Before creating either worktree, the repository was checked out to `main` and verified clean. The completed `session_tasks.md` scope contract was committed to `main` before the agent worktrees were created.

Verification:

```bash
git checkout main
git log --oneline -1
git worktree list
```

![img.png](img.png)

## Session A

**Branch name:** feature/agent-a  
**Worktree directory:** target-agent-a

**Task:**  
Create REST API controller test classes for `AdminController` and `CustomerController` using mocking. The tests should verify controller behavior, HTTP responses, request handling, and service interactions.

**Files the agent may write to:**
- `src/test/java/com/example/demo/AdminControllerTest.java`
- `src/test/java/com/example/demo/CustomerControllerTest.java`

**Files the agent may read but not write to:**
- `src/main/java/com/example/demo/AdminController.java`
- `src/main/java/com/example/demo/CustomerController.java`
- Related service classes
- `pom.xml`
- `README.md`
- Existing test files

**Write restriction:**
The agent may not modify any other source, test, configuration, documentation, or resource file.

**Commands the agent may run:**
- `mvn test`
- `mvn clean test`
- `git status`
- `git diff`

**Definition of done:**
- Test classes are created for `AdminController` and `CustomerController`.
- Tests use mocking frameworks (for example, Mockito with Spring Boot Test).
- REST API endpoints are tested with mocked service dependencies.
- Tests verify expected HTTP status codes and response data.
- All new tests pass successfully using `mvn test`.
- Changes are committed to the assigned branch with a meaningful commit message.

**From the root of your main Target Codebase, create worktree for session A.:**

Confirm the Repository is on the main branch and ensures that both worktrees branch off the same clean baseline, 
which makes the final merge straightforward.

```bash
git checkout main
git pull
git worktree add ../target-agent-a -b feature/agent-a
```
**Verify the worktrees :**

```bash
git worktree list
```

![img_5.png](img_5.png)

verify worktree is on the correct branch:

```bash
cd ../target-agent-a
ls
git branch
```

![img_16.png](img_16.png)

Launch the container in the terminal 1:

docker run -it --rm \
-v "${PWD}:/workspace" \
-v "claude-auth:/claude-auth" \
backend-banking-capstone-app

![img_15.png](img_15.png)

and use Claude inside container to execute the task

![img_3.png](img_3.png)

Actual Result:

![img_30.png](img_30.png)

![img_31.png](img_31.png)

![img_20.png](img_20.png)

After both agents finish, use git status and git diff main commands in order to inspect each worktree separately against main.

![img_21.png](img_21.png)
![img_22.png](img_22.png)


## Session B

**Branch name:** feature/agent-b  
**Worktree directory:** target-agent-b

**Task:**  
Update README.md with documentation describing the parallel multi-agent workflow, including worktree setup, agent responsibilities, container execution, and merge workflow.

**Files or folders the agent may write to:**
- `README.md`

**Files or folders the agent may read but not write to:**
- `src/main/java/**`
- `src/test/java/**`
- `pom.xml`
- Existing application source files

**Commands the agent may run:**
- `git status`
- `git diff`
- `git log`
- `git branch`
- `mvn test` (optional verification only)

**Definition of done:**
- README.md contains clear documentation of the parallel agent workflow.
- Documentation explains worktree creation, container execution, and merge process.
- Changes are committed with a meaningful commit message.

**From the root of your main target Codebase, create worktree for session B.:**

Confirm the Repository is on the main branch and ensures that both worktrees branch off the same clean baseline,
which makes the final merge straightforward.

```bash
git checkout main
git pull
git worktree add ../target-agent-b -b feature/agent-b
```

**Verify the worktrees :**

```bash
git worktree list
```

![img_5.png](img_5.png)

verify worktree is on the correct branch:

```bash
cd ../target-agent-b
ls
git branch
```
![img_6.png](img_6.png)

Launch the container in the terminal 2:

docker run -it --rm \
-v "${PWD}:/workspace" \
-v "claude-auth:/claude-auth" \
backend-banking-capstone-app

and use Claude inside container to execute the task

![img_7.png](img_7.png)

![img_17.png](img_17.png)

Actual Result:

![img_33.png](img_33.png)

![img_23.png](img_23.png)

After both agents finish, use git status and git diff main commands in order to inspect each worktree separately against main.

![img_24.png](img_24.png)

## Why These Tasks Can Run in Parallel

The two sessions were intentionally assigned non-overlapping write scopes.

- **Session A** writes only to:
    - `src/test/java/com/example/demo/AdminControllerTest.java`
    - `src/test/java/com/example/demo/CustomerControllerTest.java`
- **Session B** writes only to:
    - `README.md`

Both sessions are allowed to read the production source code, `pom.xml`, and other existing application files, but neither session is permitted to modify them. Keeping these shared resources read-only ensures both agents work from the same codebase without introducing conflicting changes.

Because each session writes to a different part of the repository, they cannot overwrite each other's work or create file-level merge conflicts during development.

If both sessions were allowed to modify the same files (for example, `README.md`, `pom.xml`, or the controller source files), concurrent edits could result in merge conflicts, accidental overwrites, or difficulty determining which changes belong to each task. Restricting each session to separate write scopes keeps the work independent and makes review and merging straightforward.

## Shared Files Excluded From Write Scope

Both sessions were allowed to read shared files such as:

- `src/main/java/**`
- `pom.xml`
- Existing application source files

These files were excluded from write access because parallel changes to production code or shared configuration could create conflicts or unintended changes. Keeping them read-only allowed both agents to work independently and made merging safer.

## Evaluation and Merge Verification

Each agent branch was reviewed independently against `main` before merging.

### Session A

```bash
cd ../target-agent-a
git status
git diff main
```
![img_13.png](img_13.png)

**Correctness and usefulness:**  
The agent added `AdminControllerTest.java` and `CustomerControllerTest.java`. The tests cover the assigned controller behavior, mocked service interactions, HTTP responses, and response data. `mvn test` completed successfully.

**Scope adherence:**  
`git diff main` showed only:
- `src/test/java/com/example/demo/AdminControllerTest.java`
- `src/test/java/com/example/demo/CustomerControllerTest.java`

No production source, README, configuration, or unrelated test files were modified.

**Production appropriateness:**  
The changes add automated controller test coverage without modifying application behavior or production source code. The tests passed successfully, so the changes were considered appropriate to merge.

**Definition of done:**  
All assigned test classes were created, mocking was used, the required endpoint behavior was tested, tests passed, and the branch was committed.

**Merge decision:** Approved.

**Reason:** The diff matched the assigned file-level scope and the successful test execution demonstrated that the required controller test coverage was functional.


### Session B

```bash
cd ../target-agent-b
git status
git diff main
```

![img_14.png](img_14.png)

**Correctness and usefulness:**  
The agent updated `README.md` with documentation covering the parallel workflow, worktree setup, container execution, agent responsibilities, and merge process.

**Scope adherence:**  
`git diff main ` showed only: `README.md`

No source, test, configuration, or other files were modified.

**Production appropriateness:**  
The change is documentation-only and does not alter application behavior. The documentation is appropriate for explaining the demonstrated workflow.

**Definition of done:**  
The README contains the required workflow documentation and the branch was committed with a meaningful commit message.

**Merge decision:** Approved.

**Reason:** The diff contained only the assigned README change and satisfied the Session B definition of done.

### Merge

```bash
cd ../BackendCapstone-BankingWeb-app
git merge feature/agent-a
git merge feature/agent-b
git log --oneline -5
git status
```
The merge was performed only after reviewing each branch with `git diff main`. The merge commands were executed for both feature branches, followed by git log --oneline -5 and git status to verify the resulting repository state.

![img_4.png](img_4.png)

![img.png](img.png)

![img_8.png](img_8.png)

## Final Cleanup

```bash
git worktree remove ../target-agent-a
git worktree remove ../target-agent-b

git worktree list

git status
```

Expected result:

- Only the main worktree remains.
- `git status` reports a clean working tree.

Screenshot:

![img_2.png](img_2.png)

<img width="596" height="80" alt="image" src="https://github.com/user-attachments/assets/ca0cc381-43e3-4ec0-a77c-6abb9f5c016f" />

## Reflection

### Q1. What did you learn about defining parallel agent tasks?

This run showed that parallel tasks need both separate responsibilities and explicit file-level write boundaries. Session A focused on controller tests while Session B focused on README documentation, so the two tasks could be executed independently. Defining the expected output files also made post-run scope verification easier.

### Q2. How did the task definitions affect agent behavior?

Session A was directed to create only the two controller test classes, while Session B was directed to modify only README.md. This gave each agent a clear target and prevented the tasks from competing for the same files.

### Q3. What happened with merge conflicts, and why?

The branches merged without a file-level conflict because the write scopes were separated before the agents started. Session A modified the assigned test files and Session B modified README.md. The clean merge was therefore a result of deliberate task decomposition rather than relying on the agents to avoid each other's files.

### Q4. What judgment call required human oversight?

The worktree and container setup could verify branch and filesystem isolation, but it could not determine whether the generated tests provided meaningful coverage or whether the README documentation was sufficiently useful and production-appropriate. Those aspects required human review of the actual diffs and test results.

### Q5. How did the scope definition affect the merge outcome?

The non-overlapping scopes made the branches independently reviewable and reduced the possibility of conflicting edits. The Session A test files and Session B README change could be merged without competing modifications to the same file.

### Q6. What would you change in the next run?

I would keep the two-task decomposition but make the agent instructions even more explicit by naming every expected output file and requiring each agent to run `git diff main` before committing. Better file-level scoping would catch unintended changes earlier, while a corrective prompt would still be useful if an agent began drifting from its assigned task during execution.