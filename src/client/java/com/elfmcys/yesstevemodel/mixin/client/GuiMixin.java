package com.elfmcys.yesstevemodel.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class GuiMixin {
    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("HEAD"))
    private void ysm$renderOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        // The legacy HUD overlays still depend on Forge-config and the old 1.21.1
        // preview pipeline. Keep them fully disabled until the new preview path is
        // rebuilt on 1.21.11.
    }
}
