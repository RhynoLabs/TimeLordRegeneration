package dev.amble.timelordregen.mixin.ait;

import dev.amble.ait.client.renderers.SonicRendering;
import dev.amble.ait.client.renderers.entities.ControlEntityRenderer;
import dev.amble.ait.core.entities.ConsoleControlEntity;
import dev.amble.timelordregen.api.RegenerationCapable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ControlEntityRenderer.class)
public class ControlEntityRendererMixin {
	@Inject(method="isScanningSonicInConsole", at=@At("HEAD"), cancellable = true, remap=false)
	private static void regen$showFlightEvents(ConsoleControlEntity entity, CallbackInfoReturnable<Boolean> cir) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (!(player instanceof RegenerationCapable capable)) return;

		capable.withInfo().ifPresent(info -> {
			if (info.getUsesLeft() > 0) {
				cir.setReturnValue(true);
			}
		});
	}

	@Inject(method="isPlayerLookingAtControlWithSonic", at=@At("HEAD"), cancellable = true, remap=false)
	private static void regen$showControlNames(HitResult hitResult, ConsoleControlEntity entity, CallbackInfoReturnable<Boolean> cir) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (!(player instanceof RegenerationCapable capable)) return;

		capable.withInfo().ifPresent(info -> {
			if (!(hitResult instanceof EntityHitResult entityHit)) {
				cir.setReturnValue(false);
				return;
			}

			Entity hitEntity = entityHit.getEntity();

			if (hitEntity == null) {
				cir.setReturnValue(false);
				return;
			}
			cir.setReturnValue(hitEntity.equals(entity) && info.getUsesLeft() > 0);
		});
	}
}
