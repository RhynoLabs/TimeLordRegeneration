package com.amble.timelordregen.mixin;

import com.amble.timelordregen.api.RegenerationInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import util.ParticleUtil;

@Mixin(Entity.class)
public class EntityMixin {

	@Unique
	@Final
	private static final ParticleUtil PARTICLE_UTIL = new ParticleUtil(true);

	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	private void regeneration$damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!(((Object) this) instanceof LivingEntity)) return;
		LivingEntity entity = (LivingEntity) (Object) this;

		RegenerationInfo info = RegenerationInfo.get(entity);
		if (info == null) return;

		boolean hurtAllowed = !info.isRegenerating();

		if (!hurtAllowed) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}

	@Inject(method = "tick()V", at = @At("TAIL"))
	private void regeneration$tick(CallbackInfo ci) {
		if (!(((Object) this) instanceof LivingEntity)) return;
		LivingEntity entity = (LivingEntity) (Object) this;

		if (entity == null) return;

		World world = entity.getWorld();

		if (world instanceof ServerWorld serverWorld) {
			PARTICLE_UTIL.spawnParticles(entity, serverWorld);
		}
    }

	@Inject(method = "move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"), cancellable = true)
	private void regeneration$move(MovementType movementType, Vec3d movement, CallbackInfo ci) {
		if (!(((Object) this) instanceof LivingEntity)) return;
		LivingEntity entity = (LivingEntity) (Object) this;

		RegenerationInfo info = RegenerationInfo.get(entity);
		if (info == null) return;

		boolean moveAllowed = !info.isRegenerating();

		if (!moveAllowed) {
			ci.cancel();
		}
	}
}

