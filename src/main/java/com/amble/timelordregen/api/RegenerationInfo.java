package com.amble.timelordregen.api;

import com.amble.timelordregen.animation.AnimationSet;
import com.amble.timelordregen.animation.AnimationTemplate;
import com.amble.timelordregen.animation.RegenAnimRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.amble.timelordregen.data.Attachments;
import dev.amble.lib.animation.AnimatedEntity;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class RegenerationInfo {
	public static final Codec<RegenerationInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("usesLeft").forGetter(RegenerationInfo::getUsesLeft),
			Codec.BOOL.fieldOf("isRegenerating").forGetter(RegenerationInfo::isRegenerating),
			Identifier.CODEC.fieldOf("animation").forGetter(RegenerationInfo::getAnimationId)
	).apply(instance, RegenerationInfo::new));

	public static final int MAX_REGENERATIONS = 12;

	@Getter
	private int usesLeft;
	@Getter @Setter
	private boolean isRegenerating;
	@Getter @Setter
	private AnimationTemplate animation;

	private RegenerationInfo(int usesLeft, boolean isRegenerating, Identifier animation) {
		this.usesLeft = usesLeft;
		this.isRegenerating = isRegenerating;
		this.animation = RegenAnimRegistry.getInstance().getOrFallback(animation);
	}

	/**
	 * Default constructor for creating a new RegenerationInfo
	 */
	public RegenerationInfo() {
		this(MAX_REGENERATIONS, false, RegenAnimRegistry.getInstance().getRandom().id());
	}

	public void setUsesLeft(int usesLeft) {
		this.usesLeft = MathHelper.clamp(usesLeft, 0, MAX_REGENERATIONS);
	}

	public void decrement() {
		this.setUsesLeft(this.getUsesLeft() - 1);
	}

	public boolean tryStart(LivingEntity entity) {
		if (this.isRegenerating() || this.usesLeft <= 0) return false;

		this.decrement();
		this.setRegenerating(true);

		entity.setHealth(entity.getMaxHealth());
		entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS);

		if (entity instanceof AnimatedEntity animated) {
			AnimationSet set = this.animation.instantiate(true); // todo config option for skin change
			set.finish(() -> this.finish(entity));
			set.start(animated);
		} else {
			Scheduler.get().runTaskLater(() -> this.finish(entity), TaskStage.END_SERVER_TICK, TimeUnit.SECONDS, 5);
		}

		RegenerationEvents.START.invoker().onStart(entity, this);

		return true;
	}

	private void finish(LivingEntity entity) {
		this.setRegenerating(false);
		RegenerationEvents.FINISH.invoker().onFinish(entity, this);
	}

	private Identifier getAnimationId() {
		return this.animation.id();
	}

	public static RegenerationInfo get(LivingEntity entity) {
		return entity.getAttachedOrCreate(Attachments.REGENERATION);
	}
}
