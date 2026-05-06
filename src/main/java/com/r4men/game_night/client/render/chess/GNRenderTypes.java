package com.r4men.game_night.client.render.chess;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public final class GNRenderTypes {
    private GNRenderTypes() {}

    /**
     * Outline render type for chess pieces.
     * Uses POSITION_COLOR format, standard back-face culling, and the custom
     * chess_outline shader. Reversed vertex winding in the BER produces the
     * outline ring at the silhouette of each piece.
     */
    public static final RenderType CHESS_OUTLINE = RenderType.create(
        "game_night_chess_outline",
        DefaultVertexFormat.POSITION_COLOR,
        VertexFormat.Mode.QUADS,
        256,
        false,
        false,
        RenderType.CompositeState.builder()
            .setShaderState(new RenderStateShard.ShaderStateShard(() -> GNShaders.CHESS_OUTLINE_SHADER))
            .setCullState(RenderStateShard.CULL)
            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
            .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
            .setLayeringState(RenderStateShard.NO_LAYERING)
            .setOutputState(RenderStateShard.MAIN_TARGET)
            .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
            .createCompositeState(false)
    );
}
