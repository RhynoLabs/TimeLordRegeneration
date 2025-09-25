package dev.amble.timelordregen.client.util;

import dev.amble.lib.animation.AnimatedEntity;
import dev.amble.lib.client.bedrock.BedrockAnimation;
import dev.amble.lib.client.bedrock.BedrockAnimationReference;
import dev.amble.lib.skin.client.SkinGrabber;
import dev.amble.timelordregen.core.particle_effects.RegenParticleEffect;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public class ClientParticleUtil {

    public static void spawnParticles(ClientWorld clientWorld, LivingEntity livingEntity) {

        AnimatedEntity animated = (AnimatedEntity) livingEntity;
        AnimationState state = animated.getAnimationState();
        if (state == null) return;

        BedrockAnimationReference ref = animated.getCurrentAnimation();
        if (ref == null) return;


        ref.get().ifPresent(bedrockAnimation -> {
            double timeRunning = bedrockAnimation.getRunningSeconds(state);
            if (timeRunning >= bedrockAnimation.animationLength) return;

            Map<String, BedrockAnimation.BoneTimeline> boneTimelines = bedrockAnimation.boneTimelines;
            Vec3d modelPosition = boneTimelines.get("head").position().resolve(timeRunning);
            Vec3d modelRotation = boneTimelines.get("head").rotation().resolve(timeRunning);


            // Convert model space position to world space
            Vec3d worldPosition = livingEntity.getPos()
                    .add(modelPosition.multiply(0.1, -0.1, -0.1).rotateY((float) -Math.toRadians(livingEntity.getYaw())));


            float lerpedValue = getLerpedValue(bedrockAnimation, timeRunning);
            /*System.out.println(timeRunning);*/
            for (int x = 0; x < 1; x++) {
                clientWorld.addParticle(
                        new RegenParticleEffect(livingEntity.getId(), (float) modelRotation.getY(), -90 + (float) modelRotation.getX(), true, false, lerpedValue),
                        worldPosition.getX(), worldPosition.getY() + 1.5f, worldPosition.getZ(), 0.1, 0, 0.1
                );
            }
        });
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
