# Pre-Session Plan and Context-Management Plan

## Project Task

Evaluate and improve the repository's context-boundary workflow for Claude Code sessions.

The agent will use the existing context-management skills and repository documentation to perform a real review of the current `summarize-session` and `write-handoff` workflow. It will identify weaknesses in how the workflow preserves task state, rules, artifact state, and changing requirements, then produce actionable recommendations and implement an approved improvement.

The work will use the actual repository's `.claude/skills/`, `CLAUDE.md`, Iteration Log, and existing workflow artifacts. No simulated project task or fabricated project history will be used.

## Agent

Use Claude Code with the existing context-management skills:

- `.claude/skills/summarize-session/SKILL.md`
- `.claude/skills/write-handoff/SKILL.md`

The agent will work interactively inside the Target Codebase and inspect related project documentation, iteration logs, and context-management artifacts.

## Phase One — Baseline Review

### Goal

Review the existing context-boundary workflow and determine whether it reliably preserves the information required for a fresh session to continue work.

### Work

The agent will inspect:

- `.claude/skills/summarize-session/SKILL.md`
- `.claude/skills/write-handoff/SKILL.md`
- relevant `CLAUDE.md` context-boundary policies;
- existing Iteration Log entries;
- existing handoff or summary artifacts;
- relevant project requirements and acceptance criteria.

The agent will identify whether the current workflow:

- preserves verified task state;
- preserves exact rules and constraints;
- distinguishes active rules from superseded rules;
- preserves the state of modified artifacts;
- records unresolved questions and blockers;
- avoids reconstructing corrupted or missing information;
- produces useful information for a fresh session.

### Phase One Rules and Scope

During Phase One:

- Use only verifiable repository and session information.
- Do not invent missing task history, decisions, rules, or artifact contents.
- Preserve exact rule wording when the source requires verbatim copying.
- Distinguish user-provided requirements from assistant proposals.
- Do not claim an artifact was modified unless repository evidence supports that claim.
- Do not modify unrelated project files.
- Produce a baseline assessment and actionable findings.

## Context Boundary and Requirement Change

Phase One ends after the baseline assessment is complete.

At that point, explicitly establish a context boundary before beginning Phase Two.

The boundary must state:

- Phase One is complete.
- Phase Two will focus on improving requirement-transition handling.
- Phase One decisions and verified artifact state remain relevant.
- The new Phase Two requirement is now active.
- Any superseded requirement must no longer control Phase Two work.

## Most Consequential Boundary Decision

The most consequential boundary is the Phase One-to-Phase Two transition.

This boundary is necessary because Phase Two introduces a new requirement: the workflow must explicitly preserve requirement transitions. Without this boundary, the agent could continue treating Phase One requirements as permanently active and fail to distinguish the new requirement from superseded instructions.

The boundary therefore preserves the verified Phase One state while explicitly establishing the new Phase Two requirement. This makes requirement drift observable and allows the agent's handling of the transition to be evaluated.

### Requirement That Changes

The Phase Two requirement is that the workflow must explicitly preserve and record the requirement transition between phases.

Phase Two must document:

- which Phase One requirements remain active;
- which requirement changes;
- why the requirement changes;
- which Phase One decisions remain relevant;
- which artifacts must be revisited under the new requirement.

This requirement tests whether the agent can distinguish active requirements from superseded requirements rather than treating every previous instruction as permanently active.

## Proactive Summary

Immediately before the Phase One-to-Phase Two boundary, request a structured session summary.

The summary must preserve:

- the current goal;
- active requirements and constraints;
- decisions made during Phase One;
- the current state of each artifact;
- unresolved questions or risks;
- the requirement that will change;
- the next planned action.

The summary will serve as the explicit context record for Phase Two.

### Reason for the Summary

The requirement changes at the boundary, creating a high risk of rule drift. A structured summary provides an auditable record of what was active before the change and what Phase Two must preserve.

## Phase Two — Implement and Validate

### Goal

Apply the approved context-boundary improvement and verify it against the actual repository workflow.

### Work

