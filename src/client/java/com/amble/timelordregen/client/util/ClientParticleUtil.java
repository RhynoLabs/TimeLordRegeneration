package com.amble.timelordregen.client.util;

import com.amble.timelordregen.core.particle_effects.RegenParticleEffect;
import dev.amble.lib.animation.AnimatedEntity;
import dev.amble.lib.client.bedrock.BedrockAnimation;
import dev.amble.lib.client.bedrock.BedrockAnimationReference;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
            Map<String, BedrockAnimation.BoneTimeline> boneTimelines = bedrockAnimation.boneTimelines;
            Vec3d modelPosition = boneTimelines.get("left_arm").position().resolve(timeRunning);
            Vec3d modelRotation = boneTimelines.get("left_arm").rotation().resolve(timeRunning);

            // Convert model space position to world space
            Vec3d worldPosition = livingEntity.getPos()
                    .add(modelPosition).multiply(1, 1, 1);

            //System.out.println(modelPosition);

            for (int x = 0; x < 1; x++) {
                /*clientWorld.addParticle(new RegenParticleEffect(livingEntity.getId(), (float) modelRotation.getY(), (float) modelRotation.getX(), true, false),
                        worldPosition.getX(), worldPosition.getY() + 1.5f, worldPosition.getZ(), 0, 0, 0);*/
                /*clientWorld.addParticle(ParticleTypes.FLAME,
                        worldPosition.getX(), worldPosition.getY() + 1.5f, worldPosition.getZ(), 0, 0.1f, 0);*/
            }
        });
    }

}
