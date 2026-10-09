# Room boundary

Read [Room boundary](../../../../../../../../../../docs/concepts/persistence.md) before changes in this area.

- Increment version exactly once per schema change and register an explicit SQL Migration.
- Never use destructive migration. Build/export and commit app/schemas without hand editing.
- Keep composite identity. Ask before identity/PK/FK changes.
- Migration tests belong in androidTest and use exported schema assets.
