package com.elfmcys.yesstevemodel.mixin;

import com.elfmcys.yesstevemodel.O00OOOOOOo0OO0O0O0000O0O;
import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
        method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z",
        at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;")
    )
    private void ysm$onAddStatusEffect(
        StatusEffectInstance effect,
        Entity source,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (!YesSteveModel.isAvailable()) {
            return;
        }

        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof ServerPlayerEntity player) {
            O00OOOOOOo0OO0O0O0000O0O.OoO0O0oO00O0o0OOOOoOOooo(player, effect);
        }
    }
}
