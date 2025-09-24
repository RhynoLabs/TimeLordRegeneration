package com.amble.timelordregen.mixin;

import com.amble.timelordregen.api.RegenerationCapable;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements RegenerationCapable {
}
