package com.rhyno.timelordregen;

import com.rhyno.timelordregen.commands.RegenCommand;
import com.rhyno.timelordregen.data.Attachments;
import com.rhyno.timelordregen.data.RegenerationInfo;
import com.rhyno.timelordregen.network.Networking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegenerationMod implements ModInitializer {

    public static final String MOD_ID = "timelordregen";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Random RANDOM = Random.create();

    /*public static final Identifier REGEN_SOUND_ID = new Identifier(MOD_ID, "regeneration");
    public static final SoundEvent REGEN_SOUND = Registry.register(
            Registries.SOUND_EVENT,
            REGEN_SOUND_ID,
            SoundEvent.of(REGEN_SOUND_ID)
    );*/

    @Override
    public void onInitialize() {
	    LOGGER.info("E Cineribus Resurgam.");

	    Attachments.init();
        Networking.register();

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            RegenCommand.register(dispatcher);
        });

        // Init regeneration manager

	    ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
		    RegenerationInfo info = RegenerationInfo.get(entity);
			return info == null || !info.isRegenerating();
	    });
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}