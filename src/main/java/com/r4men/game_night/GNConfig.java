package com.r4men.game_night;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.awt.*;

public class GNConfig {
    // Server
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SERVER_SPEC;

    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_ELO_SYSTEM;

    // Client
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec CLIENT_SPEC;

    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_COORDINATES;

    static {
        ENABLE_ELO_SYSTEM = SERVER_BUILDER
                .comment("Where")
                .translation("game_night.configuration.enable_elo_system")
                .define("enable_elo_system", true);

        SERVER_SPEC = SERVER_BUILDER.build();

        CLIENT_BUILDER.push("chess");

        SHOW_COORDINATES = CLIENT_BUILDER
                .comment("Show square coordinates on the edge of the board")
                .translation("game_night.configuration.chess.show_coordinates")
                .define("show_coordinates", true);

        CLIENT_BUILDER.pop();

        CLIENT_SPEC = CLIENT_BUILDER.build();
    }
}