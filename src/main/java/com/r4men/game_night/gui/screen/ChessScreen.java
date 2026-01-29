package com.r4men.game_night.gui.screen;

import com.r4men.game_night.gui.menu.ChessMenu;
import com.r4men.game_night.util.GNUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ChessScreen extends AbstractContainerScreen<ChessMenu> {
    public ChessScreen(ChessMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth = 8 * 16;
        this.imageHeight = 8 * 16;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        var colors = GNUtil.getChessBoardColors();
        int white = colors.getA();
        int black = colors.getB();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int minX = x + (j * 16);
                int minY = y + (i * 16);
                int maxX = minX + 16;
                int maxY = minY + 16;

                guiGraphics.fill(minX, minY, maxX, maxY, (i+j) % 2 == 0 ? white : black);
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
