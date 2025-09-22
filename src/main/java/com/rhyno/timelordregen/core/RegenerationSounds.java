package com.rhyno.timelordregen.core;

import com.rhyno.timelordregen.RegenerationMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class RegenerationSounds {

    //Register Sounds here

    private static SoundEvent register(String name) {
        Identifier id = RegenerationMod.id(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
    public static void init() {
    }
}
