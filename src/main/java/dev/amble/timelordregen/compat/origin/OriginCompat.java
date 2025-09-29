package dev.amble.timelordregen.compat.origin;

import dev.amble.timelordregen.RegenerationMod;
import dev.amble.timelordregen.api.RegenerationEvents;
import dev.amble.timelordregen.api.RegenerationInfo;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class OriginCompat {
	private static OriginLayer DEFAULT_LAYER;
	private static final Identifier DEFAULT_LAYER_ID = Identifier.of(Origins.MODID, "origin");
	public static final Identifier REGEN_ORIGIN_ID = Identifier.of(RegenerationMod.MOD_ID, "regeneration");
	public static final int REGEN_ORIGIN_COUNT = 12; // How many regenerations the power should give by default

	public static void init() {
		RegenerationMod.LOGGER.info("Origins detected, loading compatibility features.");

		RegenerationEvents.TRANSITION.register((entity, data, stage) -> {
			if (!(entity instanceof ServerPlayerEntity player)) return;

			boolean isEmpty = data.getUsesLeft() == 0;
			boolean hasOrigin = hasRegenerationPower(player);

			if (isEmpty || !hasOrigin) {
				setOrigin(player, getRandomOrigin());
			}
		});
	}

	/**
	 * Chooses a random origin from the origin registry.
	 * @return the found origin
	 */
	public static Origin getRandomOrigin() {
		int index = RegenerationMod.RANDOM.nextInt(OriginRegistry.size());
		Identifier id = (Identifier) OriginRegistry.identifiers().toArray()[index];
		Origin found = OriginRegistry.get(id);
		return (found.equals(Origin.EMPTY) ? getRandomOrigin() : found);
	}

	public static Origin getOrigin(ServerPlayerEntity player) {
		return ModComponents.ORIGIN.get(player).getOrigin(getDefaultLayer());
	}

	public static void setOrigin(ServerPlayerEntity player, Origin origin) {
		OriginComponent component = ModComponents.ORIGIN.get(player);
		component.setOrigin(getDefaultLayer(), origin);
		component.sync();
	}


	public static OriginLayer getDefaultLayer() {
		if (DEFAULT_LAYER == null) {
			DEFAULT_LAYER = OriginLayers.getLayer(DEFAULT_LAYER_ID);
			if (DEFAULT_LAYER == null) {
				RegenerationMod.LOGGER.error("Default origin layer not found, Origins compatibility may not work correctly.");
			}
		}

		return DEFAULT_LAYER;
	}

	public static boolean hasRegenerationPower(ServerPlayerEntity player) {
		Origin component = getOrigin(player);
		if (component == null) return false;
		return component.hasPowerType(PowerTypeRegistry.get(REGEN_ORIGIN_ID));
	}


	public static void setupRegenerationPower(PlayerEntity player) {
		if (!(player instanceof ServerPlayerEntity) || !hasRegenerationPower((ServerPlayerEntity) player)) return;

		RegenerationMod.LOGGER.debug("Setting up regeneration power for player {}", player.getName().getString());

		RegenerationInfo info = RegenerationInfo.get(player);
		if (info == null) {
			RegenerationMod.LOGGER.error("Regeneration power not found for player {}", player.getName().getString());
			return;
		}

		info.setUsesLeft(REGEN_ORIGIN_COUNT);
	}
}
