package com.r4men.game_night;

import net.neoforged.neoforge.common.ModConfigSpec;

public class GNConfig {
    // Server
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();

//    public static final ModConfigSpec.BooleanValue REMOVE_MECHANICAL_CRAFTER_RECIPES = BUILDER
//            .comment("Whether to remove pokeball crafting recipes from Create mod's Mechanical Crafter.")
//            .worldRestart()
//            .define("remove_mechanical_crafter_recipes", true);

    static final ModConfigSpec SERVER_SPEC = SERVER_BUILDER.build();

    // Client
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<String> WHITE_SQUARE_COLOR = CLIENT_BUILDER
            .comment("What color should the white squares be? (ex: #ffffff [#aarrggbb])")
            .define("white_square_color", "#ffffffff", GNConfig::validateHexColor);

    public static final ModConfigSpec.ConfigValue<String> BLACK_SQUARE_COLOR = CLIENT_BUILDER
            .comment("What color should the black squares be? (ex: #ff000000 [#aarrggbb])")
            .define("black_square_color", "#ff000000", GNConfig::validateHexColor);

    static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

    private static boolean validateHexColor(final Object obj) {
        if (obj instanceof String hex) {
            return hex.matches("^#[0-9A-Fa-f]{8}$");
        } else {
            return false;
        }
    }
}