1. Review the Phase One findings and proactive summary.
2. Confirm the requirement change.
3. Update the appropriate context-management skill or documentation.
4. Preserve all Phase One requirements that remain active.
5. Stop applying any requirement explicitly superseded by the Phase Two change.
6. Revisit the artifact identified below.
7. Verify that the revised workflow explicitly records requirement transitions.
8. Test the resulting workflow against real repository information.
9. Review the final state for consistency with the active requirements.

### Phase Two Rules and Scope

During Phase Two:

- The new requirement takes precedence over conflicting Phase One requirements.
- Requirements that did not change remain active.
- Superseded requirements must not silently remain active.
- No fabricated historical information may be introduced.
- Changes must remain scoped to the context-management workflow.
- The agent must verify actual repository state before claiming completion.
- The final result must provide useful, actionable work for the Target Codebase.

## Artifact to Revisit

The primary artifact to revisit is:

`.claude/skills/write-handoff/SKILL.md`

The agent will determine whether this skill adequately preserves requirement changes across a context boundary and whether its handoff instructions provide enough information for a fresh session to continue correctly.

The relevant Iteration Log entry will also be reviewed to ensure that the observed behavior and resulting changes are documented accurately.

## Compaction Plan

Compaction will not be triggered deliberately.

If the context window becomes crowded enough that continuing without compaction creates a meaningful risk of losing important task information, compaction may be used.

If compaction occurs, the agent must compare the post-compaction state against:

- the proactive summary;
- the current repository files;
- the active Phase Two requirements.

The agent must correct any discrepancy before continuing.

## Final Evaluation

After Phase Two implementation, establish a second explicit boundary before evaluation.

The agent must stop making implementation changes and review:

- the original task;
- the current requirements;
- the requirement change;
- Phase One decisions;
- the modified artifacts;
- unresolved issues;
- the final repository state.

This separates implementation from evaluation and prevents the agent from treating its own recent edits as proof of correctness.

## Evidence for Evaluation

Use concrete evidence from the session and repository:

1. Phase One baseline findings.
2. The proactive summary.
3. The explicit Phase One-to-Phase Two boundary.
4. The requirement-change record.
5. Changes to `.claude/skills/write-handoff/SKILL.md`.
6. `git diff` showing the exact modifications.
7. The final contents of affected files.
8. Any generated summary or handoff artifact.
9. The final Iteration Log entry.
10. Agent responses showing whether current requirements and artifact state were preserved.

## Rubric

### Accuracy

Did the agent use the current project facts, rules, requirements, and artifact state correctly?

### Task Adherence

Did the agent track the requirement change and follow the active instructions during each phase?

### Coherence

Did the final artifacts, recommendations, and decisions reflect one consistent current state, or did parts of the result rely on outdated or conflicting context?

Use the existing project scoring scale for all three dimensions.

## Required Iteration Log Entry

After the session, record:

- Run ID and date;
- task performed;
- agent used;
- Phase One work;
- Phase Two work;
- requirement that changed;
- explicit context boundaries used;
- proactive summary used;
- whether compaction occurred and why;
- Accuracy score;
- Task Adherence score;
- Coherence score;
- evidence supporting each score;
- context drift or other misfires;
- the most significant observed limitation;
- one context-management choice to change in a future run;
- relevant commit SHAs.

## Acceptance Criteria

The session succeeds when:

- The agent performs a real task against the Target Codebase.
- The workflow contains at least two distinct phases.
- Phase One has an explicit completion boundary.
- A real requirement change occurs at the planned boundary.
- Phase Two correctly applies the changed requirement.
- Requirements that remain active are preserved.
- Superseded requirements are not incorrectly applied.
- The previously identified artifact is revisited.
- A proactive summary preserves the required context before the transition.
- The agent verifies repository state instead of inventing missing information.
- The final work produces useful, actionable information or changes for the Target Codebase.
- The session can be evaluated using concrete repository evidence.
- The results are documented in the Iteration Log.
- Relevant changes are committed to git.

## Future Improvement

After the run, identify one context-management decision that should change in a future run.

The recommendation must be based on observed agent behavior and repository evidence rather than speculation.

## Technique Choice

A proactive summary is preferred over relying only on compaction because the requirement changes at the phase boundary. The summary creates an explicit, auditable record of the active rules and artifact state before the change.

An explicit context boundary is used instead of a summary alone because the boundary clearly marks when the new requirement becomes active.
