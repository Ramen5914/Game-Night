package com.r4men.game_night.datagen.models;

import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.TextureSlots;

import java.util.Optional;

public class SquareBoardModels {
    public static final TextureSlot BOARD = TextureSlot.create("board", TextureSlot.ALL);

    public static final ModelTemplate SQUARE_BOARD_TEMPLATE = new ModelTemplate(
            Optional.of(
                    ModelLocationUtils.decorateBlockModelLocation("game_night:square_board")
            ),
            Optional.of("_example"),
            BOARD
    );
}
