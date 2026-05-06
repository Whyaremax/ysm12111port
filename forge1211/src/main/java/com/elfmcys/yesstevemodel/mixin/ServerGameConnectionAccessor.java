package com.elfmcys.yesstevemodel.mixin;

import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerCommonPacketListenerImpl.class)
public interface ServerGameConnectionAccessor {
    @Accessor("connection")
    Connection connection();

    @Deprecated
    default Connection OoO0O0oO00O0o0OOOOoOOooo() {
        return connection();
    }
}
