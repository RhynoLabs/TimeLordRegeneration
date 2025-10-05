package dev.amble.timelordregen.mixin;

import dev.amble.timelordregen.api.RegenerationCapable;
import dev.amble.timelordregen.api.RegenerationInfo;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method="damage", at=@At("HEAD"), cancellable=true)
	private void regeneration$damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (amount == Float.MAX_VALUE) return;

		if (this instanceof RegenerationCapable capable) {
			RegenerationInfo info = capable.getRegenerationInfo();
			if (info == null) return;
			if (info.isActive()) {
				cir.setReturnValue(false);
			}

			LivingEntity thisEntity = (LivingEntity)(Object)this;
			if (amount >= thisEntity.getHealth() && info.tryStart(thisEntity)) {
				cir.setReturnValue(false);
			}
		}
	}
}

