package com.elfmcys.yesstevemodel.runtime;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public final class YsmGeoModelAdapter extends GeoModel<YsmGeoAnimatablePlayer> {
    private final YsmGeoPack pack;

    public YsmGeoModelAdapter(YsmGeoPack pack) {
        this.pack = pack;
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return this.pack.modelResource();
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return this.pack.textureResource();
    }

    @Override
    public Identifier getAnimationResource(YsmGeoAnimatablePlayer animatable) {
        return this.pack.animationResource();
    }
}
