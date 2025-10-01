package dev.amble.timelordregen.core.item;

import dev.amble.timelordregen.api.RegenerationInfo;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.client.ClientScheduler;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PocketWatchItem extends Item {
	private static final int COOLDOWN = 100; // 5 secs
	private static final String OWNER_KEY = "MarkedOwner";
	private static final String OPENED_KEY = "IsOpened";

	public PocketWatchItem(Settings settings) {
		super(settings.maxCount(1).maxDamage(RegenerationInfo.MAX_REGENERATIONS));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		user.getItemCooldownManager().set(this, COOLDOWN);
		setOpened(stack, true);

		if (!world.isClient()) {
			Scheduler.get().runTaskLater(() -> setOpened(stack, false), TaskStage.END_SERVER_TICK, TimeUnit.TICKS, COOLDOWN);

			UUID ownerId = getOwner(user.getStackInHand(hand));
			RegenerationInfo info = RegenerationInfo.get(user);
			if (info == null || ownerId != null && !ownerId.equals(user.getUuid())) {
				world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_WITHER_SPAWN, user.getSoundCategory(), 1.0F, 1.0F);

				return TypedActionResult.fail(user.getStackInHand(hand));
			}

			markOwner(stack, user);

			// get damage value (number of uses left)
			int damage = stack.getMaxDamage() - stack.getDamage();
			int usesLeft = info.getUsesLeft();

			boolean intoUser = damage > 0 && usesLeft < RegenerationInfo.MAX_REGENERATIONS;

			if (intoUser) {
				int transferable = Math.min(damage, RegenerationInfo.MAX_REGENERATIONS - usesLeft);
				usesLeft += transferable;
				damage -= transferable;
			} else {
				int transferable = Math.min(usesLeft, RegenerationInfo.MAX_REGENERATIONS - damage);
				damage += transferable;
				usesLeft -= transferable;
			}

			info.setUsesLeft(usesLeft);
			stack.setDamage(stack.getMaxDamage() - damage);

			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_TOTEM_USE, user.getSoundCategory(), 1.0F, 1.0F);
			return TypedActionResult.success(user.getStackInHand(hand), false);
		} else {
			ClientScheduler.get().runTaskLater(() -> setOpened(stack, false), TimeUnit.TICKS, COOLDOWN);
		}

		return super.use(world, user, hand);
	}

	private static void markOwner(ItemStack stack, PlayerEntity player) {
		stack.getOrCreateNbt().putUuid(OWNER_KEY, player.getUuid());
	}

	@Nullable
	private static UUID getOwner(ItemStack stack) {
		if (stack.getNbt() != null && stack.getNbt().contains(OWNER_KEY)) {
			return stack.getNbt().getUuid(OWNER_KEY);
		}
		return null;
	}

	public static boolean isOpened(ItemStack stack) {
		return stack.getOrCreateNbt().getBoolean(OPENED_KEY);
	}

	private static void setOpened(ItemStack stack, boolean opened) {
		stack.getOrCreateNbt().putBoolean(OPENED_KEY, opened);
	}
}
