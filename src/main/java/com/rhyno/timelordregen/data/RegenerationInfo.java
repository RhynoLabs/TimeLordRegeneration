package com.rhyno.timelordregen.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rhyno.timelordregen.RegenerationMod;
import com.rhyno.timelordregen.api.RegenerationEvents;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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

	public boolean tryStart(Entity entity) {
		if (this.isRegenerating() || this.usesLeft <= 0) return false;

		this.decrement();
		this.setRegenerating(true);

		if (entity instanceof LivingEntity living) {
			living.setHealth(living.getMaxHealth());
		}

		entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS);

		RegenerationEvents.START.invoker().onStart(entity, this);
		Scheduler.get().runTaskLater(() -> finish(entity), TaskStage.END_SERVER_TICK, TimeUnit.TICKS, DURATION);

		return true;
	}

	private void finish(Entity entity) {
		this.setRegenerating(false);
		RegenerationEvents.FINISH.invoker().onFinish(entity, this);
	}

	public static RegenerationInfo get(Entity entity) {
		return entity.getAttachedOrCreate(Attachments.REGENERATION);
	}
}
