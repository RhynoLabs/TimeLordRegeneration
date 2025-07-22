package com.rhyno.timelordregen.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

public class Networking {
    public static final Identifier OPEN_GUI_PACKET_ID = new Identifier("timelordregen", "open_regen_gui");
    public static final Identifier OPEN_GUI_PACKET = new Identifier("timelordregen", "open_gui");

    public static void register() {
        // Register server-side packet receivers here if needed
    }

    public static void sendOpenGuiPacket(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, OPEN_GUI_PACKET_ID, PacketByteBufs.create());
    }
}