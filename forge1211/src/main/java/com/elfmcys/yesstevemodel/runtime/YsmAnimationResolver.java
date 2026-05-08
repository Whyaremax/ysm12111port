package com.elfmcys.yesstevemodel.runtime;

public final class YsmAnimationResolver {
    private YsmAnimationResolver() {
    }

    public static String resolve(YsmGeoPack pack, YsmPoseSnapshot pose) {
        if (pose == null) {
            return fallback(pack);
        }
        if (pose.death() && pack.hasAnimation("death")) {
            return "death";
        }
        if (pose.sleeping() && pack.hasAnimation("sleep")) {
            return "sleep";
        }
        if (pose.flying() && pack.hasAnimation("fly")) {
            return "fly";
        }
        if (pose.gliding()) {
            if (pack.hasAnimation("elytra_fly")) {
                return "elytra_fly";
            }
            if (pack.hasAnimation("elytra")) {
                return "elytra";
            }
        }
        if (pose.riptide() && pack.hasAnimation("riptide")) {
            return "riptide";
        }
        if (pose.swimming()) {
            if (pack.hasAnimation("swim")) {
                return "swim";
            }
        } else if (pose.swimStanding() && pack.hasAnimation("swim_stand")) {
            return "swim_stand";
        }
        if (pose.boat() && pack.hasAnimation("boat")) {
            return "boat";
        }
        if (pose.pigRide() && pack.hasAnimation("ride_pig")) {
            return "ride_pig";
        }
        if (pose.riding()) {
            if (pack.hasAnimation("ride")) {
                return "ride";
            }
            if (pack.hasAnimation("sit")) {
                return "sit";
            }
        }
        if (pose.climbing()) {
            if (pack.hasAnimation("climbing")) {
                return "climbing";
            }
            if (pack.hasAnimation("climb")) {
                return "climb";
            }
        } else if (pose.climb() && pack.hasAnimation("climb")) {
            return "climb";
        }
        if (pose.sneaking()) {
            if (pose.sneakingIdle() && pack.hasAnimation("sneaking")) {
                return "sneaking";
            }
            if (pack.hasAnimation("sneak")) {
                return "sneak";
            }
        }
        if (pose.jumping() && pack.hasAnimation("jump")) {
            return "jump";
        }
        if (pose.moving()) {
            if (pose.sprinting() && pack.hasAnimation("run")) {
                return "run";
            }
            if (pack.hasAnimation("walk")) {
                return "walk";
            }
        }
        if (pack.hasAnimation("idle")) {
            return "idle";
        }
        return fallback(pack);
    }

    private static String fallback(YsmGeoPack pack) {
        return pack.animationNames().stream().findFirst().orElse(null);
    }
}
