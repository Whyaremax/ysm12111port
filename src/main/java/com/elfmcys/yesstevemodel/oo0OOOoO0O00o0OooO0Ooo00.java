package com.elfmcys.yesstevemodel;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public final class oo0OOOoO0O00o0OooO0Ooo00 {
    private oo0OOOoO0O00o0OooO0Ooo00() {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(ooO00ooOOOoO00oOoo00O00o bootstrap) {
        Entity entity = bootstrap.oo0Oo0oo0OOO00O0oO0o0O0O();
        World world = ysm$getWorld(entity);
        boolean clientWorld = world != null && world.isClient();

        if (entity instanceof PlayerEntity) {
            bootstrap.OoO0O0oO00O0o0OOOOoOOooo(new oOo0O00Oooo00OOO0OO0oooo());
            bootstrap.OoO0O0oO00O0o0OOOOoOOooo(new OOo00OO0o0Oo0OO0OO0OOO0o());
        }

        if (!clientWorld) {
            return;
        }

        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return;
        }

        o0OoOOoo0oO00ooOoO0oo00o<?> clientAttachment = ysmCreateClientPlayerAttachment(entity);
        if (clientAttachment != null) {
            bootstrap.OoO0O0oO00O0o0OOOOoOOooo(clientAttachment);
            return;
        }

        if (OOoOo0OO00000oO00OOO0oOo.oo0Oo0oo0OOO00O0oO0o0O0O(entity)) {
            bootstrap.OoO0O0oO00O0o0OOOOoOOooo(OOoOo0OO00000oO00OOO0oOo.OoO0O0oO00O0o0OOOOoOOooo(entity));
            return;
        }

        bootstrap.OoO0O0oO00O0o0OOOOoOOooo(new oOooooOooO0oO0ooOOoo00oo(entity));
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(Entity entity, ServerWorld world) {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(Entity entity, ServerPlayerEntity player) {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(MinecraftServer server) {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(net.minecraft.entity.vehicle.AbstractMinecartEntity vehicle, ServerPlayerEntity player) {
    }

    public static void oo0Oo0oo0OOO00O0oO0o0O0O(Entity entity, ServerPlayerEntity player) {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(ServerPlayerEntity player, boolean force) {
    }

    private static World ysm$getWorld(Entity entity) {
        for (String methodName : new String[]{"getWorld", "getEntityWorld", "method_37908"}) {
            try {
                Object world = entity.getClass().getMethod(methodName).invoke(entity);
                if (world instanceof World typedWorld) {
                    return typedWorld;
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static o0OoOOoo0oO00ooOoO0oo00o<?> ysmCreateClientPlayerAttachment(Entity entity) {
        try {
            Class<?> clientPlayerClass = Class.forName("net.minecraft.class_742");
            if (!clientPlayerClass.isInstance(entity)) {
                return null;
            }
            return (o0OoOOoo0oO00ooOoO0oo00o<?>) O0o0O00O0oOOoOOoo00OO0o0.class
                .getConstructor(clientPlayerClass)
                .newInstance(entity);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
