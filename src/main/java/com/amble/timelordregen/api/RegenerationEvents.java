package com.amble.timelordregen.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;

public final class RegenerationEvents {
	public static final Event<Start> START = EventFactory.createArrayBacked(Start.class, callbacks -> (player, data) -> {
		for (Start callback : callbacks) {
			return callback.onStart(player, data);
		}

		return false;
	});
	public static final Event<Finish> FINISH = EventFactory.createArrayBacked(Finish.class, callbacks -> (player, data) -> {
		for (Finish callback : callbacks) {
			return callback.onFinish(player, data);
		}

		return false;
	});


	/**
	 * Called when a player starts to rebirth
	 */
	@FunctionalInterface
	public interface Start {
		boolean onStart(Entity player, RegenerationInfo data);
	}

	/**
	 * Called when a player has finished rebirth
	 */
	@FunctionalInterface
	public interface Finish { // ( Loqor couldnt. )
		boolean onFinish(Entity player, RegenerationInfo data);
	}
}
