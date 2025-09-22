package com.rhyno.timelordregen.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.rhyno.timelordregen.api.RegenerationInfo;
import com.rhyno.timelordregen.network.Networking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;


// Add this import for literal() if needed:
import static net.minecraft.server.command.CommandManager.literal;

public class RegenCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Register /regen command
        dispatcher.register(literal("regen")
                .requires(source -> source.hasPermissionLevel(0)) // Allow all players
                .executes(context -> {
                    var player = context.getSource().getPlayer();
                    if (player == null) return 0;

	                RegenerationInfo info = RegenerationInfo.get(player);
	                if (info == null) {
		                context.getSource().sendError(Text.literal("Regeneration data not found."));
		                return 0;
	                }

                    if (info.tryStart(player)) {
                        context.getSource().sendFeedback(() -> Text.literal("Regeneration triggered!"), false);
                    } else {
                        context.getSource().sendError(Text.literal("No regenerations left or already regenerating."));
                    }
                    return 1;
                })
        );

        // Register /regenui command
        dispatcher.register(literal("regenui")
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
