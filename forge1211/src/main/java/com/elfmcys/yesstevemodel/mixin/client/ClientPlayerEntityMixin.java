package com.elfmcys.yesstevemodel.mixin.client;

import com.elfmcys.yesstevemodel.runtime.YsmAnimationResolver;
import com.elfmcys.yesstevemodel.runtime.YsmGeoAnimatablePlayer;
import com.elfmcys.yesstevemodel.runtime.YsmGeoPack;
import com.elfmcys.yesstevemodel.runtime.YsmGeoPlayerRenderer;
import com.elfmcys.yesstevemodel.runtime.YsmRenderStateStore;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.util.GeckoLibUtil;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin implements YsmGeoAnimatablePlayer {
    @Unique
    private final AnimatableInstanceCache ysm$animatableCache = GeckoLibUtil.createInstanceCache((YsmGeoAnimatablePlayer) this);

    @Unique
    private final Map<String, RawAnimation> ysm$rawAnimations = new LinkedHashMap<>();

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("player", 4, this::ysm$selectMainAnimation));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.ysm$animatableCache;
    }

    @Unique
    private PlayState ysm$selectMainAnimation(AnimationTest<YsmGeoAnimatablePlayer> state) {
        YsmGeoPack pack = YsmRenderStateStore.activePack();
        if (pack == null) {
            return PlayState.STOP;
        }

        String animation = YsmAnimationResolver.resolve(pack, state.getDataOrDefault(YsmGeoPlayerRenderer.POSE_SNAPSHOT, null));
        if (animation == null) {
            return PlayState.STOP;
        }

        return state.setAndContinue(this.ysm$rawAnimations.computeIfAbsent(animation, key -> RawAnimation.begin().thenLoop(key)));
    }
}
