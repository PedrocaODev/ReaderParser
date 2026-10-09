---
id: concept-agent-lanes
title: "Agent lanes and independent gates"
type: Concept
status: active
valid_from: 2026-10-08
scope:
  system: readerparser
relations:
  - type: supported_by
    target: ../evidence/infrastructure-history.md
  - type: relates_to
    target: ../specs/agent-lanes.md
  - type: relates_to
    target: ./repository-workflow.md
  - type: relates_to
    target: ../adr/0105-enforced-agent-separation.md
---

# Agent lanes and independent gates

Use the [agent-lane specification](../specs/agent-lanes.md) and [repository workflow](./repository-workflow.md) for current requirements.
This concept explains why lane boundaries exist and identifies limits in the historical enforcement model.
OpenSpec apply/archive command wiring below is historical context. The approved vault workflow replaces it.

## Separate execution, verification, review, and integration

The June 9 records described seven specialists: `source-author`, `screen-author`, `room-migration`, `domain-author`, `runner`, `reviewer`, and `journey-runner`.
They also described a global orchestrator and a `build` subagent.
Before hardening, `build` could implement, execute Gradle checks, and stage or commit changes in one turn.
Review and verification were inconsistently dispatched, and the git/PR/CI lifecycle was an ad hoc afterthought.

The selected model adds a dedicated `integrator` for staging, commit, branch, push, PR creation, CI observation, and authorized merge.
The runner owns build, lint, tests, and verification reporting. The reviewer owns read-only diff review.
The implementer changes files. The orchestrator coordinates specialists rather than taking their execution responsibilities.
Extending runner with delivery work was rejected because it would blur the verification gate.
Giving delivery directly to the orchestrator was rejected because a reusable specialist keeps execution separate from coordination.

The integrator definition's metadata described class `I`, mode `subagent`, read access to git status/log/diff, and routing on git/commit/branch/push/PR/CI/merge.
Its historical frontmatter denied edit, skill, and task permissions.
The lane rules prohibited source edits, Gradle execution, force-push, and merge without user confirmation.
Existing authorization and current tool permission rules govern actions. Historical merge wording does not itself authorize delivery.

## Permission enforcement and its limits

The June design chose two layers in `.opencode/opencode.json`: global git-write defaults of `ask`, plus agent-specific Bash overrides.
Prompt-only self-policing with globally allowed writes was rejected because configuration was the intended enforcement mechanism.

| Historical lane | Intended Bash boundary |
| --- | --- |
| `build` | Deny `git *`, `gh *`, and `:app:assembleDebug`, `:app:lintDebug`, `:app:testDebugUnitTest`. |
| `runner` | Allow those verification tasks plus `:app:ktlintCheck`, `:app:ktlintFormat`, `:app:detekt`, and git status/diff/log/show. |
| `reviewer` | Allow git status/diff/log/show and deny `./gradlew *`. |
| `integrator` | Allow git add/commit/push/branch/checkout/switch and gh pr create/merge/checks, gh run view. |

The config also added `integrator: allow` to task dispatch and changed global `git add *` and `git commit *` from allow to ask.
The design described `git *` as a write denial, although that pattern also matches read-only git commands.
The runner formatter allowance (`ktlintFormat`) can mutate files. It is historical permission metadata, not a statement that a read-only verification gate may edit.
These are retained limitations of the original permission description. The migration does not change OpenCode permission configuration.

## Independent completion gates

Historically, `/opsx-apply` previously stopped after tasks were checked. The replacement flow dispatched runner and reviewer before declaring completion.
Its suite was `assembleDebug`, `lintDebug`, and `testDebugUnitTest`.
Failures or review BLOCKER/SHOULD-FIX findings required correction, re-verification, and re-review.
Both gates had to pass before “Implementation Complete — Ready to archive”.
The design bounded the correction loop at three iterations, then required pausing with remaining issues rather than looping forever.
Task wording separately said to pause for a fix after each failed gate. This records procedural intent, not proof of enforced runtime behavior.
Commands `.opencode/commands/opsx-apply.md` and skill `openspec-apply-change/SKILL.md` were to mirror steps 7–9.
Durable gate rules were placed in `agent-lane-hardening` and routing in `repository-governance`, rather than only command text.

## Delivery lifecycle and historical merge choice

Historically, `/opsx-archive` previously moved a directory and synchronized delta specs, without git delivery.
The added phase dispatched integrator after archive and spec sync, then created `change/<change-name>`, grouped commits by prefix, pushed, created a PR, observed CI, and merged after authorization.
The allowed prefixes were `feat`, `fix`, `refactor`, `ci`, `cd`, and `docs`.
PR output was to include URL, CI status, and merge result.
Commands `.opencode/commands/opsx-archive.md` and `openspec-archive-change/SKILL.md` were to mirror the integration steps.

The June design preferred merge commits over squash to preserve separate commit-prefix groupings.
It considered manual user merging and chose integrator completion after user confirmation instead.
The records do not establish that this merge strategy remains required or accepted today.
Current delivery uses explicit authorization and current repository policy. No commit, push, PR, or merge is part of this documentation migration.

## Costs and scope

A dedicated lane adds token cost. The design proposed a small definition and normally one final dispatch per change.
Permission tightening could interrupt workflows, but the design said existing practice already delegated most git operations.
The change excluded application code, Source interfaces, identity changes, framework replacement, dependencies, OpenSpec schema changes, and release/CD automation.
Its subject was verification and CI delivery, rather than release publishing.
The [historical completion claims](../evidence/infrastructure-history.md#agent-lane-completion-claims) do not prove configuration enforcement or acceptance.
