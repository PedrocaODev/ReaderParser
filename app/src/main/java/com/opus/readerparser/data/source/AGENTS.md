# Source boundary

Read [Source boundary](../../../../../../../../../docs/concepts/source-plugins.md) before changes in this area.

- Ask before changing Source. Keep site-specific logic out of HtmlSource.
- Registry is a compile-time Hilt Map<Long,Source>. Derive IDs with computeSourceId(name,lang,type).
- Sources throw operational errors without logging or null sentinels. Preserve explicit existing recovery contracts.
