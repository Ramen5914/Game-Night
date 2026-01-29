package com.r4men.game_night.util;

import com.r4men.game_night.GNConfig;
import net.minecraft.util.Tuple;

public class GNUtil {
    public static Tuple<Integer, Integer> getChessBoardColors() {
        String white = GNConfig.WHITE_SQUARE_COLOR.get();
        String black = GNConfig.BLACK_SQUARE_COLOR.get();
        int whiteColor = (int) Long.parseLong(white.replace("#", ""), 16);
        int blackColor = (int) Long.parseLong(black.replace("#", ""), 16);

        return new Tuple<>(whiteColor, blackColor);
    }
}
