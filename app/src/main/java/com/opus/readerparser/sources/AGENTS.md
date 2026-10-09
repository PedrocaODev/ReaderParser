# Site plugin rules

Read [Site plugin rules](../../../../../../../../docs/concepts/source-plugins.md) before changes in this area.

- Place each site in a lowercase directory with <SiteName>.kt, extending HtmlSource.
- Override only the novel text parser or manhwa page parser, then register in core/di/SourceModule.kt.
- Use selectFirst/null-safety, absUrl, and trimmed text. Obtain real selectors and fixture evidence.
- Add MockEngine JVM tests and saved fixtures in app/src/test/resources/fixtures/<sitename>.
