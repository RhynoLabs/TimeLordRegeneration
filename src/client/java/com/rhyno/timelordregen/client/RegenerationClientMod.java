package com.rhyno.timelordregen.client;

import com.rhyno.timelordregen.RegenerationMod;
import com.rhyno.timelordregen.client.animations.RegenerationAnimations;
import com.rhyno.timelordregen.client.gui.RegenerationSettingsScreen;
import com.rhyno.timelordregen.network.Networking;
import mc.duzo.animation.player.holder.PlayerAnimationHolder;
import mc.duzo.animation.registry.AnimationRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class RegenerationClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        Animations.init();

        ClientPlayNetworking.registerGlobalReceiver(Networking.OPEN_GUI_PACKET, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                if (client.player != null) {
                    client.setScreen(new RegenerationSettingsScreen(client.player));
                }
            });
        });
    }

    public static class Animations {

        public static class Players {
            //public static final Supplier<PlayerAnimationHolder> REGENERATE = AnimationRegistry.instance().register(() -> new PlayerAnimationHolder(new Identifier(RegenerationMod.MOD_ID, "regeneration"), RegenerationAnimations.CASE_OPEN_PLAYER));

            public static void init() {}

        }

        public static void init() {
            Players.init();
        }

    }
}
