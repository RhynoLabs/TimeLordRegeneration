package com.rhyno.timelordregen.client;

import com.rhyno.timelordregen.client.gui.RegenerationSettingsScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class ClientNetworking {
    public static final Identifier OPEN_GUI_PACKET = new Identifier("timelordregen", "open_gui");

    public static void registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(OPEN_GUI_PACKET, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                MinecraftClient.getInstance().setScreen(new RegenerationSettingsScreen(MinecraftClient.getInstance().player));
            });
        });
    }
}