package com.r4men.game_night.client.render.chess;

import net.minecraft.client.resources.model.BakedModel;
import org.joml.Matrix4f;

/**
 * Snapshot of everything needed to render a chess piece's silhouette outline.
 * Captured by the BER each frame; consumed by GNOutlineRenderer in the
 * AFTER_BLOCK_ENTITIES stage event.
 *
 * @param model        The baked model for this piece.
 * @param matrix       Full model-to-view-space transform recorded at BER render time.
 *                     Vertices in model-local space multiplied by this matrix produce
 *                     camera-relative world positions, ready for projection.
 * @param outlineRed   Red channel (0..255) for this piece's outline color.
 * @param outlineGreen Green channel (0..255) for this piece's outline color.
 * @param outlineBlue  Blue channel (0..255) for this piece's outline color.
 */
public record OutlineJob(BakedModel model, Matrix4f matrix, int outlineRed, int outlineGreen, int outlineBlue) {}
