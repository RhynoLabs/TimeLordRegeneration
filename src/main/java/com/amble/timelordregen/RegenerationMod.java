package com.amble.timelordregen;

import com.amble.timelordregen.commands.RegenCommand;
import com.amble.timelordregen.core.RegenerationModBlocks;
import com.amble.timelordregen.core.RegenerationModItemGroups;
import com.amble.timelordregen.core.RegenerationModItems;
import com.amble.timelordregen.data.Attachments;
import com.amble.timelordregen.api.RegenerationInfo;
import com.amble.timelordregen.network.Networking;
import dev.amble.lib.container.RegistryContainer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.util.Identifier;
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
        RegistryContainer.register(RegenerationModItemGroups.class, MOD_ID);
        RegistryContainer.register(RegenerationModBlocks.class, MOD_ID);
        RegistryContainer.register(RegenerationModItems.class, MOD_ID);

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            RegenCommand.register(dispatcher);
        });

        // Init regeneration manager

	    ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
		    RegenerationInfo info = RegenerationInfo.get(entity);

			if (info == null) return true;

			return !info.tryStart(entity);
	    });
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}