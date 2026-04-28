package com.elfmcys.yesstevemodel.mixin;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientConnection.class)
public interface ConnectionAccessor {
    @Accessor("channel")
    Channel channel();

    @Deprecated
    default Channel OoO0O0oO00O0o0OOOOoOOooo() {
        return channel();
    }
}
