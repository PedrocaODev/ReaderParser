# Worker boundary

Read [Worker boundary](../../../../../../../../docs/concepts/runtime-wiring.md) before changes in this area.

- Use the repository graph and injected storage through WorkManager/Hilt. Keep UI dependencies out of workers.
- Preserve queue/progress/cancellation/retry semantics and inspect platform-sensitive tests.
- Existing direct image HTTP is a documented architecture discrepancy, not authorization to extend that exception.
