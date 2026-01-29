package com.r4men.game_night.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.r4men.game_night.GameNight;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ReturnHomeCommand {
    public ReturnHomeCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("home").then(Commands.literal("return").executes(this::execute)));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        String key = GameNight.ID + ".homepos";

        ServerPlayer player = context.getSource().getPlayer();
//        boolean hasHomepos = player.getPersistentData().getIntArray(key).length != 0;
        boolean hasHomepos = player.getPersistentData().contains(key);

        if (hasHomepos) {
            int[] playerPos = player.getPersistentData().getIntArray(key);
            player.teleportTo(playerPos[0], playerPos[1], playerPos[2]);

            context.getSource().sendSuccess(() -> Component.literal("Player returned Home!"), false);
            return 1;
        } else {
            context.getSource().sendFailure(Component.literal("No home position has been set!"));
            return -1;
        }
    }
}
