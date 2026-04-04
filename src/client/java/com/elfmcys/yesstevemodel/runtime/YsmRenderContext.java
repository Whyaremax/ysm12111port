package com.elfmcys.yesstevemodel.runtime;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

public record YsmRenderContext(
    ClientPlayerEntity player,
    LivingEntityRenderState vanillaState,
    YsmPoseSnapshot poseSnapshot
) {
}
