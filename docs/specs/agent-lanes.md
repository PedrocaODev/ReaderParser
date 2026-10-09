---
id: specs-agent-lanes
title: "Agent lane requirements and historical automation"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/specification-evolution.md
---

# Agent lane requirements and historical automation

Root and local agent instructions define current routing. This node preserves the durable lane contract and older automation conditions.
Historical OpenCode permission values describe the old configuration contract, not a request to change present tool permissions.

## Lane ownership

Implementation writers delegate git writes to `integrator` and verification to `runner`.
`integrator` owns staging, commit, branch, push, PR creation, CI watching, and authorized merge-on-green.
`runner` owns verification. `reviewer` owns read-only diff review.
Agent instructions must list `integrator` with those responsibilities and routing keywords.
When preparing delivery, group commits by prefix `feat`, `fix`, `refactor`, `ci`, `cd`, or `docs` and follow repository commit conventions.
After grouped commits on a feature branch, push and create a descriptive PR when authorized.
On CI failure, report the failure and pause merge. Preserve grouped commits using a merge commit when merge is authorized.
The old unconditional merge-on-green and explicit confirmation clauses coexist historically. Neither grants present merge authorization.

## Historical verification correction cycle

The retired OpenSpec apply flow ran `runner` after task completion, then `reviewer` after runner passed.
Runner executed `:app:assembleDebug`, `:app:lintDebug`, and `:app:testDebugUnitTest` through Gradle.
A failed verification dispatched a writer fix followed by runner again.
Reviewer checked policy, architecture, and tests. BLOCKER or SHOULD-FIX findings dispatched a writer and repeated verification/review.
Both green runner and ready reviewer produced `Implementation Complete — Ready to archive`.
After three unsuccessful correction cycles, pause and present remaining issues for user guidance.
This old archive-specific completion phrase and iteration rule are historical, not new requirements for every present task.

## Historical archive delivery

The retired archive flow synced delta specs and archived the change before dispatching integrator for delivery to `origin/main`.
Create `change/<change-name>` from current HEAD, group commits, push, create a PR, and watch checks to completion.
After all checks passed, the flow described merge commits and success reporting.
A separate scenario required surfacing the PR URL and asking for user confirmation before merging.
Current delivery must use actual user authorization and current lane instructions.

## Historical OpenCode permission contract

The old `.opencode/opencode.json` contract required global and per-agent `permission.bash` enforcement.
Global `git add *` and `git commit *` defaults were `ask`, with explicit overrides for git-writing agents.

| Lane | Allowed operations | Denied operations |
| --- | --- | --- |
| `build` | Implementation within its lane | Git writes (`git *` write scope), `gh *`, assembleDebug, lintDebug, testDebugUnitTest |
| `runner` | assembleDebug, lintDebug, testDebugUnitTest, ktlintCheck, ktlintFormat, detekt; git status/diff/log/show | Operations outside its verification lane |
| `reviewer` | git status/diff/log/show | `./gradlew *` |
| `integrator` | git add/commit/push/branch/checkout/switch; gh pr create/merge/checks; gh run view | Operations outside its integration lane |

All Gradle task permissions above refer to `./gradlew :app:<task>`.
Non-integrator add/commit required confirmation (`ask`) or denial according to the agent override.
Integrator add/commit was allowed without confirmation (`allow`) in that historical permission configuration.
These facts preserve the former enforceable boundary at both levels. They do not modify current sandbox approval rules.
