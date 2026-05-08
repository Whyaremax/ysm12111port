package com.elfmcys.yesstevemodel.mixin;

import com.elfmcys.yesstevemodel.O0oOOO0O0OOOOo0oOoOoOOOO;
import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.oo0OOOoO0O00o0OooO0Ooo00;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public class ProjectileEntityMixin {
    @Inject(method = "setOwner(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"))
    private void ysm$afterSetOwner(Entity owner, CallbackInfo ci) {
        if (!YesSteveModel.isAvailable()) {
            return;
        }

        Projectile self = (Projectile) (Object) this;
        if (owner == null || ysm$isClientWorld(self)) {
            return;
        }

        if (owner instanceof ServerPlayer serverPlayer) {
            oo0OOOoO0O00o0OooO0Ooo00.OoO0O0oO00O0o0OOOOoOOooo(self, serverPlayer);
        } else if (O0oOOO0O0OOOOo0oOoOoOOOO.OoO0O0oO00O0o0OOOOoOOooo(owner)) {
            O0oOOO0O0OOOOo0oOoOoOOOO.OoO0O0oO00O0o0OOOOoOOooo(self, owner);
        }
    }

    private static boolean ysm$isClientWorld(Projectile projectile) {
        try {
            Object world = projectile.getClass().getMethod("getWorld").invoke(projectile);
            return world != null && (boolean) world.getClass().getMethod("isClient").invoke(world);
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }
}
