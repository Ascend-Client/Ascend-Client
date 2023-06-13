package io.github.betterclient.client.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class MarkCommand {
    public static List<String> markedPlayers = new ArrayList<>();

    public static void create(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder
                        .<ServerCommandSource>literal("mark")
                        .then(
                                RequiredArgumentBuilder
                                        .<ServerCommandSource, String>argument("player", StringArgumentType.string())
                                        .executes(context -> {
                                            String playerName = StringArgumentType.getString(context, "player");

                                            return execute(playerName);
                                        })
                        )
        );
    }

    private static int execute(String playerName) {
        if(markedPlayers.contains(playerName)) {
            markedPlayers.remove(playerName);
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§cRemoved mark of player: §6" + playerName));
        } else {
            markedPlayers.add(playerName);
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§9Marked: §6" + playerName));
        }

        return 0;
    }
}
