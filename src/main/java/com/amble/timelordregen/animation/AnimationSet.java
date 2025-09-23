package com.amble.timelordregen.animation;

import dev.amble.lib.animation.AnimatedEntity;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Consumer;

public class AnimationSet {
	private final AnimationTemplate template;
	private final EnumMap<AnimationTemplate.Stage, List<Consumer<AnimationTemplate.Stage>>> callbacks;
	private final List<Runnable> finishCallbacks;
	private AnimationTemplate.Stage current;
	private boolean cancelled = false;
	@Getter
	@Nullable private AnimatedEntity target;

	public AnimationSet(AnimationTemplate template) {
		this.template = template;
		this.callbacks = new EnumMap<>(AnimationTemplate.Stage.class);
		this.finishCallbacks = new ArrayList<>();
		this.current = AnimationTemplate.Stage.START;
	}

	private AnimationTemplate.ReferenceWrapper get(AnimationTemplate.Stage stage) {
		return this.template.get(stage);
	}

	public void callback(AnimationTemplate.Stage stage, Consumer<AnimationTemplate.Stage> callback) {
		this.callbacks.computeIfAbsent(stage, s -> new ArrayList<>()).add(callback);
	}

	public void finish(Runnable callback) {
		this.finishCallbacks.add(callback);
	}

	private void runCallbacks(AnimationTemplate.Stage stage) {
		List<Consumer<AnimationTemplate.Stage>> cbs = this.callbacks.get(stage);
		if (cbs != null) {
			for (Consumer<AnimationTemplate.Stage> cb : cbs) {
				cb.accept(stage);
			}
		}
	}

	private void setStage(AnimationTemplate.Stage stage) {
		this.current = stage;
		this.runCallbacks(stage);
	}

	@Nullable
	private AnimationTemplate.ReferenceWrapper next() {
		AnimationTemplate.Stage next = this.current.next();
		if (next != null) {
			this.setStage(next);
			return this.get(next);
		} else {
			this.current = null;
		}
		return null;
	}

	public void start(AnimatedEntity target) {
		this.setStage(AnimationTemplate.Stage.START);

		this.target = target;

		this.runStage(this.current);
	}

	private void runStage(AnimationTemplate.Stage stage) {
		if (this.cancelled || this.target == null) return;

		if (stage == null) {
			for (Runnable cb : this.finishCallbacks) {
				cb.run();
			}
			return;
		}

		AnimationTemplate.ReferenceWrapper wrapper = this.get(stage);
		if (wrapper == null) return;

		target.playAnimation(wrapper.reference());
		Scheduler.get().runTaskLater(() -> {
			this.next();
			this.runStage(this.current);
		}, TaskStage.END_SERVER_TICK, wrapper.unit(), wrapper.duration());
	}

	public void cancel() {
		this.cancelled = true;
	}
}
