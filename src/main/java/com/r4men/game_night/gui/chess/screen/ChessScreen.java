package com.r4men.game_night.gui.chess.screen;

import com.r4men.game_night.gui.chess.menu.ChessMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ChessScreen extends AbstractContainerScreen<ChessMenu> {
    public ChessScreen(ChessMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.titleLabelX = -10000;
        this.inventoryLabelX = -10000;
    }

    protected void renderBg(GuiGraphicsExtractor graphics, float partialTick, int mouseX, int mouseY) {
        // Background rendering intentionally minimal while migrating to the new GUI API.
    }
}

