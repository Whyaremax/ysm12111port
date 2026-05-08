package com.elfmcys.yesstevemodel.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V", at = @At("HEAD"))
    private void ysm$renderOverlay(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        // The legacy HUD overlays still depend on Forge-config and the old 1.21.1
        // preview pipeline. Keep them fully disabled until the new preview path is
        // rebuilt on 1.21.11.
    }
}
