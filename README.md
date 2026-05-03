# YSM 2.6.4 Fabric 1.21.1 → Fabric 1.21.11 Port Workspace

This repository is an unofficial compatibility workspace for porting selected Java-side functionality from the original YSM `1.21.1` build to Fabric `1.21.11`.

> [!IMPORTANT]
> This project is not affiliated with, endorsed by, maintained by, or connected to the official Yes Steve Model project, the YSM/CIT Resewn team site, or the Modrinth Yes Steve Model page.
>
> Official/project pages this repository is **not** related to:
>
> - https://ysm.cfpa.team/
> - https://modrinth.com/mod/yes-steve-model

> [!NOTE]
> This repository exists only as an independent compatibility, porting, and interoperability workspace.

## Project status

This port is experimental and actively being rewritten around source-owned Java-side behavior.

The current goal is to keep the `1.21.11` client path usable while removing dependency on the original native runtime and replacing runtime-critical behavior with code maintained in this repository.

> [!WARNING]
> This is not an official release of Yes Steve Model. Expect incomplete features, missing UI parity, and behavior differences from the original mod.

## What this project is

- An unofficial Fabric `1.21.11` porting workspace.
- A Java-side compatibility effort for selected YSM client/common behavior.
- A place to replace runtime-critical classes with source-owned code.
- A testbed for model selection, pack discovery, import state, and render bridge work.
- A companion project to the external extractor path used for supported `.ysm` imports.

## What this project is not

- It is not the official Yes Steve Model project.
- It is not connected to `ysm.cfpa.team` or the Modrinth Yes Steve Model page.
- It is not a full replacement for every original YSM feature yet.
- It does not include the original native runtime capability.
- It does not package original native artifacts such as `.dll`, `.so`, `.dylib`, `.jnilib`, `ysm-core*`, or `libysm-core*`.

## Project approach

This workspace repackages selected Java-side content from the original `1.21.1` JAR, applies `1.21.11` metadata, and replaces runtime-critical client/common classes with code maintained in this repository.

> [!TIP]
> The current focus is practical runtime stability first: launch, config, pack discovery, selection UI, import state, and local-player rendering.

## Native policy

- Original native artifacts are not included in this port output.
- `META-INF/native/**`, `.dll`, `.so`, `.dylib`, `.jnilib`, `ysm-core*`, `libysm-core*`, and native/JNI-looking bridge classes are blocked from the packaged JAR.
- The build runs `verifyNoNativeArtifacts` after `jar`, so the build fails if any blocked native artifact appears in the output.
- Runtime selection and import should rely on this repository's Java-side implementation and the Python extractor path, not the original native capability path.

> [!CAUTION]
> Native/JNI functionality is intentionally not restored from the original native library. Any replacement behavior should be independently implemented or routed through the maintained extractor path.

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

> [!NOTE]
> `.ysm` import support currently depends on the dedicated extractor path and is limited to format `31`.

## Build

```bash
cd modern_port/port1211
GRADLE_USER_HOME=.gradle ./gradlew build
```

> [!IMPORTANT]
> The build should fail if blocked native artifacts appear in the packaged output.

## Roadmap

- Continue replacing decompiled/obfuscated Java-side behavior with readable source-owned code.
- Improve model selection UI parity.
- Continue reducing animation glitches and stale cache behavior.
- Restore or replace missing preview/render paths.
- Improve other-player render parity.
- Add safer import handling as extractor support improves.
- Remove abandoned compatibility stubs when they are no longer needed.

## Related project

`.ysm` extraction support is handled by the dedicated extractor project:

- https://github.com/Whyaremax/ILoveOpenYSM

## License

This repository uses a custom source-available notice. See [LICENSE](LICENSE).

> [!CAUTION]
> This repository is publicly viewable for compatibility research, interoperability work, personal porting, review, and educational inspection. It is not MIT-licensed, and no permission is granted to sell, repackage, sublicense, or commercially distribute this repository or builds derived from it.

## Disclaimer

This repository is provided for compatibility, interoperability, research, and personal porting work. It is provided "as is", without warranty of any kind.

> [!IMPORTANT]
> You are responsible for making sure you have the right to use, inspect, modify, port, or redistribute any model, texture, animation, code, or other asset involved in your workflow.
