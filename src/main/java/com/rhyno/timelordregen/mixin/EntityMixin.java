package com.rhyno.timelordregen.mixin;

import com.rhyno.timelordregen.api.RegenerationInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
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

	@Inject(method = "move", at = @At("HEAD"), cancellable = true)
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

