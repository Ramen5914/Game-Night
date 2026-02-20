package com.r4men.game_night.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.r4men.game_night.gui.menu.ChessMenu;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

public class ChessCommand {
    public ChessCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("gameNight").then(Commands.literal("chess").executes(this::execute)));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();

        assert player != null;
        player.openMenu(new SimpleMenuProvider((containerId, playerInventory, player1) -> new ChessMenu(containerId, null), Component.literal("Chess")));
        return 1;
    }
}
