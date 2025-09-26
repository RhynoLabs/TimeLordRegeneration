package dev.amble.timelordregen.mixin.client;

import dev.amble.lib.animation.client.AnimatedEntityModel;
import dev.amble.timelordregen.client.util.ClientParticleUtil;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public class PlayerEntityModelMixin<T extends LivingEntity> {

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("HEAD"))
    private void regeneration$setAngles(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo callbackInfo) {
        World world = livingEntity.getWorld();
        if (world instanceof ClientWorld clientWorld) {
	        AnimatedEntityModel thisModel = (AnimatedEntityModel) (Object) this;
            ClientParticleUtil.spawnParticles(thisModel, clientWorld, livingEntity, h - livingEntity.age);
        }
    }
}
