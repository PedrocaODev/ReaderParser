# ReaderParser project index

ReaderParser is a personal Android novel/manhwa reader. Project knowledge lives in [docs/index.md](docs/index.md).
Start with the referenced file/test and nearest local AGENTS before broader retrieval.

- For layers/contracts/invariants, read [architecture](docs/architecture.md).
- For models/APIs, read [domain](docs/concepts/domain-contracts.md).
- For repositories/storage, read [persistence](docs/concepts/persistence.md).
- For Compose/navigation/theme, read [presentation](docs/concepts/presentation.md).
- For plugins, read [sources](docs/concepts/source-plugins.md).
- For DI/workers, read [runtime](docs/concepts/runtime-wiring.md).
- For tests/scripts/git delivery, read [development](docs/runbooks/development.md).
- For requirements, use docs/specs. For history/gaps, use docs/evidence. For terms, use docs/CONTEXT.md.

| Code boundary | Scoped instructions |
| --- | --- |
| Domain | [domain/AGENTS.md](app/src/main/java/com/opus/readerparser/domain/AGENTS.md) |
| Data orchestration | [data/AGENTS.md](app/src/main/java/com/opus/readerparser/data/AGENTS.md) |
| UI | [ui/AGENTS.md](app/src/main/java/com/opus/readerparser/ui/AGENTS.md) |
| Site plugins | [sources/AGENTS.md](app/src/main/java/com/opus/readerparser/sources/AGENTS.md) |
| Infrastructure | [core/AGENTS.md](app/src/main/java/com/opus/readerparser/core/AGENTS.md) |
| Workers | [workers/AGENTS.md](app/src/main/java/com/opus/readerparser/workers/AGENTS.md) |
| Source contract | [data/source/AGENTS.md](app/src/main/java/com/opus/readerparser/data/source/AGENTS.md) |
| Room | [database/AGENTS.md](app/src/main/java/com/opus/readerparser/data/local/database/AGENTS.md) |

Use `akashic query docs --seed architecture-readerparser --mode current` for architecture context.
Use canonical seeds from docs/index.md. Installed 0.2.0 does not filter current guidance by status alone.
If graphify-out/graph.json exists, query it before broad raw navigation. Details are in development.md.

Use grill-with-docs → to-spec → to-tickets → implement as material work requires.
Project glossary/specs/durable knowledge belong in docs. Tickets belong in GitHub, overriding skill storage defaults.
Trivial and read-only work proceeds directly. Keep unrelated code untouched.

Delegate writes to the bounded writer lane, checks to runner, read-only diff review to reviewer, and git/PR/CI delivery to integrator.
Source-author, screen-author, room-migration, domain-author, and journey-runner own their project specialties.
Natural-language commit/push/PR requests route to integrator. Once a PR exists, use babysit-pr for its authorized lifecycle.
Only runner VERDICT: PASSED satisfies verification. Preserve existing session authorization and current tool approval rules.

Ask before changing Source, identity/PK/FK behavior, adding a top-level layer/module, adding manifest permissions or third-party dependencies,
or replacing Hilt or Ktor's engine.
Keep domain platform-free, ViewModels source-free, one Reader/two ChapterContent variants, stable `(sourceId,url)` identity,
private downloads, and no production runBlocking. Full contracts are in [docs/architecture.md](docs/architecture.md).
Scope excludes cloud/accounts/sync, redistribution/hosting, multi-user behavior, browser shells, and non-Android platforms unless requested.
