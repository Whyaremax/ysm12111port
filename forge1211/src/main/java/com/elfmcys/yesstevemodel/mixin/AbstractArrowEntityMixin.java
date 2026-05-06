package com.elfmcys.yesstevemodel.mixin;

import com.elfmcys.yesstevemodel.OoO0O0oO00O0o0OOOOoOOooo;
import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractArrow.class, priority = 2000)
public abstract class AbstractArrowEntityMixin implements OoO0O0oO00O0o0OOOOoOOooo {
    @Shadow
    protected int inGroundTime;

    @Unique
    private String OoO0O0oO00O0o0OOOOoOOooo = "";

    @Override
    public boolean OoO0O0oO00O0o0OOOOoOOooo() {
        return this.inGroundTime > 0;
    }

    @Override
    public int oo0Oo0oo0OOO00O0oO0o0O0O() {
        return ((AbstractArrow) (Object) this).getPierceLevel();
    }

    @Override
    public String oOoOoO0OoOOoo00ooO0oO00o() {
        return this.OoO0O0oO00O0o0OOOOoOOooo;
    }

    @Inject(method = "setOwner(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"))
    private void ysm$afterSetOwner(Entity owner, CallbackInfo ci) {
        if (!YesSteveModel.isAvailable()) {
            return;
        }

        if (owner instanceof LivingEntity livingEntity) {
            ItemStack stack = livingEntity.getMainHandItem();
            Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (itemId != null) {
                this.OoO0O0oO00O0o0OOOOoOOooo = itemId.toString();
            }
        }
    }
}
