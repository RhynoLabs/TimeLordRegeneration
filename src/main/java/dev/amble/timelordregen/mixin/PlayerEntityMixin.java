package dev.amble.timelordregen.mixin;

import dev.amble.timelordregen.api.RegenerationCapable;
import dev.amble.timelordregen.api.RegenerationInfo;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements RegenerationCapable {
	@Inject(method="tick", at=@At("HEAD"))
	private void regeneration$tick(CallbackInfo ci) {
		this.tickRegeneration();
	}
}
