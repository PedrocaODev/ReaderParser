# Infrastructure wiring boundary

Read [Infrastructure wiring boundary](../../../../../../../../docs/concepts/runtime-wiring.md) before changes in this area.

- Keep DI wiring in core/di and stateless JVM helpers in core/util.
- Preserve subsystem ownership and qualifiers. Avoid UI/business policy in infrastructure.
- Ask before replacing Hilt or the Ktor engine or adding dependencies.
