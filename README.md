# YSM 2.6.4 Fabric 1.21.1 → Fabric 1.21.11 Port Workspace

This repository is an unofficial workspace for porting selected Java-side functionality from the original YSM `1.21.1` build to Fabric `1.21.11`.

## Unofficial project notice

This project is not affiliated with, endorsed by, maintained by, or connected to the official Yes Steve Model project, the YSM/CIT Resewn team site, or the Modrinth Yes Steve Model page:

- https://ysm.cfpa.team/
- https://modrinth.com/mod/yes-steve-model

This repository exists only as an independent compatibility and porting workspace.

## Project approach

This workspace repackages selected Java-side content from the original `1.21.1` JAR, applies `1.21.11` metadata, and replaces runtime-critical client/common classes with code maintained in this repository.

## Native policy

- Original native artifacts are not included in this port output.
- `META-INF/native/**`, `.dll`, `.so`, `.dylib`, `.jnilib`, `ysm-core*`, `libysm-core*`, and native/JNI-looking bridge classes are blocked from the packaged JAR.
- The build runs `verifyNoNativeArtifacts` after `jar`, so the build fails if any blocked native artifact appears in the output.
- Runtime selection and import should rely on this repository's Java-side implementation and the Python extractor path, not the original native capability path.

## Current state

- The native bootstrap has been replaced with a Java runtime shim.
- A focused client runtime owns config handling, pack discovery, model selection, and import state.
- `Alt+Y` opens a `1.21.11` model screen.
- The model screen includes a dedicated texture-selection screen and fallback texture-cycling buttons.
- Bundled models are scanned from `assets/yes_steve_model/builtin/**`.
- Pack validation parses `ysm.json` directly for `files.player.model`, `files.player.animation`, and `files.player.texture`.
- The screen keeps the last valid selection if a pack is missing required player assets.
- `.ysm` files are supported only for format `31`. Extraction is handled by the dedicated extractor: https://github.com/Whyaremax/ILoveOpenYSM
- Legacy HUD overlays are disabled until a new preview/render path is implemented.
- The initial local-player render bridge is enabled for the body, arms, and first-person hands using `1.21.11` buffers.
- GeckoLib model, animation, and texture resources are versioned per compile to reduce stale-cache animation glitches.
- Optional compatibility bootstrap is wired for `firstperson` / `firstpersonmod` and `playeranimator` when those mods are present.
- `forge-config-api-port` is no longer packaged, although some legacy compatibility stubs still reference its types.
- Enabled common mixins: `AbstractArrowEntityMixin`, `ConnectionAccessor`, `EntityMixin`, `LivingEntityMixin`, `ProjectileEntityMixin`, `ServerGameConnectionAccessor`, `ServerPlayerMixin`.
- Enabled client mixins: `GuiMixin`, `ItemInHandRendererMixin`, `LivingRendererMixin`, `PlayerRendererMixin`, `WorldRendererMixin`.
- Deferred for later rewrite: inventory preview, pause/editor hooks, other-player render parity, and NBT persistence hooks.

## Build

```bash
cd modern_port/port1211
GRADLE_USER_HOME=.gradle ./gradlew build
```
