# modern_port 3DModelNow addon split - May 5

Implemented the first working split for the Fabric 1.21.11 `modern_port` lane:

- Base runtime jar is `3dmodelnow-fabric-1211-0.1.0+mc1.21.11.jar`.
- Optional YSM compatibility jar is `3dmodelnow-ysm-compat-0.1.0+mc1.21.11.jar`.
- Base jar owns `threed_model_now`, generic provider registries, a generic model browser, generic render hooks, and no bundled YSM implementation classes/assets.
- YSM addon owns provider id `ysm`, registers the YSM browser/render adapter, keeps YSM mixins/assets/runtime classes, and depends on the base jar plus GeckoLib.
- New YSM config writes go to `config/3dmodelnow/ysm/client-runtime.json`; old `config/yes_steve_model/client-runtime.json` and legacy imported packs are read as non-destructive fallback.
- Bundled YSM pack/import resource lookup now checks the addon mod container first, then falls back to the base id for older dev layouts.

Validation:

```bash
GRADLE_USER_HOME=/tmp/gradle-modern-port ./gradlew --no-daemon clean build
```

Result: build successful. Jar inspection confirmed the base jar contains no `com/elfmcys`, no `yes_steve_model` resources, and no `com/threedmodelnow/compat/ysm` classes, while the addon jar contains `YsmCompatAddon`, YSM runtime classes, YSM mixin metadata, and bundled `assets/yes_steve_model` packs.
