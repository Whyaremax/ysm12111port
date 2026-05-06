# Modern Port 3DModelNow Identity and Inventory Slice (May 5)

## Summary

Started the broader `modern_port` flip from a YSM-named overlay toward the standalone `3DModelNow` project, while keeping the implementation focused on the current Fabric 1.21.11 lane.

## Changes made

- Renamed the public mod identity in `modern_port`:
  - archive base name: `3dmodelnow`
  - Fabric mod id: `threed_model_now`
  - display name: `3DModelNow`
  - license metadata: `MIT`
- Replaced `modern_port/README.md` with a 3DModelNow README that:
  - credits original YSM contributors and `OpenYSM/YSMParser`
  - states the project is unofficial and not affiliated with named upstream projects
  - states the 1.21.11 YSM-compatibility lane will be maintained until official YSM ships 1.21.11, then archived unless it has become useful to the broader standalone project
  - documents future legacy-version ambition for 1.12.2 and 1.7.10
- Added `modern_port/LICENSE` with MIT text for the source-owned workspace code.
- Moved the model-selection hotkey path off handwritten obfuscated classes:
  - added readable `ModelSelectionKeyBindings`
  - deleted the old source-owned obfuscated keybinding files
  - kept the old binary names excluded from the original jar so they do not re-enter the packaged jar
- Added a source-owned `InventoryScreenMixin` and client mixin registration for the inventory preview path.
- Added inventory-preview markers in `YsmRenderBridge`; the existing local-player render bridge remains the actual selected-model renderer.

## Compatibility boundary

- The public mod id is now `threed_model_now`.
- The `yes_steve_model` namespace is still retained for YSM-compatible assets and packet/channel compatibility where needed.
- The package remains `com.elfmcys.yesstevemodel` in this slice to avoid a destructive package-wide move while other original-jar compatibility classes remain.

## Verification

Built successfully:

```bash
GRADLE_USER_HOME=/tmp/gradle-modern-port ./gradlew --no-daemon build
```

Verified output jar:

- `modern_port/build/libs/3dmodelnow-0.1.0+mc1.21.11.jar`
- `fabric.mod.json` reports `id=threed_model_now`, `name=3DModelNow`, `license=MIT`
- jar contains `ModelSelectionKeyBindings.class`
- jar contains `mixin/client/InventoryScreenMixin.class`
- jar contains bundled Linux `YSMParser` assets

## Remaining work

- This is not the complete whole-project rewrite.
- The selection screen is still the current Java replacement screen, only renamed publicly to 3DModelNow.
- The inventory preview hook is restored at the mixin level, but it still relies on the current local-player render bridge for actual YSM rendering.
- Many source-owned compatibility classes remain obfuscated and should be replaced subsystem by subsystem.
