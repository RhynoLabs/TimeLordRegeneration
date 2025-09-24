package dev.amble.timelordregen.api;

import dev.amble.timelordregen.animation.AnimationSet;
import dev.amble.timelordregen.animation.AnimationTemplate;
import dev.amble.timelordregen.animation.RegenAnimRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.lib.animation.AnimatedEntity;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class RegenerationInfo {
	public static void init() {
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			List<Entity> entities = new ArrayList<>(server.getPlayerManager().getPlayerList());
			server.getWorlds().forEach(world -> world.iterateEntities().forEach(entities::add));
			entities.forEach(entity -> {
				if (!(entity instanceof RegenerationCapable regen) || !(entity instanceof LivingEntity living)) return;

				RegenerationInfo info = regen.getRegenerationInfo();
				if (info != null && info.isRegenerating()) {
					info.finish(living);
				}
			});
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ServerPlayerEntity entity = handler.getPlayer();
			if (!(entity instanceof RegenerationCapable regen)) return;

			RegenerationInfo info = regen.getRegenerationInfo();
			if (info != null && info.isRegenerating()) {
				info.setRegenQueued(true);
				info.setRegenerating(false);
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity entity = handler.getPlayer();
			if (!(entity instanceof RegenerationCapable regen)) return;

			RegenerationInfo info = regen.getRegenerationInfo();
			if (info != null && info.isRegenQueued()) {
				info.tryStart(entity);
			}
		});

		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
			RegenerationInfo info = RegenerationInfo.get(entity);

			if (info == null) return true;

			return !info.tryStart(entity) && !info.isRegenerating();
		});
	}

	public static final Codec<RegenerationInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("usesLeft").forGetter(RegenerationInfo::getUsesLeft),
			Codec.BOOL.fieldOf("isRegenerating").forGetter(RegenerationInfo::isRegenerating),
			Codec.BOOL.fieldOf("queued_regen").forGetter(RegenerationInfo::isRegenQueued),
			Identifier.CODEC.fieldOf("animation").forGetter(RegenerationInfo::getAnimationId)
	).apply(instance, RegenerationInfo::new));

	public static final int MAX_REGENERATIONS = 12;

	@Getter
	private int usesLeft;
	@Getter @Setter
	private boolean isRegenerating;
	@Getter @Setter
	private AnimationTemplate animation;
	@Getter @Setter
	private boolean regenQueued; // for when a player leaves and rejoins while regenerating

	private RegenerationInfo(int usesLeft, boolean isRegenerating, boolean regenQueued, Identifier animation) {
		this.usesLeft = usesLeft;
		this.isRegenerating = isRegenerating;
		this.regenQueued = regenQueued;
		this.animation = RegenAnimRegistry.getInstance().getOrFallback(animation);
	}

	/**
	 * Default constructor for creating a new RegenerationInfo
	 */
	public RegenerationInfo() {
		this(MAX_REGENERATIONS, false, false, RegenAnimRegistry.getInstance().getRandom().id());
	}

	public void setUsesLeft(int usesLeft) {
		this.usesLeft = MathHelper.clamp(usesLeft, 0, MAX_REGENERATIONS);
	}

	public void decrement() {
		this.setUsesLeft(this.getUsesLeft() - 1);
	}

	public boolean tryStart(LivingEntity entity) {
		if (this.isRegenerating() || this.usesLeft <= 0) return false;

		this.setRegenQueued(false);
		this.decrement();
		this.setRegenerating(true);

		entity.setHealth(entity.getMaxHealth());
		entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS);

		if (entity instanceof AnimatedEntity animated) {
			AnimationSet set = this.animation.instantiate(true); // todo config option for skin change
			set.finish(() -> this.finish(entity));
			set.start(animated);

			for (AnimationTemplate.Stage stage : AnimationTemplate.Stage.values()) {
				set.callback(stage, s -> {
					RegenerationEvents.CHANGE_STAGE.invoker().onStateChange(entity, this, s);
				});
			}
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
		if (!(entity instanceof RegenerationCapable capability)) return null;

		return capability.getRegenerationInfo();
	}
}
