package com.r4men.game_night.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.r4men.game_night.GameNight;
import com.r4men.game_night.block.entity.ChessBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GNChessBlockEntityRenderer implements BlockEntityRenderer<ChessBlockEntity> {
    private final Minecraft minecraft;
    private final Map<Character, ModelResourceLocation> pieceModelLocations = new HashMap<>();

    public GNChessBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.minecraft = Minecraft.getInstance();
        initializePieceModels();
    }

    private void initializePieceModels() {
        // White pieces (uppercase in FEN)
        pieceModelLocations.put('P', new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess/white_pawn"), "inventory"));
        pieceModelLocations.put('N', new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess/white_knight"), "inventory"));
        pieceModelLocations.put('B', new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess/white_bishop"), "inventory"));
        pieceModelLocations.put('R', new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess/white_rook"), "inventory"));
        pieceModelLocations.put('Q', new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess/white_queen"), "inventory"));
        pieceModelLocations.put('K', new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess/white_king"), "inventory"));

        // Black pieces (lowercase in FEN)
        pieceModelLocations.put('p', new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess/black_pawn"), "inventory"));
        pieceModelLocations.put('n', new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess/black_knight"), "inventory"));
        pieceModelLocations.put('b', new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess/black_bishop"), "inventory"));
        pieceModelLocations.put('r', new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess/black_rook"), "inventory"));
        pieceModelLocations.put('q', new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess/black_queen"), "inventory"));
        pieceModelLocations.put('k', new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess/black_king"), "inventory"));
    }

    @Override
    public void render(@NotNull ChessBlockEntity chessBlockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        String fen = chessBlockEntity.getFen();
        if (fen == null || fen.isEmpty()) {
            return;
        }

        // Parse FEN to get board state
        char[][] board = parseFenToBoard(fen);
        if (board == null) {
            return;
        }

        poseStack.pushPose();

        // Board now spans the full 16x16 block
        float boardStartX = 0.0f;
        float boardStartZ = 0.0f;
        float boardWidth = 1f;
        float squareSize = boardWidth / 8.0f;

        // Render pieces
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char piece = board[row][col];
                if (piece != ' ' && piece != '0') {
                    renderPiece(poseStack, multiBufferSource, piece, row, col,
                               boardStartX, boardStartZ, squareSize, packedLight);
                }
            }
        }

        poseStack.popPose();
    }

    private char[][] parseFenToBoard(String fen) {
        // FEN format: "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        // We only need the first part (board state)
        String[] parts = fen.split(" ");
        if (parts.length == 0) {
            return null;
        }

        String boardState = parts[0];
        String[] ranks = boardState.split("/");
        if (ranks.length != 8) {
            return null;
        }

        char[][] board = new char[8][8];

        // FEN starts from rank 8 (top of board, black's side) down to rank 1
        for (int rank = 0; rank < 8; rank++) {
            String rankStr = ranks[rank];
            int file = 0;

            for (char c : rankStr.toCharArray()) {
                if (Character.isDigit(c)) {
                    // Empty squares
                    int emptySquares = Character.getNumericValue(c);
                    for (int i = 0; i < emptySquares && file < 8; i++) {
                        board[rank][file] = ' ';
                        file++;
                    }
                } else {
                    // Piece
                    if (file < 8) {
                        board[rank][file] = c;
                        file++;
                    }
                }
            }

            // Fill remaining squares if needed
            while (file < 8) {
                board[rank][file] = ' ';
                file++;
            }
        }

        return board;
    }

    private void renderPiece(PoseStack poseStack, MultiBufferSource buffer, char piece,
                            int row, int col, float boardStartX, float boardStartZ,
                            float squareSize, int packedLight) {
        ModelResourceLocation modelLocation = pieceModelLocations.get(piece);
        if (modelLocation == null) {
            return;
        }

        BakedModel model = minecraft.getModelManager().getModel(modelLocation);

        poseStack.pushPose();

        // Calculate position
        // Chess board: row 0 is rank 8 (top), row 7 is rank 1 (bottom)
        // col 0 is file a (left), col 7 is file h (right)
        float x = boardStartX + (col * squareSize) + (squareSize / 2.0f);
        float z = boardStartZ + (row * squareSize) + (squareSize / 2.0f);
        float y = 1.0f / 16.0f; // Slightly above the board surface (board height is 1 pixel)

        poseStack.translate(x, y, z);

        // Rotate to face the correct direction (models face north by default)
        poseStack.mulPose(Axis.YP.rotationDegrees(180));

        // Scale down the piece to fit in the square
        float scale = squareSize * 0.8f; // 80% of square size for some spacing
        poseStack.scale(scale, scale, scale);

        // Render the model using a dummy ItemStack
        ItemStack dummyStack = new ItemStack(Items.PAPER);
        minecraft.getItemRenderer().render(
            dummyStack,
            ItemDisplayContext.FIXED,
            false,
            poseStack,
            buffer,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            model
        );

        poseStack.popPose();
    }
}
