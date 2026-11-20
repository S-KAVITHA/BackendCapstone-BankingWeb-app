## .eval-artifacts/runs/holdout-clean/HO-02.json
[PASS] write_classifications (schema validation): All storage writes carried a valid classification.
[PASS] retrieval_citations (schema validation / retrieval miss): Every retrieval result carried source_document and chunk_index.
[PASS] output_schema (schema validation): all subagent output documents matched the result schema.
[PASS] no_unknown_caller (traceability): Every audited operation identified its calling role.
[PASS] audit_matches_writes (traceability): Audit log entries match the writes in the transcript.
[PASS] read_matches_write (traceability): 1 verification read(s) targeted entry_ids written earlier in this run.
[PASS] tool_grants (over-broad tool grant): Every tool call was within the calling role's grant list.
[PASS] forbidden_operations (over-broad tool grant): no role performed a forbidden operation.
[PASS] required_roles (routing misfire): All required subagents ran.
[PASS] role_order (routing misfire): Subagents ran in the expected order.
[PASS] context_bleed (context bleed): no canary planted; bleed check not exercised this run.
[PASS] reviewer_conflict (conflicting reviewers): fewer than two reviewers ran; no conflict possible.
[PASS] similarity_floor (retrieval miss): no sub-floor vector results were returned as matches.
[PASS] latency (latency): run took 38.0s, within budget.
[PASS] cost (cost): run used 0 tokens, within budget.

15/15 deterministic checks passed.

Running rubric suite (each dimension is one model call)...

[FAIL] rubric:correctness: scored 1/4 (needs 3). No code was changed or tested; the implementer only wrote a plan entry with placeholders, and the reviewer and tester approved work that does not exist.
[PASS] rubric:task_adherence: scored 3/4 (needs 3). Stayed on the ISO 8601 date-parsing task without unrequested changes, but retrieved irrelevant rate-limit documents and documented an approach instead of changing any code.
[FAIL] rubric:groundedness: scored 2/4 (needs 3). Reviewer and tester claim verified, approved code with no code, tests, or citations behind it; retrieved sources were irrelevant rate-limit documents and were never cited.
[FAIL] rubric:clarity: scored 2/4 (needs 3). Summaries read well but stay vague: no files or code, a placeholder for existing logic, an unanswered project_id question, and repeated prose instead of actionable steps.
[FAIL] rubric:aggregate: total 8 (needs 12).

1/5 rubric checks passed.
## exit: 1
## .eval-artifacts/runs/regression/HO-02-after-fix.json
[PASS] write_classifications (schema validation): All storage writes carried a valid classification.
[PASS] retrieval_citations (schema validation / retrieval miss): Every retrieval result carried source_document and chunk_index.
[PASS] output_schema (schema validation): all subagent output documents matched the result schema.
[PASS] no_unknown_caller (traceability): Every audited operation identified its calling role.
[PASS] audit_matches_writes (traceability): Audit log entries match the writes in the transcript.
[PASS] read_matches_write (traceability): no verification read_entry in this run; check not exercised.
[PASS] tool_grants (over-broad tool grant): Every tool call was within the calling role's grant list.
[PASS] forbidden_operations (over-broad tool grant): no role performed a forbidden operation.
[PASS] required_roles (routing misfire): All required subagents ran.
[PASS] role_order (routing misfire): Subagents ran in the expected order.
[PASS] context_bleed (context bleed): no canary planted; bleed check not exercised this run.
[PASS] reviewer_conflict (conflicting reviewers): fewer than two reviewers ran; no conflict possible.
[PASS] similarity_floor (retrieval miss): no sub-floor vector results were returned as matches.
[PASS] latency (latency): run took 21.2s, within budget.
[PASS] cost (cost): run used 0 tokens, within budget.

15/15 deterministic checks passed.

Running rubric suite (each dimension is one model call)...

[FAIL] rubric:correctness: scored 1/4 (needs 3). No implementation was produced: every agent stopped over a missing project_id, so ISO 8601 support was never written, reviewed or tested.
[PASS] rubric:task_adherence: scored 3/4 (needs 3). Stayed on the date-parsing task without unrequested changes, but delivered no implementation, and the planner's rate-limit retrievals were irrelevant drift.
[FAIL] rubric:groundedness: scored 2/4 (needs 3). No claims cite the retrieved rate-limit sources, which were off-topic. The blocking 'project_id is essential' claim is an unsupported assumption that later agents repeated.
[FAIL] rubric:clarity: scored 2/4 (needs 3). Each blocked status says project_id is missing, but the implementer's output is cut off mid-sentence, and the run delivers no plan or code to act on.
[FAIL] rubric:aggregate: total 8 (needs 12).

1/5 rubric checks passed.
## exit: 1
