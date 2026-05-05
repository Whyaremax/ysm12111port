package com.elfmcys.yesstevemodel.runtime;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

public final class YsmGeoPlayerRenderer extends GeoObjectRenderer<YsmGeoAnimatablePlayer, YsmRenderContext, GeoRenderState.Impl> {
    public static final DataTicket<LivingEntityRenderState> VANILLA_STATE = DataTicket.create("ysm_vanilla_state", LivingEntityRenderState.class);
    public static final DataTicket<YsmPoseSnapshot> POSE_SNAPSHOT = DataTicket.create("ysm_pose_snapshot", YsmPoseSnapshot.class);
    private static final DataTicket<Float> BODY_YAW = DataTicket.create("ysm_body_yaw", Float.class);
    private static final DataTicket<Float> DEATH_TIME = DataTicket.create("ysm_death_time", Float.class);
    private static final String[] HEAD_ROTATION_BONES = {"AllHead", "MHead", "Head"};
    private final YsmGeoPack pack;


    public YsmGeoPlayerRenderer(YsmGeoPack pack) {
        super(new YsmGeoModelAdapter(pack));
        this.pack = pack;
    }

    public YsmGeoPack pack() {
        return this.pack;
    }

    public void renderBody(
        YsmRenderContext context,
        MatrixStack matrices,
        OrderedRenderCommandQueue queue,
        CameraRenderState cameraRenderState,
        int light,
        float tickProgress
    ) {
        YsmGeoAnimatablePlayer animatable = (YsmGeoAnimatablePlayer) context.player();
        GeoRenderState.Impl renderState = fillRenderState(animatable, context, createRenderState(animatable, context), tickProgress);
        renderState.addGeckolibData(DataTickets.PACKED_LIGHT, light);
        performRenderPass(renderState, matrices, queue, cameraRenderState);
    }

    @Override
    public void addRenderData(YsmGeoAnimatablePlayer animatable, YsmRenderContext relatedObject, GeoRenderState.Impl renderState, float partialTick) {
        LivingEntityRenderState vanillaState = relatedObject.vanillaState();
        YsmPoseSnapshot poseSnapshot = relatedObject.poseSnapshot();

        renderState.addGeckolibData(VANILLA_STATE, vanillaState);
        renderState.addGeckolibData(POSE_SNAPSHOT, poseSnapshot);
        renderState.addGeckolibData(BODY_YAW, vanillaState.bodyYaw);
        renderState.addGeckolibData(DEATH_TIME, vanillaState.deathTime);

        renderState.addGeckolibData(DataTickets.SPRINTING, poseSnapshot.sprinting());
        renderState.addGeckolibData(DataTickets.IS_MOVING, poseSnapshot.moving());
        renderState.addGeckolibData(DataTickets.IS_CROUCHING, poseSnapshot.sneaking());
        renderState.addGeckolibData(DataTickets.IS_DEAD_OR_DYING, poseSnapshot.death());

        renderState.addGeckolibData(DataTickets.ENTITY_YAW, relatedObject.player().getHeadYaw());
        renderState.addGeckolibData(DataTickets.ENTITY_PITCH, relatedObject.player().getPitch());

        renderState.addGeckolibData(DataTickets.VELOCITY, relatedObject.player().getVelocity());
        renderState.addGeckolibData(DataTickets.ELYTRA_ROTATION, new Vec3d(0.0, 0.0, poseSnapshot.elytraRoll()));
        renderState.addGeckolibData(DataTickets.SWINGING_ARM, relatedObject.player().handSwinging);
        renderState.addGeckolibData(DataTickets.IS_LEFT_HANDED, relatedObject.player().getMainArm() != net.minecraft.util.Arm.RIGHT);
    }

