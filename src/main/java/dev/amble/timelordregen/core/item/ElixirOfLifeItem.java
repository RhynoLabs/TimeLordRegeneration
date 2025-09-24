package dev.amble.timelordregen.core.item;

import dev.amble.timelordregen.api.RegenerationCapable;
import dev.amble.timelordregen.api.RegenerationInfo;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class ElixirOfLifeItem extends Item {
	public ElixirOfLifeItem(Settings settings) {
		super(settings.food(new FoodComponent.Builder().alwaysEdible().build()));
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (!world.isClient() && user instanceof RegenerationCapable capability) {
			capability.getRegenerationInfo().setUsesLeft(RegenerationInfo.MAX_REGENERATIONS);

			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_TOTEM_USE, user.getSoundCategory(), 1.0F, 1.0F);
		}

		return super.finishUsing(stack, world, user);
	}

	@Override
	public SoundEvent getEatSound() {
		return SoundEvents.ENTITY_GENERIC_DRINK;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}
}
