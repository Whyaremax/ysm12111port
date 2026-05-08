package com.elfmcys.yesstevemodel.runtime;

import java.io.IOException;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.cache.model.GeoQuad;
import software.bernie.geckolib.cache.model.cuboid.CuboidGeoBone;
import software.bernie.geckolib.cache.model.cuboid.GeoCube;

public final class YsmModelProbe {
    private YsmModelProbe() {
    }

    public static YsmModelProbeStats inspect(YsmActiveSelection selection) throws IOException {
        if (selection == null || !selection.runtimeApplied()) {
            throw new IOException("No YSM model is currently applied");
        }

        YsmCompiledPack compiledPack = selection.compiledPack();
        YsmGeoPack geoPack = selection.geoPack();
        Identifier modelId = geoPack.modelResource();
        BakedGeoModel bakedModel = YsmGeoResourceBridge.getInstalledModel(modelId)
            .orElseThrow(() -> new IOException("GeckoLib cache is missing baked model " + modelId));

        MutableStats stats = new MutableStats();
        GeoBone[] topLevelBones = bakedModel.topLevelBones();
        for (GeoBone topLevelBone : topLevelBones) {
            walkBone(topLevelBone, stats);
        }

        return new YsmModelProbeStats(
            compiledPack.descriptor().id(),
            compiledPack.descriptor().displayName(),
            compiledPack.selectedTextureId(),
            topLevelBones.length,
            stats.bones,
            stats.objects,
            stats.surfaces,
            stats.surfaces * 2
        );
    }

    public static Optional<YsmPackDescriptor> resolveDescriptor(String probeTarget) {
        if (probeTarget == null) {
            return Optional.empty();
        }

        String normalized = probeTarget.trim();
        if (normalized.isEmpty()) {
            return Optional.empty();
        }

        String lower = normalized.toLowerCase(java.util.Locale.ROOT);
        for (YsmPackDescriptor descriptor : YsmClientRuntime.packs()) {
            if (matches(lower, descriptor.id())
                || matches(lower, descriptor.legacyModelId())
                || matches(lower, descriptor.displayName())
                || matches(lower, stripPrefix(descriptor.id()))) {
                return Optional.of(descriptor);
            }
        }

        return Optional.empty();
    }

    private static boolean matches(String probeTarget, String candidate) {
        return candidate != null && candidate.toLowerCase(java.util.Locale.ROOT).equals(probeTarget);
    }

    private static String stripPrefix(String packId) {
        int separator = packId == null ? -1 : packId.indexOf(':');
        return separator >= 0 ? packId.substring(separator + 1) : packId;
    }

    private static void walkBone(GeoBone bone, MutableStats stats) {
        if (bone == null) {
            return;
        }

        stats.bones++;
        if (bone instanceof CuboidGeoBone cuboidGeoBone && cuboidGeoBone.cubes != null) {
            for (GeoCube cube : cuboidGeoBone.cubes) {
                if (cube == null) {
                    continue;
                }
                stats.objects++;
                GeoQuad[] quads = cube.quads();
                if (quads == null) {
                    continue;
                }
                for (GeoQuad quad : quads) {
                    if (quad != null) {
                        stats.surfaces++;
                    }
                }
            }
        }

        for (GeoBone child : bone.children()) {
            walkBone(child, stats);
        }
    }

    private static final class MutableStats {
        private int bones;
        private int objects;
        private int surfaces;
    }

    public record YsmModelProbeStats(
        String packId,
        String displayName,
        String textureId,
        int rootBones,
        int bones,
        int objects,
        int surfaces,
        int faces
    ) {
    }
}