    @Override
    public void setMolangQueryValues(YsmGeoAnimatablePlayer animatable, YsmRenderContext relatedObject, GeoRenderState.Impl renderState, float partialTick) {
        YsmPoseSnapshot pose = relatedObject.poseSnapshot();

        MathParser.setVariable("ysm.head_yaw", controller -> -pose.relativeHeadYaw());
        MathParser.setVariable("ysm.head_pitch", controller -> pose.headPitch());

        MathParser.setVariable("head_yaw", controller -> -pose.relativeHeadYaw());
        MathParser.setVariable("head_pitch", controller -> pose.headPitch());
        MathParser.setVariable("ysm.body_yaw", controller -> pose.bodyYaw());

        MathParser.setVariable("ysm.input_vertical", controller -> pose.inputVertical());
        MathParser.setVariable("ysm.input_horizontal", controller -> pose.inputHorizontal());
        MathParser.setVariable("ysm.elytra_rot_z", controller -> pose.elytraRoll());
        MathParser.setVariable("ysm.food_level", controller -> pose.foodLevel());
        MathParser.setVariable("ysm.has_mainhand", controller -> pose.hasMainHand() ? 1.0 : 0.0);
        MathParser.setVariable("ysm.has_offhand", controller -> pose.hasOffhand() ? 1.0 : 0.0);
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<GeoRenderState.Impl> renderPassInfo, float widthScale, float heightScale) {
        YsmScaleProfile scaleProfile = this.pack().scaleProfile();
        super.scaleModelForRender(
            renderPassInfo,
            scaleProfile.scale() * widthScale,
            scaleProfile.scale() * heightScale
        );
    }

    @Override
    public void preRenderPass(RenderPassInfo<GeoRenderState.Impl> renderPassInfo, OrderedRenderCommandQueue renderTasks) {
        renderPassInfo.poseStack().translate(0.0F, this.pack().scaleProfile().worldTranslateY(), 0.0F);
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<GeoRenderState.Impl> renderPassInfo) {
        float bodyYaw = renderPassInfo.renderState().getOrDefaultGeckolibData(BODY_YAW, 0.0F);
        renderPassInfo.poseStack().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - bodyYaw));

        float deathTime = renderPassInfo.renderState().getOrDefaultGeckolibData(DEATH_TIME, 0.0F);
        if (deathTime > 0.0F) {
            float deathRotation = Math.min(MathHelper.sqrt((deathTime - 1.0F) / 20.0F * 1.6F), 1.0F) * 90.0F;
            renderPassInfo.poseStack().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(deathRotation));
        }
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<GeoRenderState.Impl> renderPassInfo, BoneSnapshots snapshots) {
        YsmPoseSnapshot pose = renderPassInfo.renderState().getOrDefaultGeckolibData(POSE_SNAPSHOT, (YsmPoseSnapshot) null);
        if (pose == null) {
            return;
        }

        applyHeadTracking(snapshots, pose);

        if (YsmFirstPersonCompat.shouldUseFirstPersonModel()) {
            hideFirstPersonHead(snapshots);
        }
    }

    private static void applyHeadTracking(BoneSnapshots snapshots, YsmPoseSnapshot pose) {
        BoneSnapshot headSnapshot = firstPresent(snapshots, HEAD_ROTATION_BONES);
        if (headSnapshot == null) {
            return;
        }

        float yawRadians = (float) Math.toRadians(MathHelper.clamp(pose.relativeHeadYaw(), -75.0f, 75.0f)) * 0.85f;
        float pitchRadians = (float) Math.toRadians(MathHelper.clamp(pose.headPitch(), -60.0f, 60.0f)) * 0.85f;
        headSnapshot.setRotY(headSnapshot.getRotY() - yawRadians);
        headSnapshot.setRotX(headSnapshot.getRotX() - pitchRadians);
    }

    private void hideFirstPersonHead(BoneSnapshots snapshots) {
        for (String boneName : this.pack().firstPersonHiddenBones()) {
            snapshots.ifPresent(boneName, snapshot -> snapshot.skipRender(true).skipChildrenRender(true));
        }
    }

    private static BoneSnapshot firstPresent(BoneSnapshots snapshots, String[] candidates) {
        for (String candidate : candidates) {
            var snapshot = snapshots.get(candidate);
            if (snapshot.isPresent()) {
                return snapshot.get();
            }
        }

        return null;
    }
}
