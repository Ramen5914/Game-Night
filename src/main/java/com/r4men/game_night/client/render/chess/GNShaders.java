package com.r4men.game_night.client.render.chess;

import net.minecraft.client.renderer.ShaderInstance;

public final class GNShaders {
    private GNShaders() {}

    /** Populated by RegisterShadersEvent in GameNightClient, null until then. */
    public static ShaderInstance CHESS_OUTLINE_SHADER = null;

    /** Screen-space edge detection shader for the outline composite pass. */
    public static ShaderInstance CHESS_EDGE_SHADER = null;
}
