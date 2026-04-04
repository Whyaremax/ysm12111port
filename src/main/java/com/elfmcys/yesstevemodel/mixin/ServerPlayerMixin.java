package com.elfmcys.yesstevemodel.mixin;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.oo0OOOoO0O00o0OooO0Ooo00;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin {
    @Inject(
        method = "startRiding(Lnet/minecraft/entity/Entity;ZZ)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;updatePassengerPosition(Lnet/minecraft/entity/Entity;)V",
            shift = At.Shift.AFTER
        )
    )
    private void ysm$afterStartRiding(Entity vehicle, boolean force, boolean keepPos, CallbackInfoReturnable<Boolean> cir) {
        if (!YesSteveModel.isAvailable()) {
            return;
        }

        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
        if (vehicle.getControllingPassenger() == self) {
            oo0OOOoO0O00o0OooO0Ooo00.oo0Oo0oo0OOO00O0oO0o0O0O(vehicle, self);
        }
    }
}
