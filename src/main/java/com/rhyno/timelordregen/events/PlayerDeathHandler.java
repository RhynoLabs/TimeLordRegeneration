package com.rhyno.timelordregen.events;

import com.rhyno.timelordregen.regeneration.RegenerationManager;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public class PlayerDeathHandler {
    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player == null || world.isClient) return ActionResult.PASS;

            float damage = 5.0f; // or some way to get the attack damage

            if (player.getHealth() - damage <= 0 && RegenerationManager.canRegenerate(player)) {
                RegenerationManager.triggerRegeneration(player);
                return ActionResult.FAIL; // Cancel the attack to prevent death
            }

            return ActionResult.PASS;
        });
    }
}

