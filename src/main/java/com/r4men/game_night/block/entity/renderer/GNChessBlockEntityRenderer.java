package com.r4men.game_night.block.entity.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import com.mojang.blaze3d.vertex.PoseStack;
import com.r4men.game_night.block.entity.ChessBlockEntity;
import com.r4men.game_night.client.render.chess.ChessPieceBakedModel;
import com.r4men.game_night.client.render.chess.OutlineJob;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.data.ModelData;

public class GNChessBlockEntityRenderer implements BlockEntityRenderer<ChessBlockEntity> {
    private final Minecraft minecraft;
    private final Map<Character, String> pieceModelNames = new HashMap<>();
    private final Map<Character, Float> pieceScales = new HashMap<>();
    private final Map<Character, BakedModel> modelCache = new HashMap<>();

    /** Populated each frame by renderPiece(); consumed and cleared by GNOutlineRenderer. */
    public static final List<OutlineJob> PENDING_OUTLINES = new ArrayList<>();

    public GNChessBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.minecraft = Minecraft.getInstance();
        initializePieceModels();
    }

    private void initializePieceModels() {
        // Map each piece character to its model name
        // Pieces: Pawn, Knight, Bishop, Rook, Queen, King
        pieceModelNames.put('P', "pawn");   // White Pawn
        pieceModelNames.put('N', "knight");  // White Knight
        pieceModelNames.put('B', "bishop");  // White Bishop
        pieceModelNames.put('R', "rook");   // White Rook
        pieceModelNames.put('Q', "queen");   // White Queen
        pieceModelNames.put('K', "king");   // White King

        pieceModelNames.put('p', "pawn");   // Black Pawn
        pieceModelNames.put('n', "knight");  // Black Knight
        pieceModelNames.put('b', "bishop");  // Black Bishop
        pieceModelNames.put('r', "rook");   // Black Rook
        pieceModelNames.put('q', "queen");   // Black Queen
        pieceModelNames.put('k', "king");   // Black King

        // Scale factors — all pieces use the same scale since they're Blockbench models
        // Adjust this value to make pieces bigger or smaller relative to the board
        float scale = 0.125f; // 1/8 of a block

        for (char piece : pieceModelNames.keySet()) {
            pieceScales.put(piece, scale);
        }
    }

    private BakedModel getModel(char piece) {
        return modelCache.computeIfAbsent(piece, pieceName -> {
            String color = Character.isUpperCase(piece) ? "white" : "black";

            ResourceLocation modelLocation = ResourceLocation.fromNamespaceAndPath("game_night", "chess/pieces/" + color + "/" + Character.toLowerCase(pieceName));
            ModelManager modelManager = minecraft.getBlockRenderer().getBlockModelShaper().getModelManager();
            ModelResourceLocation modelResLoc = ModelResourceLocation.standalone(modelLocation);
            return modelManager.getModel(modelResLoc);
        });
    }

    private static int getPackedOutlineColor(BakedModel bakedModel, char piece) {
        if (bakedModel instanceof ChessPieceBakedModel chessPieceBakedModel) {
            return chessPieceBakedModel.getOutlineColor();
        }

        int[] fallback = getOutlineColor(piece);
        return (fallback[0] << 16) | (fallback[1] << 8) | fallback[2];
    }

    private static int[] getOutlineColor(char piece) {
        // Distinct per-piece outline palette (upper/lowercase keep separate shades).
        return switch (piece) {
            case 'K' -> new int[] {255, 215, 0};
            case 'Q' -> new int[] {186, 85, 211};
            case 'R' -> new int[] {70, 130, 255};
            case 'B' -> new int[] {60, 179, 113};
            case 'N' -> new int[] {255, 140, 0};
            case 'P' -> new int[] {220, 20, 60};
            case 'k' -> new int[] {255, 239, 128};
            case 'q' -> new int[] {221, 160, 221};
            case 'r' -> new int[] {135, 206, 250};
            case 'b' -> new int[] {144, 238, 144};
            case 'n' -> new int[] {255, 179, 102};
            case 'p' -> new int[] {255, 99, 132};
            default -> new int[] {0, 0, 0};
        };
    }

    @Override
    public void render(@NotNull ChessBlockEntity chessBlockEntity, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource,
                       int packedLight, int packedOverlay) {
        String fen = chessBlockEntity.getFen();
        if (fen == null || fen.isEmpty()) {
            return;
        }

        char[][] board = parseFenToBoard(fen);
        if (board == null) {
            return;
        }

        poseStack.pushPose();

        float boardStartX = 0.0f;
        float boardStartZ = 0.0f;
        float boardWidth = 1f;
        float squareSize = boardWidth / 8.0f;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char piece = board[row][col];
                if (piece != ' ' && piece != '0') {
                    renderPiece(poseStack, multiBufferSource, piece, row, col,
                               boardStartX, boardStartZ, squareSize, packedLight,
                               packedOverlay, partialTick);
                }
            }
        }

        poseStack.popPose();
    }

    private char[][] parseFenToBoard(String fen) {
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

        for (int rank = 0; rank < 8; rank++) {
            String rankStr = ranks[rank];
            int file = 0;

            for (char c : rankStr.toCharArray()) {
                if (Character.isDigit(c)) {
                    int emptySquares = Character.getNumericValue(c);
                    for (int i = 0; i < emptySquares && file < 8; i++) {
                        board[rank][file] = ' ';
                        file++;
                    }
                } else {
                    if (file < 8) {
                        board[rank][file] = c;
                        file++;
                    }
                }
            }

            while (file < 8) {
                board[rank][file] = ' ';
                file++;
            }
        }

        return board;
    }

    private void renderPiece(PoseStack poseStack, MultiBufferSource buffer, char piece,
                            int row, int col, float boardStartX, float boardStartZ,
                            float squareSize, int packedLight, int packedOverlay, float partialTick) {
        Float scale = pieceScales.get(piece);

        if (scale == null) {
            return;
        }

        BakedModel bakedModel = getModel(piece);
        if (bakedModel == null) {
            return;
        }

        poseStack.pushPose();

        // Calculate position on board
        float x = boardStartX + (col * squareSize) + (squareSize / 2.0f);
        float z = boardStartZ + (row * squareSize) + (squareSize / 2.0f);
        float y = 1.0f / 16.0f;

        // Translate to the piece's position
        poseStack.translate(x, y, z);

        // Scale the piece
        poseStack.scale(scale, scale, scale);

        // Rotate black pieces 180 degrees around the Y axis
        if (Character.isLowerCase(piece)) {
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0f));
        }

        // Center the model on its position
        poseStack.translate(-0.5f, 0, -0.5f);

        // Queue this piece for the deferred silhouette outline pass.
        // This outlines the final visible piece silhouette rather than extruding each
        // cube inside the model, which avoids the blocky shell artifact.
        int outlineColor = getPackedOutlineColor(bakedModel, piece);
        PENDING_OUTLINES.add(new OutlineJob(
                bakedModel,
                new Matrix4f(poseStack.last().pose()),
                (outlineColor >> 16) & 0xFF,
                (outlineColor >> 8) & 0xFF,
                outlineColor & 0xFF));

        // Normal model render
        ModelBlockRenderer renderer = minecraft.getBlockRenderer().getModelRenderer();
        renderer.renderModel(poseStack.last(), buffer.getBuffer(RenderType.cutoutMipped()),
                            null, bakedModel, 1.0f, 1.0f, 1.0f, packedLight, packedOverlay,
                            ModelData.EMPTY, RenderType.cutoutMipped());

        poseStack.popPose();
    }
}
