package com.amble.timelordregen.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.amble.timelordregen.api.RegenerationInfo;
import com.amble.timelordregen.network.Networking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;


// Add this import for literal() if needed:
import static net.minecraft.server.command.CommandManager.literal;

public class RegenCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Register /regen command
        dispatcher.register(literal(Text.translatable("command.regen.name").getString())
                .requires(source -> source.hasPermissionLevel(0)) // Allow all players
                .executes(context -> {
                    var player = context.getSource().getPlayer();
                    if (player == null) return 0;

	                RegenerationInfo info = RegenerationInfo.get(player);
	                if (info == null) {
		                context.getSource().sendError(Text.translatable("command.regen.data.error"));
		                return 0;
	                }

                    if (info.tryStart(player)) {
                        context.getSource().sendFeedback(() -> Text.translatable("command.regen.triggered"), false);
                    } else {
                        context.getSource().sendError(Text.translatable("command.regen.fail") );
                    }
                    return 1;
                })
        );

        // Register /regenui command
        dispatcher.register(literal(Text.translatable("command.regenui.name").getString())
                .requires(source -> source.hasPermissionLevel(0))
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player == null) return 0;
                    Networking.sendOpenGuiPacket(player);
                    return 1;
                })
        );
    }
}
