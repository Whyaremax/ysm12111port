# modern_port Forge 1.21.11 release lane

## Summary

Added a parallel Forge 1.21.11 lane under `modern_port/forge1211` for the 3DModelNow release shape:

- Base jar: `3dmodelnow-forge-1211-0.1.0+mc1.21.11.jar`
- Optional YSM addon jar: `3dmodelnow-ysm-compat-forge-0.1.0+mc1.21.11.jar`

The Fabric/Loom project remains the root build. The Forge lane is invoked separately with `./gradlew -p forge1211 build` and targets Forge `1.21.11-61.1.0`.

## Implementation Notes

- Added ForgeGradle 7 build metadata, Forge `mods.toml` files, Forge mixin manifests, and Forge entrypoints for the base and addon jars.
- Ported Fabric loader calls behind Forge platform helpers for config/game-dir lookup, mod presence checks, and classpath lookup.
- Registered Forge client key mappings, client tick callbacks, client command registration, and base render mixins in the base jar.
- Kept YSM compatibility in the addon jar, including provider registration, YSM pack/assets, import plumbing, and a limited Forge addon mixin set.
- Excluded original native/JNI artifacts, upstream nested jars, and export/native-looking classes from packaged Forge artifacts.
- Updated the wrapper to Gradle 9.3.0 because ForgeGradle 7 requires it; the existing Fabric build was rechecked afterward.

## Verification

Commands run:

```bash
GRADLE_USER_HOME=/tmp/gradle-modern-port ./gradlew -p forge1211 --no-daemon build
GRADLE_USER_HOME=/tmp/gradle-modern-port ./gradlew --no-daemon build
```

Both builds completed successfully.

Jar inspection confirmed:

- Forge base jar contains `threed_model_now.forge.mixins.json` and `META-INF/mods.toml`.
- Forge base jar does not contain `com/elfmcys`, `assets/yes_steve_model`, `com/threedmodelnow/compat/ysm`, native artifacts, nested upstream jars, or the upstream YSM release jar.
- Forge addon jar contains `yes_steve_model.forge.client.mixins.json` and the advertised addon mixin classes.
- Forge addon jar contains YSM compatibility classes/assets and `tools/ysmparser/linux/YSMParser`.
- Forge addon jar does not contain blocked `.so`, `.dll`, `.dylib`, `.jnilib`, nested upstream jars, or `ysm-2.6.4-fabric+mc1.21.1-release.jar`.

## Publication

Committed and pushed the source changes to `Whyaremax/3DModelNow` on `main` as `124501d` (`Add Forge 1.21.11 release lane`).

Uploading the two Forge jars to the existing GitHub release `v0.1.0-mc1.21.11` was blocked by GitHub:

```text
HTTP 422: Cannot upload assets to an immutable release.
```

The release API reports `immutable: true`, and the release still contains only the existing Fabric assets.

Created a separate Forge release instead:

- Tag: `v0.1.0-mc1.21.11-forge`
- URL: `https://github.com/Whyaremax/3DModelNow/releases/tag/v0.1.0-mc1.21.11-forge`
- Assets:
  - `3dmodelnow-forge-1211-0.1.0+mc1.21.11.jar`
  - `3dmodelnow-ysm-compat-forge-0.1.0+mc1.21.11.jar`

The separate Forge release asset list was verified to exclude `ysm-2.6.4-fabric+mc1.21.1-release.jar`.

## Remaining Boundary

No headed Forge runtime smoke was performed in this pass. The implementation is build- and package-verified, but loader launch validation on Forge `1.21.11-61.1.0` remains the next runtime proof step.
