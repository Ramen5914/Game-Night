package com.r4men.game_night;

import net.neoforged.neoforge.common.ModConfigSpec;

public class GNConfig {
    // Server
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SERVER_SPEC;

    // Client
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec CLIENT_SPEC;

    public static final ModConfigSpec.ConfigValue<String> WHITE_SQUARE_COLOR;
    public static final ModConfigSpec.ConfigValue<String> WHITE_SQUARE_COLOR_2;
    public static final ModConfigSpec.ConfigValue<String> BLACK_SQUARE_COLOR;

    static {
        SERVER_SPEC = SERVER_BUILDER.build();

        WHITE_SQUARE_COLOR_2 = CLIENT_BUILDER
                .comment("ARGB hex color for light board squares. Example: #FFFFFFFF")
                .translation("config.game_night.board.white_square_color")
                .define("white_square_color", "#FFFFFFFF", GNConfig::validateHexColor);

        CLIENT_BUILDER.push("chess");

        WHITE_SQUARE_COLOR = CLIENT_BUILDER
                .comment("ARGB hex color for light board squares. Example: #FFFFFFFF")
                .translation("config.game_night.board.white_square_color")
                .define("white_square_color", "#FFFFFFFF", GNConfig::validateHexColor);

        BLACK_SQUARE_COLOR = CLIENT_BUILDER
                .comment("ARGB hex color for dark board squares. Example: #FF000000")
                .translation("config.game_night.board.black_square_color")
                .define("black_square_color", "#FF000000", GNConfig::validateHexColor);

        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.push("Monopoly");
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.push("Connect 4");
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.push("Uno");
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.push("Go");
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.push("Poker");
        CLIENT_BUILDER.pop();

        CLIENT_SPEC = CLIENT_BUILDER.build();
    }

    private static boolean validateHexColor(final Object obj) {
        return obj instanceof String hex && hex.matches("^#[0-9A-Fa-f]{8}$");
    }
}