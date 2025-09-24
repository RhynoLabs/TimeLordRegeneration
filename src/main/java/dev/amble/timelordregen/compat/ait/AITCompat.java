package dev.amble.timelordregen.compat.ait;

import dev.amble.timelordregen.RegenerationMod;
import dev.amble.timelordregen.animation.AnimationTemplate;
import dev.amble.timelordregen.api.RegenerationEvents;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.registry.impl.DesktopRegistry;
import net.minecraft.text.Text;

public class AITCompat {
	public static void init() {
		RegenerationMod.LOGGER.info("AIT detected, loading compatibility features.");


		RegenerationEvents.START.register(((entity, data) -> {
			if (!TardisServerWorld.isTardisDimension(entity.getWorld())) return;

			ServerTardis tardis = ((TardisServerWorld) (entity.getWorld())).getTardis();
			if (tardis == null) return;

			tardis.alarm().enable(Text.literal(entity.getEntityName() + " is regenerating!"));
		}));

		RegenerationEvents.CHANGE_STAGE.register(((entity, data, stage) -> {
			if (!TardisServerWorld.isTardisDimension(entity.getWorld())) return;

			ServerTardis tardis = ((TardisServerWorld) (entity.getWorld())).getTardis();
			if (tardis == null) return;

			tardis.alarm().enable(Text.literal(entity.getEntityName() + " is regenerating!"));

			if (stage == AnimationTemplate.Stage.LOOP && tardis.travel().inFlight()) {
				tardis.travel().crash();
			}
		}));

		RegenerationEvents.FINISH.register((entity, data) -> {;
			if (!TardisServerWorld.isTardisDimension(entity.getWorld())) return;

			ServerTardis tardis = ((TardisServerWorld) (entity.getWorld())).getTardis();
			if (tardis == null) return;

			tardis.interiorChangingHandler().queueInteriorChange(DesktopRegistry.getInstance().getRandom(tardis));
		});
	}
}
