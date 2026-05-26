package com.r4men.game_night.gui.chess.screen;

import com.r4men.game_night.gui.chess.menu.ChessSetupMenu;
import com.r4men.game_night.gui.chess.menu.ChessMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ChessSetupScreen extends AbstractContainerScreen<ChessSetupMenu> {
    private Button startButton;
    private int x;
    private int y;

    public ChessSetupScreen(ChessSetupMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.titleLabelX = -10000;
        this.inventoryLabelX = -10000;
    }

    @Override
    protected void init() {
        super.init();
        x = this.leftPos;
        y = this.topPos;

        this.startButton = this.addRenderableWidget(
                Button.builder(Component.translatable("game_night.games.chess.setup.start"), button -> {
                    onStartButtonPressed();
                }).bounds(x+20, y+20, 80, 30).build()
        );
    }

    protected void renderBg(GuiGraphicsExtractor guiGraphics, float v, int i, int i1) {
        guiGraphics.fill(x, y, x+250, y+250, 0xFFFFFFFF);
    }


    private void onStartButtonPressed() {
        // Mark the block entity as setup so the block will open the normal chess menu
        menu.setSetup(true);

        // Close this setup screen first
        onClose();

        // Try to open the full ChessScreen client-side using the same block entity
        var be = menu.getBlockEntity();
        try {
            if (be != null && minecraft.player != null) {
                // Create a ChessMenu tied to the block entity (use 0 as a local container id)
                ChessMenu chessMenu = new ChessMenu(0, be);
                minecraft.setScreen(new ChessScreen(chessMenu, minecraft.player.getInventory(), Component.translatable("game_night.games.chess.title")));
                return;
            }
        } catch (Exception e) {
            // Fall through to a safe fallback if something goes wrong
        }

        // Fallback: if we couldn't construct the ChessScreen, ask the player to re-open the block menu provider
        if (minecraft.player != null && be != null) minecraft.player.openMenu(be);
    }
}
