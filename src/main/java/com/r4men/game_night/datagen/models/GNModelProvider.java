package com.r4men.game_night.datagen.models;

import com.r4men.game_night.GameNight;
import com.r4men.game_night.block.GNBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.*;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class GNModelProvider extends ModelProvider {
    public GNModelProvider(PackOutput packOutput) {
        super(packOutput, GameNight.ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createTrivialBlock(GNBlocks.CHESS.get(), SQUARE_BOARD_PROVIDER);
        blockModels.createTrivialBlock(GNBlocks.MONOPOLY.get(), SQUARE_BOARD_PROVIDER);
    }

    public static final TextureSlot BOARD = TextureSlot.create("board", TextureSlot.ALL);

    public static final ModelTemplate SQUARE_BOARD_TEMPLATE = new ModelTemplate(
            Optional.of(
                    ModelLocationUtils.decorateBlockModelLocation("game_night:square_board")
            ),
            Optional.empty(),
            BOARD
    );

    public static final TexturedModel.Provider SQUARE_BOARD_PROVIDER = TexturedModel.createDefault(
            // Block to texture mapping
            block -> new TextureMapping()
                    .put(BOARD, TextureMapping.getBlockTexture(block)),
            // The template to generate from
            SQUARE_BOARD_TEMPLATE
    );
}
