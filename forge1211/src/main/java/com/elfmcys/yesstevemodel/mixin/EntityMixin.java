package com.elfmcys.yesstevemodel.mixin;

import com.elfmcys.yesstevemodel.OooOo0oOoOo000o0oO000Oo0;
import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.oOOoo0O0oO0OoOOOO00oO0O0;
import com.elfmcys.yesstevemodel.oOoO0oo0Oooo0oOOO0OoO0Oo;
import com.elfmcys.yesstevemodel.oo0OOOoO0O00o0OooO0Ooo00;
import com.elfmcys.yesstevemodel.ooO00ooOOOoO00oOoo00O00o;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class, priority = 500)
public abstract class EntityMixin implements OooOo0oOoOo000o0oO000Oo0 {
    @Unique
    @Nullable
    private oOOoo0O0oO0OoOOOO00oO0O0 OoO0O0oO00O0o0OOOOoOOooo;

    @Override
    public @NotNull <T> Optional<T> OoO0O0oO00O0o0OOOOoOOooo(oOoO0oo0Oooo0oOOO0OoO0Oo<T> key) {
        if (this.OoO0O0oO00O0o0OOOOoOOooo != null) {
            return this.OoO0O0oO00O0o0OOOOoOOooo.OoO0O0oO00O0o0OOOOoOOooo(key);
        }
        return Optional.empty();
    }

    public @Nullable oOOoo0O0oO0OoOOOO00oO0O0 ooOO0oo000O00OOoooO0o0Oo() {
        return this.OoO0O0oO00O0o0OOOOoOOooo;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
    private void ysm$initAttachment(EntityType<?> type, Level world, CallbackInfo ci) {
        if (!YesSteveModel.isAvailable() || !world.isClientSide()) {
            return;
        }

        ooO00ooOOOoO00oOoo00O00o bootstrap = new ooO00ooOOOoO00oOoo00O00o((Entity) (Object) this);
        oo0OOOoO0O00o0OooO0Ooo00.OoO0O0oO00O0o0OOOOoOOooo(bootstrap);
        if (!bootstrap.OoO0O0oO00O0o0OOOOoOOooo()) {
            this.OoO0O0oO00O0o0OOOOoOOooo = new oOOoo0O0oO0OoOOOO00oO0O0(bootstrap);
        }
    }
}
