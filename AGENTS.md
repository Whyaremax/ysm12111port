# Repository Notes

- This directory is the standalone `3DModelNow` / `modern_port` git repository.
- Keep this lane separate from the parent YSM extractor, reverse-engineering, Windows-capture, and Forge 1.20.1 macOS overlay work unless the user explicitly asks to merge context.
- Summarize each completed substantive action in `memory/`.
- Skip `memory/` entries for minor checks and small modifications that would not harm future continuity if omitted.
- Name each log file to match the action it records in a clear, logical way.
- At the start of every new session, before proceeding with substantive work, always perform a memory-audit pass.
- Always spawn one memory-audit subagent using `gpt-5.4-mini` with `medium` reasoning and do not proceed to forward work until this is done.
- That subagent should compare the latest local `memory/` notes against older local notes and, when useful, `~/.codex/memories/MEMORY.md`, then refresh or confirm the active `modern_port` dedupe note before widening into a new lane.
- Treat `ysm-2.6.4-fabric+mc1.21.1-release.jar` as a local-only compatibility input. Do not commit it and do not upload it as a release asset.
- Build checks for this repo:
  - Fabric: `GRADLE_USER_HOME=/tmp/gradle-modern-port ./gradlew --no-daemon build`
  - Forge 1.21.11: `GRADLE_USER_HOME=/tmp/gradle-modern-port ./gradlew -p forge1211 --no-daemon build`
