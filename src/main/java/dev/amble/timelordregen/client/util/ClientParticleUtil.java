package dev.amble.timelordregen.client.util;

import dev.amble.lib.animation.AnimatedEntity;
import dev.amble.lib.animation.client.AnimatedEntityModel;
import dev.amble.lib.client.bedrock.BedrockAnimation;
import dev.amble.lib.client.bedrock.BedrockAnimationReference;
import dev.amble.lib.skin.client.SkinGrabber;
import dev.amble.timelordregen.core.particle_effects.RegenParticleEffect;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public class ClientParticleUtil {

    public static void spawnParticles(AnimatedEntityModel model, ClientWorld clientWorld, LivingEntity livingEntity, float tickDelta) {

        AnimatedEntity animated = (AnimatedEntity) livingEntity;
        AnimationState state = animated.getAnimationState();
        if (state == null) return;

        BedrockAnimationReference ref = animated.getCurrentAnimation();
        if (ref == null) return;


        ref.get().ifPresent(bedrockAnimation -> {
            double timeRunning = bedrockAnimation.getRunningSeconds(state);
            if (timeRunning >= bedrockAnimation.animationLength) return;

			float bodyYaw = (livingEntity instanceof ClientPlayerEntity player ? MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw) : livingEntity.bodyYaw);

			animatePart(clientWorld, model, "right_arm", bedrockAnimation, timeRunning, livingEntity, bodyYaw);
			animatePart(clientWorld, model, "left_arm", bedrockAnimation, timeRunning, livingEntity, bodyYaw);
			animatePart(clientWorld, model, "head", bedrockAnimation, timeRunning, livingEntity, bodyYaw);
        });
    }

	private static void animatePart(ClientWorld clientWorld, AnimatedEntityModel model, String partName, BedrockAnimation bedrockAnimation, double timeRunning, LivingEntity livingEntity, float bodyYaw) {
		Map<String, BedrockAnimation.BoneTimeline> boneTimelines = bedrockAnimation.boneTimelines;
		Vec3d modelPosition = boneTimelines.get(partName).position().resolve(timeRunning);
		Vec3d modelRotation = boneTimelines.get(partName).rotation().resolve(timeRunning);
		ModelPart part = model.getChild(partName).get();
		Vec3d pivot = new Vec3d(part.getDefaultTransform().pivotX, part.getDefaultTransform().pivotY, part.getDefaultTransform().pivotZ);
		Vec3d rotation = new Vec3d(part.getDefaultTransform().pitch, part.getDefaultTransform().yaw, part.getDefaultTransform().roll);
		modelRotation = modelRotation.add(rotation);

		final Vec3d[] furthest = {Vec3d.ZERO};

		part.forEachCuboid(new MatrixStack(), (MatrixStack.Entry matrix, String path, int index, ModelPart.Cuboid cuboid) -> {
			furthest[0] = furthest[0].add(cuboid.maxX - cuboid.minX, cuboid.maxY - cuboid.minY, cuboid.maxZ - cuboid.minZ);
		});

	    Vec3d worldPosition = modelPosition.add(pivot.multiply(1)).add(furthest[0].multiply(0)).rotateY((float) -Math.toRadians(modelRotation.y + bodyYaw)).multiply(-1/16F, -1/16F, -1/16F).add(livingEntity.getPos()).add(0, livingEntity.getStandingEyeHeight(), 0);

		float lerpedValue = getLerpedValue(bedrockAnimation, timeRunning);
		/*System.out.println(timeRunning);*/
		for (int x = 0; x < 1; x++) {
			clientWorld.addParticle(
					new RegenParticleEffect(livingEntity.getId(), (float) modelRotation.getY(), -90 + (float) modelRotation.getX(), true, false, lerpedValue),
					worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 0.1, 0, 0.1
			);
		}
	}

    private static float getLerpedValue(BedrockAnimation bedrockAnimation, double timeRunning) {
        float lerpedValue;
        String animName = bedrockAnimation.name.toLowerCase();
        double animLength = bedrockAnimation.animationLength;
        if (animName.contains("loop")) {
            lerpedValue = 0.4f;
        } else if (animName.contains("end")) {
            lerpedValue = 0f;
        } else {
            // Lerp from 0.1 to 0.4 over the animation duration
            float t = Math.max(0f, Math.min(1f, (float) (timeRunning / animLength)));
            lerpedValue = 0.01f + t * 0.3f;
        }
        return lerpedValue;
    }

}
