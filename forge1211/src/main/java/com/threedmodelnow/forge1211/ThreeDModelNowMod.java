package com.threedmodelnow.forge1211;

import com.threedmodelnow.core.ThreeDModelNow;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ThreeDModelNow.MOD_ID)
public final class ThreeDModelNowMod {
    public ThreeDModelNowMod(FMLJavaModLoadingContext context) {
        ThreeDModelNow.LOGGER.info("3DModelNow core initialized");
        ModelSelectionKeyBindings.register(context.getModBusGroup());
        ThreeDModelNow.LOGGER.info("3DModelNow Forge 1.21.11 client initialized");
    }
}
