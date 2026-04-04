package com.elfmcys.yesstevemodel.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerCommonNetworkHandler.class)
public interface ServerGameConnectionAccessor {
    @Accessor("connection")
    ClientConnection OoO0O0oO00O0o0OOOOoOOooo();
}
