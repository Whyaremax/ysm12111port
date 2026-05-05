# 3DModelNow

3DModelNow is an unofficial Fabric `1.21.11` 3D player-model framework. The base mod is standalone, and YSM/OpenYSM support is provided by an optional compatibility addon jar.

> [!IMPORTANT]
> This project is not affiliated with, endorsed by, maintained by, or connected to the official Yes Steve Model project, the YSM/CIT Resewn team site, the Modrinth Yes Steve Model page, CFPA, OpenYSM, Figura, Customizable Player Models, GeckoLib, Fabric, Mojang, or Microsoft.

Names are used only to identify compatibility targets, upstream inspiration, bundled compatibility data, parser tooling, or libraries.

## Current Target

- Loader: Fabric
- Minecraft: `1.21.11`
- Base mod id: `threed_model_now`
- Optional YSM addon id: `threed_model_now_ysm_compat`
- First compatibility lane: YSM 2.6.x-style pack loading, import, selection, and local-player rendering

The base jar owns generic model provider registries, model browser plumbing, and render hooks. The YSM addon keeps the `yes_steve_model` resource and network namespace only where required for existing YSM assets, pack layout, or protocol assumptions.

## Optional Addons

The build produces two runtime jars:

- `3dmodelnow-fabric-1211-<version>.jar`: the base Fabric 1.21.11 client jar. It registers the generic model browser and render hooks, and falls back to vanilla/default rendering when no addon is installed.
- `3dmodelnow-ysm-compat-<version>.jar`: optional YSM compatibility addon. Install it beside the base jar to enable YSM/OpenYSM import, bundled/local YSM pack discovery, YSM selection UI, and the current YSM display adapter.

The base jar does not include YSM runtime classes, bundled YSM assets, YSM mixins, or the GeckoLib dependency declaration. Those are scoped to the optional YSM compatibility jar.

## Current State

- Base jar alone opens a generic 3DModelNow model browser and does not expose YSM importers or displays.
- With the YSM addon installed, builtin YSM packs are scanned from `assets/yes_steve_model/builtin/**`.
- With the YSM addon installed, dropped `.ysm`, `.zip`, and loose-folder imports are synchronized from the imports folder.
- `.ysm` import prefers `OpenYSM/YSMParser` when available, with the bundled Python extractor retained as fallback.
- Third-person local-player rendering and inventory preview route through the generic base render service, with YSM rendering supplied by the addon provider.
- First-person and full original-client parity are still incomplete and should be treated as active rewrite work.

## Data Root

New 3DModelNow writes use:

```text
config/3dmodelnow/
```

YSM compatibility data is stored under:

```text
config/3dmodelnow/ysm/
```

On first load, the YSM addon reads legacy `config/yes_steve_model/client-runtime.json` and legacy imported packs as a fallback. Usable selection metadata is migrated by writing normalized config to the new root. Legacy files and imported folders are not deleted or destructively rewritten.

## Native Policy

- Original native artifacts are not included in the packaged output.
- `META-INF/native/**`, `.dll`, `.so`, `.dylib`, `.jnilib`, `ysm-core*`, `libysm-core*`, and native/JNI-looking bridge classes are blocked from the packaged jars.
- Runtime selection and import should rely on this repository's Java-side implementation and extractor/parser paths, not the original native capability path.

## Maintenance Policy

The Fabric 1.21.11 YSM-compatibility lane will be maintained until the official YSM project ships a working 1.21.11 release. After that happens, this lane may be archived unless it has become useful as part of the broader standalone 3DModelNow version-support project.

## Credits

- Original Yes Steve Model authors and contributors: YS Group, TartaricAcid, TomatoPuddin, AryochiL, and the wider YSM contributor community.
- Parser/import reference and tooling: [OpenYSM/YSMParser](https://github.com/OpenYSM/YSMParser).
- Related extractor project: [Whyaremax/ILoveOpenYSM](https://github.com/Whyaremax/ILoveOpenYSM).
- Rendering library: GeckoLib and its maintainers.
- Minecraft modding platform: Fabric and Yarn maintainers.

Credits do not imply endorsement, affiliation, or responsibility for this project.

## Build

The upstream YSM release jar is not tracked in this repository. To rebuild the optional compatibility addon from source, keep a local copy of `ysm-2.6.4-fabric+mc1.21.1-release.jar` in the project root. It is used only as a local compatibility input for classes/assets that are repackaged into the project-owned addon jar; it is not published as a repository artifact.

```bash
GRADLE_USER_HOME=/tmp/gradle-modern-port ./gradlew --no-daemon build
```

Output jars are written to `build/libs/`:

- `3dmodelnow-fabric-1211-0.1.0+mc1.21.11.jar`
- `3dmodelnow-ysm-compat-0.1.0+mc1.21.11.jar`

## License

This repository uses a custom source-available notice. See [LICENSE](LICENSE).

> [!CAUTION]
> This repository is publicly viewable for compatibility research, interoperability work, personal porting, review, and educational inspection. It is not MIT-licensed, and no permission is granted to sell, repackage, sublicense, or commercially distribute this repository or builds derived from it.
