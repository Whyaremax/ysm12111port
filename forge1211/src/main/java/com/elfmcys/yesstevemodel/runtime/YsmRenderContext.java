package com.elfmcys.yesstevemodel.runtime;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public record YsmRenderContext(
    LocalPlayer player,
    LivingEntityRenderState vanillaState,
    YsmPoseSnapshot poseSnapshot
) {
}
