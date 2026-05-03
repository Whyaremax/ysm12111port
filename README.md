# YSM 2.6.4 1.21.1 Fabric -> Fabric 1.21.11 port workspace

This workspace repackages selected Java-side content from the original `1.21.1` jar, overlays `1.21.11` metadata, and replaces the runtime-critical client/common classes with source-owned code.

Native policy:
- Original native artifacts are not part of this port output.
- `META-INF/native/**`, `.dll`, `.so`, `.dylib`, `.jnilib`, `ysm-core*`, `libysm-core*`, and native/JNI-looking bridge classes are blocked from the packaged jar.
- The build runs `verifyNoNativeArtifacts` after `jar` so these artifacts fail the build if they appear in the output.
- Runtime selection/import should rely on this repo's Java and Python extractor path, not the original native capability path.

Current state:
- Native bootstrap is replaced with a Java runtime shim.
- A narrow client runtime owns config, pack discovery, selection, and import state.
- `Alt+Y` opens a `1.21.11` model screen.
- The model screen has a dedicated texture-selection screen plus texture cycling fallback buttons.
- Bundled models are scanned from `assets/yes_steve_model/builtin/**`.
- Pack validation parses `ysm.json` directly for `files.player.model`, `files.player.animation`, and `files.player.texture`.
- The screen keeps the last good selection if a pack is missing required player assets.
- `.ysm` File are supported, only format `31`, extractors will be directly used from [dedicated extractor](https://github.com/Whyaremax/ILoveOpenYSM)
- Legacy HUD overlays are disabled until a new preview/render path exists.
- Initial local-player render bridge is enabled for body, arms, and first-person hands using `1.21.11` buffers.
- GeckoLib model, animation, and texture resources are versioned per compile to reduce stale cache animation glitches.
- Optional compatibility bootstrap is wired for `firstperson` / `firstpersonmod` and `playeranimator` when those mods are present.
- `forge-config-api-port` is no longer packaged, but some legacy compatibility stubs still reference its types.
- Enabled common mixins: `AbstractArrowEntityMixin`, `ConnectionAccessor`, `EntityMixin`, `LivingEntityMixin`, `ProjectileEntityMixin`, `ServerGameConnectionAccessor`, `ServerPlayerMixin`.
- Enabled client mixins: `GuiMixin`, `ItemInHandRendererMixin`, `LivingRendererMixin`, `PlayerRendererMixin`, `WorldRendererMixin`.
- Deferred for later rewrite: inventory preview, pause/editor hooks, other-player render parity, and NBT persistence hooks.

Build:

```bash
cd modern_port/port1211
GRADLE_USER_HOME=.gradle ./gradlew build
```
