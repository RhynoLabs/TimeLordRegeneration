package dev.amble.timelordregen.mixin;

import dev.amble.timelordregen.api.RegenerationCapable;
import dev.amble.timelordregen.api.RegenerationInfo;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method="swingHand(Lnet/minecraft/util/Hand;Z)V", at=@At("HEAD"))
	private void regeneration$swingHand(net.minecraft.util.Hand hand, boolean fromPacket, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
		if (this instanceof RegenerationCapable capable) {
			RegenerationInfo info = capable.getRegenerationInfo();
			if (info == null) return;
			info.getDelay().stopEvent();
		}
	}
}
