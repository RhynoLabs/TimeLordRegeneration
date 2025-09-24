package com.amble.timelordregen.client.mixin;

import com.amble.timelordregen.client.util.ClientParticleUtil;
import com.amble.timelordregen.core.particle_effects.RegenParticleEffect;
import dev.amble.lib.animation.AnimatedEntity;
import dev.amble.lib.client.bedrock.BedrockAnimation;
import dev.amble.lib.client.bedrock.BedrockAnimationReference;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PlayerEntityModel.class)
public class PlayerEntityModelMixin<T extends LivingEntity> {

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("HEAD"))
    private void regeneration$setAngles(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo callbackInfo) {
        World world = livingEntity.getWorld();
        if (world instanceof ClientWorld clientWorld) {
            ClientParticleUtil.spawnParticles(clientWorld, livingEntity);
        }
    }

}
