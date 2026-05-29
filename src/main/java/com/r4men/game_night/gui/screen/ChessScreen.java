package com.r4men.game_night.gui.screen;

import com.r4men.game_night.GNConfig;
import com.r4men.game_night.GameNight;
import com.r4men.game_night.gui.menu.ChessMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class ChessScreen extends AbstractContainerScreen<ChessMenu> {
    private final Identifier BOARD = GameNight.getIdentifier("textures/gui/container/chess.png");

    private final Identifier BLACK_BISHOP = GameNight.getIdentifier("container/chess/black_bishop");
    private final Identifier BLACK_KING = GameNight.getIdentifier("container/chess/black_king");
    private final Identifier BLACK_KNIGHT = GameNight.getIdentifier("container/chess/black_knight");
    private final Identifier BLACK_PAWN = GameNight.getIdentifier("container/chess/black_pawn");
    private final Identifier BLACK_QUEEN = GameNight.getIdentifier("container/chess/black_queen");
    private final Identifier BLACK_ROOK = GameNight.getIdentifier("container/chess/black_rook");
    private final Identifier WHITE_BISHOP = GameNight.getIdentifier("container/chess/white_bishop");
    private final Identifier WHITE_KING = GameNight.getIdentifier("container/chess/white_king");
    private final Identifier WHITE_KNIGHT = GameNight.getIdentifier("container/chess/white_knight");
    private final Identifier WHITE_PAWN = GameNight.getIdentifier("container/chess/white_pawn");
    private final Identifier WHITE_QUEEN = GameNight.getIdentifier("container/chess/white_queen");
    private final Identifier WHITE_ROOK = GameNight.getIdentifier("container/chess/white_rook");

    private final Map<Character, Identifier> PIECE_SPRITES = Map.ofEntries(
            Map.entry('b', BLACK_BISHOP),
            Map.entry('k', BLACK_KING),
            Map.entry('n', BLACK_KNIGHT),
            Map.entry('p', BLACK_PAWN),
            Map.entry('q', BLACK_QUEEN),
            Map.entry('r', BLACK_ROOK),
            Map.entry('B', WHITE_BISHOP),
            Map.entry('K', WHITE_KING),
            Map.entry('N', WHITE_KNIGHT),
            Map.entry('P', WHITE_PAWN),
            Map.entry('Q', WHITE_QUEEN),
            Map.entry('R', WHITE_ROOK)
    );

    public ChessScreen(ChessMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 256, 256);

        this.titleLabelY = -1000000;
        this.inventoryLabelY = -1000000;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(BOARD, leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0, 1, 0, 1);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        String[] pieces = menu.getSimplifiedFen().split("/");

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Identifier piece = PIECE_SPRITES.get(pieces[y].charAt(x));

                if (piece != null) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, piece, leftPos + (imageWidth/8 * x), topPos + (imageHeight/8 * y), imageWidth/8, imageHeight/8);
                }
            }
        }
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);

        if (GNConfig.SHOW_COORDINATES.get()) {
            for (int x = 0; x < 8; x++) {
                int color;

                if (x % 2 == 0) {
                    color = 0xFFFFFFFF;
                } else {
                    color = 0xFF000000;
                }

                graphics.text(this.font, Character.toString(97 + x), 1 + (x * (this.imageWidth / 8)), this.imageHeight - this.font.lineHeight + 1, color, false);
            }

            for (int y = 0; y < 8; y++) {
                int color;

                if (y % 2 == 0) {
                    color = 0xFFFFFFFF;
                } else {
                    color = 0xFF000000;
                }

                String string = Character.toString(56 - y);

                graphics.text(this.font, string, this.imageHeight - this.font.width(string), 1 + (y * (this.imageWidth / 8)), color, false);
            }
        }
    }
}
