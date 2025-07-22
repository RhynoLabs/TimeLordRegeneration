package com.rhyno.timelordregen.client;

import com.rhyno.timelordregen.client.gui.RegenerationSettingsScreen;
import com.rhyno.timelordregen.network.Networking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class RegenerationClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(Networking.OPEN_GUI_PACKET, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                if (client.player != null) {
                    client.setScreen(new RegenerationSettingsScreen(client.player));
                }
            });
        });
    }
}
