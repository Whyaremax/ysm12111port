# Modern Port YSMParser Import Backend Integration (May 5)

## Summary

Switched the `modern_port` `.ysm` import path to prefer `OpenYSM/YSMParser` while keeping the existing bundled Python extractor as a fallback. The change stays local to the importer/runtime contract that already expects normalized pack folders with `ysm.json`, `models`, `animations`, and `textures`.

## What changed

- Patched `modern_port/src/client/java/com/elfmcys/yesstevemodel/runtime/YsmPythonImporter.java`.
- `.zip` and loose-folder imports are unchanged.
- `.ysm` imports now:
  - look for an explicit parser executable via `yes_steve_model.ysmparser.executable` or `YSM_PARSER_EXECUTABLE`
  - look for an explicit parser bundle via `yes_steve_model.ysmparser.bundle` or `YSM_PARSER_BUNDLE`
  - materialize a bundled parser from `tools/ysmparser/<platform>`
  - fall back to `YSMParser` on `PATH`
  - fall back to the existing bundled Python extractor if parser execution is unavailable or fails
- Added import-detail metadata so imported packs record which backend was used.

## Bundled assets

Added a Linux parser bundle under:

- `modern_port/tools/ysmparser/linux/YSMParser`
- `modern_port/tools/ysmparser/linux/libYSMParserJNI.so`
- `modern_port/tools/ysmparser/linux/LICENSE.txt`

This matches the current dev/build host and lets the Fabric runtime extract a real bundled parser during import tests.

## Verification

Built successfully with:

```bash
GRADLE_USER_HOME=/tmp/gradle-modern-port ./gradlew --no-daemon build
```

Verified the remapped jar contains both the importer class and the bundled parser assets:

- `modern_port/build/libs/yes-steve-model-2.6.4-fabric+mc1.21.11-port26.jar`
- `com/elfmcys/yesstevemodel/runtime/YsmPythonImporter.class`
- `tools/ysmparser/linux/YSMParser`
- `tools/ysmparser/linux/libYSMParserJNI.so`
- `tools/ysmparser/linux/LICENSE.txt`

## Remaining gap

- Only the Linux parser bundle was added in this pass.
- Windows and macOS can still import through:
  - an explicit parser bundle/executable
  - a `YSMParser` binary on `PATH`
  - the existing Python extractor fallback
- If we want first-class parser-backed import on Windows or macOS inside `modern_port`, add matching `tools/ysmparser/windows` and `tools/ysmparser/macos` bundles next.
