## command: python3 eval/test_deterministic.py .eval-artifacts/runs/dev/RUN-20260921-014752.json
## date: 2026-11-20T04:47:54Z
[PASS] write_classifications (schema validation): All storage writes carried a valid classification.
[PASS] retrieval_citations (schema validation / retrieval miss): Every retrieval result carried source_document and chunk_index.
[PASS] output_schema (schema validation): all subagent output documents matched the result schema.
[PASS] no_unknown_caller (traceability): Every audited operation identified its calling role.
[PASS] audit_matches_writes (traceability): Audit log entries match the writes in the transcript.
[FAIL] read_matches_write (traceability): verification read(s) not tied to a preceding write: tester read 'api-validation-rule-decision', which no earlier write_entry returned (written: ['cd10904f-996d-4da9-86d3-2d6584401ae3']).
[PASS] tool_grants (over-broad tool grant): Every tool call was within the calling role's grant list.
[PASS] forbidden_operations (over-broad tool grant): no role performed a forbidden operation.
[PASS] required_roles (routing misfire): All required subagents ran.
[PASS] role_order (routing misfire): Subagents ran in the expected order.
[PASS] context_bleed (context bleed): no canary planted; bleed check not exercised this run.
[PASS] reviewer_conflict (conflicting reviewers): fewer than two reviewers ran; no conflict possible.
[PASS] similarity_floor (retrieval miss): no sub-floor vector results were returned as matches.
[PASS] latency (latency): run took 32.3s, within budget.
[PASS] cost (cost): run used 0 tokens, within budget.

14/15 deterministic checks passed.
## exit code: 1
