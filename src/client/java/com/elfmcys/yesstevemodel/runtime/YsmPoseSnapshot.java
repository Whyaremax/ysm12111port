package com.elfmcys.yesstevemodel.runtime;

public record YsmPoseSnapshot(
    float bodyYaw,
    float headYaw,
    float relativeHeadYaw,
    float headPitch,
    float movementMagnitude,
    float verticalSpeed,
    float inputVertical,
    float inputHorizontal,
    float elytraRoll,
    boolean moving,
    boolean sprinting,
    boolean sneaking,
    boolean sneakingIdle,
    boolean jumping,
    boolean flying,
    boolean gliding,
    boolean riptide,
    boolean swimming,
    boolean swimStanding,
    boolean riding,
    boolean boat,
    boolean pigRide,
    boolean climb,
    boolean climbing,
    boolean sleeping,
    boolean death,
    boolean hasMainHand,
    boolean hasOffhand,
    int foodLevel
) {
}
