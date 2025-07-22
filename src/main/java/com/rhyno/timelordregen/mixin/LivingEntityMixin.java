package com.rhyno.timelordregen.mixin;

import com.rhyno.timelordregen.regeneration.RegenerationManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof PlayerEntity player) {
            if (player.getHealth() - amount <= 0.0F && RegenerationManager.canRegenerate(player)) {
                RegenerationManager.triggerRegeneration(player);
                cir.setReturnValue(false); // Cancel the fatal damage
            }

            if (RegenerationManager.isRegenerating(player)) {
                cir.setReturnValue(false); // Cancel all damage while regenerating
            }
        }
    }
}