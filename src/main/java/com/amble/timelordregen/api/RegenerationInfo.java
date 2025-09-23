package com.amble.timelordregen.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.amble.timelordregen.data.Attachments;
import dev.amble.lib.skin.SkinData;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class RegenerationInfo {
	public static final Codec<RegenerationInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("usesLeft").forGetter(RegenerationInfo::getUsesLeft),
			Codec.BOOL.fieldOf("isRegenerating").forGetter(RegenerationInfo::isRegenerating)
	).apply(instance, RegenerationInfo::new));

	public static final int MAX_REGENERATIONS = 12;
	private static final int DURATION = 200; // TODO use animation library stuff instead of hardcode

	@Getter
	private int usesLeft;
	@Getter @Setter
	private boolean isRegenerating;

	private RegenerationInfo(int usesLeft, boolean isRegenerating) {
		this.usesLeft = usesLeft;
		this.isRegenerating = isRegenerating;
	}

	/**
	 * Default constructor for creating a new RegenerationInfo
	 */
	public RegenerationInfo() {
		this(MAX_REGENERATIONS, false);
	}

	public void decrement() {
		if (usesLeft > 0) {
			usesLeft--;
		}
	}

	public boolean tryStart(LivingEntity entity) {
		if (this.isRegenerating() || this.usesLeft <= 0) return false;

		this.decrement();
		this.setRegenerating(true);

		entity.setHealth(entity.getMaxHealth());
		entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS);

		RegenerationEvents.START.invoker().onStart(entity, this);
		Scheduler.get().runTaskLater(() -> finish(entity), TaskStage.END_SERVER_TICK, TimeUnit.TICKS, DURATION);

		return true;
	}

	private void finish(LivingEntity entity) {
		this.setRegenerating(false);
		RegenerationEvents.FINISH.invoker().onFinish(entity, this);

		// TODO make better this is temporary for testing purposes
		if (entity instanceof ServerPlayerEntity player) {
			String[] usernames = new String[]{"duzo", "portal3i", "winndi", "loqor"};
			SkinData.username((usernames[(int) (Math.random() * usernames.length)]), false).upload(player);
		}
	}

	public static RegenerationInfo get(LivingEntity entity) {
		return entity.getAttachedOrCreate(Attachments.REGENERATION);
	}

	public int getUsesLeft() {
		return this.usesLeft;
	}

	public boolean isRegenerating() {
		return this.isRegenerating;
	}

	public void setRegenerating(boolean regenerating) {
		this.isRegenerating = regenerating;
	}
}
