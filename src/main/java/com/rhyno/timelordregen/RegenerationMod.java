package com.rhyno.timelordregen;

import com.rhyno.timelordregen.commands.RegenCommand;
import com.rhyno.timelordregen.events.PlayerDeathHandler;
import com.rhyno.timelordregen.network.Networking;
import com.rhyno.timelordregen.regeneration.RegenerationManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

public class RegenerationMod implements ModInitializer {

    public static final String MOD_ID = "timelordregen";

    public static final Identifier REGEN_SOUND_ID = new Identifier(MOD_ID, "regeneration");
    public static final SoundEvent REGEN_SOUND = Registry.register(
            Registries.SOUND_EVENT,
            REGEN_SOUND_ID,
            SoundEvent.of(REGEN_SOUND_ID)
    );

    @Override
    public void onInitialize() {
        // Register player death handler
        PlayerDeathHandler.register();

        Networking.register();

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            RegenCommand.register(dispatcher);
        });

        // Init regeneration manager
    }
}