package com.r4men.game_night.gui.screen;

import com.r4men.game_night.GameNight;
import com.r4men.game_night.gui.menu.ChessMenu;
import com.r4men.game_night.util.GNUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChessScreen extends AbstractContainerScreen<ChessMenu> {
    private static final int TILE_SIZE = 24;
    private static final ResourceLocation CHESS_PIECES = ResourceLocation.fromNamespaceAndPath(GameNight.ID, "textures/gui/pieces.png");

    // Maps piece char to texture U coordinate (V is 0 for white, 16 for black)
    private static final Map<Character, Integer> PIECE_U_COORDS = Map.of(
            'p', 0,
            'n', 16,
            'b', 32,
            'r', 48,
            'k', 64,
            'q', 80
    );

    // Selected square for click-to-click movement (-1 means no selection)
    private int selectedRow = -1;
    private int selectedCol = -1;

    // Possible moves for the selected piece
    private List<int[]> possibleMoves = new ArrayList<>();

    // Dragging state
    private boolean isDragging = false;
    private int dragStartRow = -1;
    private int dragStartCol = -1;
    private char draggedPiece = 0;

    public ChessScreen(ChessMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.titleLabelY = -10000;
        this.inventoryLabelY = -10000;

        this.imageWidth = 8 * TILE_SIZE;
        this.imageHeight = 8 * TILE_SIZE;
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
                int minX = x + (j * TILE_SIZE);
                int minY = y + (i * TILE_SIZE);
                int maxX = minX + TILE_SIZE;
                int maxY = minY + TILE_SIZE;

                guiGraphics.fill(minX, minY, maxX, maxY, (i+j) % 2 == 0 ? white : black);

                // Highlight selected square
                if (i == selectedRow && j == selectedCol) {
                    guiGraphics.fill(minX, minY, maxX, maxY, 0x8000FF00); // Semi-transparent green
                }

                // Highlight possible moves
                if (isPossibleMove(i, j)) {
                    boolean isCapture = menu.hasPiece(i, j);
                    if (isCapture) {
                        // Red tint for captures
                        guiGraphics.fill(minX, minY, maxX, maxY, 0x80FF0000);
                    } else {
                        // Draw a circle/dot indicator for empty squares
                        int centerX = minX + TILE_SIZE / 2;
                        int centerY = minY + TILE_SIZE / 2;
                        int radius = TILE_SIZE / 6;
                        guiGraphics.fill(centerX - radius, centerY - radius, centerX + radius, centerY + radius, 0x80808080);
                    }
                }
            }
        }
    }

    /**
     * Check if a square is a possible move destination.
     */
    private boolean isPossibleMove(int row, int col) {
        for (int[] move : possibleMoves) {
            if (move[0] == row && move[1] == col) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int startX = (width - imageWidth) / 2;
        int startY = (height - imageHeight) / 2;

        // Render pieces from board state
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char piece = menu.getPiece(row, col);
                if (piece != 0) {
                    // Don't render the piece being dragged at its original position
                    if (isDragging && row == dragStartRow && col == dragStartCol) {
                        continue;
                    }
                    renderPiece(guiGraphics, piece, startX + col * TILE_SIZE, startY + row * TILE_SIZE);
                }
            }
        }

        // Render dragged piece at mouse position
        if (isDragging && draggedPiece != 0) {
            renderPiece(guiGraphics, draggedPiece, mouseX - TILE_SIZE / 2, mouseY - TILE_SIZE / 2);
        }

        // Render time controls
        renderTimeControls(guiGraphics, startX, startY);
    }

    private void renderTimeControls(GuiGraphics guiGraphics, int boardX, int boardY) {
        // Black's clock (top)
        String blackTime = formatTime(menu.getBlackTimeMs());
        int blackClockX = boardX + imageWidth + 10;
        int blackClockY = boardY + 10;
        boolean blackActive = !menu.isWhiteToMove() && !menu.isGameOver();
        int blackColor = blackActive ? 0xFFFFFF00 : 0xFFFFFFFF; // Yellow if active
        guiGraphics.drawString(font, blackTime, blackClockX, blackClockY, blackColor);

        // White's clock (bottom)
        String whiteTime = formatTime(menu.getWhiteTimeMs());
        int whiteClockX = boardX + imageWidth + 10;
        int whiteClockY = boardY + imageHeight - 20;
        boolean whiteActive = menu.isWhiteToMove() && !menu.isGameOver();
        int whiteColor = whiteActive ? 0xFFFFFF00 : 0xFFFFFFFF; // Yellow if active
        guiGraphics.drawString(font, whiteTime, whiteClockX, whiteClockY, whiteColor);

        // Show check indicator
        if (!menu.isGameOver() && menu.isInCheck()) {
            String checkMsg = "Check!";
            int checkX = boardX + imageWidth + 10;
            int checkY = boardY + imageHeight / 2 - 5;
            guiGraphics.drawString(font, checkMsg, checkX, checkY, 0xFFFF5555);
        }

        // Show game over message
        if (menu.isGameOver()) {
            String reason = menu.getGameOverReason();
            char winner = menu.getWinner();
            String message;
            if (winner == 'w') {
                message = "White wins! " + reason;
            } else if (winner == 'b') {
                message = "Black wins! " + reason;
            } else {
                message = "Draw! " + reason;
            }
            int msgX = boardX + (imageWidth - font.width(message)) / 2;
            int msgY = boardY + imageHeight / 2 - 5;
            guiGraphics.fill(msgX - 5, msgY - 5, msgX + font.width(message) + 5, msgY + 15, 0xCC000000);
            guiGraphics.drawString(font, message, msgX, msgY, 0xFFFF5555);
        }
    }

    private String formatTime(long timeMs) {
        if (timeMs <= 0) return "0:00";
        long totalSeconds = timeMs / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private void renderPiece(GuiGraphics guiGraphics, char piece, int x, int y) {
        char lowerPiece = Character.toLowerCase(piece);
        Integer uCoord = PIECE_U_COORDS.get(lowerPiece);
        if (uCoord != null) {
            int vCoord = Character.isUpperCase(piece) ? 0 : 16;
            guiGraphics.blit(CHESS_PIECES, x, y, TILE_SIZE, TILE_SIZE, uCoord, vCoord, 16, 16, 96, 32);
        }
    }

    private int[] getBoardCoords(double mouseX, double mouseY) {
        int startX = (width - imageWidth) / 2;
        int startY = (height - imageHeight) / 2;

        // Check if mouse is within board bounds first
        if (mouseX < startX || mouseX >= startX + imageWidth ||
            mouseY < startY || mouseY >= startY + imageHeight) {
            return null;
        }

        int col = (int) ((mouseX - startX) / TILE_SIZE);
        int row = (int) ((mouseY - startY) / TILE_SIZE);

        return new int[]{row, col};
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && !menu.isGameOver()) { // Left click, game not over
            int[] coords = getBoardCoords(mouseX, mouseY);
            if (coords != null) {
                int row = coords[0];
                int col = coords[1];
                char piece = menu.getPiece(row, col);

                if (selectedRow == -1 && selectedCol == -1) {
                    // No piece selected yet - only allow selecting current player's pieces
                    if (piece != 0 && menu.isWhite(piece) == menu.isWhiteToMove()) {
                        // Select this piece (for click-to-click) and start potential drag
                        selectedRow = row;
                        selectedCol = col;
                        possibleMoves = menu.getPossibleMoves(row, col);
                        isDragging = true;
                        dragStartRow = row;
                        dragStartCol = col;
                        draggedPiece = piece;
                    }
                } else {
                    // A piece is already selected - this is the destination
                    if (row == selectedRow && col == selectedCol) {
                        // Clicked same square - deselect
                        selectedRow = -1;
                        selectedCol = -1;
                        possibleMoves.clear();
                    } else if (isPossibleMove(row, col)) {
                        // Only move if it's a legal move
                        menu.movePiece(selectedRow, selectedCol, row, col);
                        selectedRow = -1;
                        selectedCol = -1;
                        possibleMoves.clear();
                    } else if (piece != 0 && menu.isWhite(piece) == menu.isWhiteToMove()) {
                        // Clicked on another piece of the current player - select it instead
                        selectedRow = row;
                        selectedCol = col;
                        possibleMoves = menu.getPossibleMoves(row, col);
                    }
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && isDragging) {
            int[] coords = getBoardCoords(mouseX, mouseY);
            if (coords != null) {
                int row = coords[0];
                int col = coords[1];

                // If released on a different square than where drag started, try to move the piece
                if (row != dragStartRow || col != dragStartCol) {
                    if (isPossibleMove(row, col)) {
                        // Only move if it's a legal move
                        menu.movePiece(dragStartRow, dragStartCol, row, col);
                        // Clear selection since we completed a drag move
                        selectedRow = -1;
                        selectedCol = -1;
                        possibleMoves.clear();
                    }
                    // If not a legal move, piece snaps back (selection stays)
                }
                // If released on same square, keep selection for click-to-click
            }
            // If released outside board, cancel drag but keep selection

            isDragging = false;
            draggedPiece = 0;
            dragStartRow = -1;
            dragStartCol = -1;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && isDragging) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
}
