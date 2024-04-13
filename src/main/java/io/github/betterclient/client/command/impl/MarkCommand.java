package io.github.betterclient.client.command.impl;

import io.github.betterclient.client.bridge.IBridge.*;

import java.util.ArrayList;
import java.util.List;

public class MarkCommand implements CommandExecutor {
    public static List<String> markedPlayers = new ArrayList<>();

    public static void create(CommandDispatcher dispatcher) {
        dispatcher.register("mark", new MarkCommand(), new CommandArgument("PlayerName", String.class));
    }

    @Override
    public int execute(List<?> arguments) {
        String playerName = (String) arguments.get(0);

        if(markedPlayers.contains(playerName)) {
            markedPlayers.remove(playerName);
            MinecraftClient.getInstance().addMessage(Text.literal("§cRemoved mark of player: §6" + playerName));
        } else {
            markedPlayers.add(playerName);
            MinecraftClient.getInstance().addMessage(Text.literal("§9Marked: §6" + playerName));
        }

        return 0;
    }
}
