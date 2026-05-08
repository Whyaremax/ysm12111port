# modern_port remove upstream jar and publish own release - May 5

Removed the checked-in upstream YSM binary from the public 3DModelNow repository:

- Stopped tracking `modern_port/ysm-2.6.4-fabric+mc1.21.1-release.jar`.
- Added the upstream jar name to `modern_port/.gitignore` so it remains a local-only build input.
- Updated `modern_port/README.md` to explain that the upstream YSM jar is not a repository artifact and is used only for source rebuilds of the optional compatibility addon.
- Pushed commit `a6689ef` (`Stop tracking upstream YSM jar`) to `Whyaremax/3DModelNow` on `main`.

Published project-owned release artifacts instead:

- Release: `v0.1.0-mc1.21.11`
- URL: `https://github.com/Whyaremax/3DModelNow/releases/tag/v0.1.0-mc1.21.11`
- Assets:
  - `3dmodelnow-fabric-1211-0.1.0+mc1.21.11.jar`
  - `3dmodelnow-ysm-compat-0.1.0+mc1.21.11.jar`

Validation:

```bash
GRADLE_USER_HOME=/tmp/gradle-modern-port ./gradlew --no-daemon build
```

Result: build successful. `git ls-tree -r --name-only HEAD | rg 'ysm-2\.6\.4|3dmodelnow.*\.jar'` returned no matches, confirming the current source tree tracks neither the upstream jar nor built release jars.

Note: this commit removes the upstream jar from the current branch head. The large blob still exists in old Git history unless history is rewritten and force-pushed.
