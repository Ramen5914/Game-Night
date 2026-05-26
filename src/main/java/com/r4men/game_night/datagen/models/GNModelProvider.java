package com.r4men.game_night.datagen.models;

import com.r4men.game_night.GameNight;
import com.r4men.game_night.block.GNBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Optional;

public class GNModelProvider extends ModelProvider {
    public GNModelProvider(PackOutput packOutput) {
        super(packOutput, GameNight.ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createHorizontallyRotatedBlock(GNBlocks.CHESS.get(), SQUARE_BOARD_PROVIDER);
        blockModels.createTrivialBlock(GNBlocks.MONOPOLY.get(), SQUARE_BOARD_PROVIDER);

        // Black pieces
        itemModels.itemModelOutput.register(
                GameNight.getIdentifier("chess/black_bishop"),
                new ClientItem(
                        new CuboidItemModelWrapper.Unbaked(
                                GameNight.getIdentifier("chess/pieces/black/b"),
                                Optional.empty(),
                                List.of()
                        ),
                        ClientItem.Properties.DEFAULT
                )
        );
        itemModels.itemModelOutput.register(
                GameNight.getIdentifier("chess/black_king"),
                new ClientItem(
                        new CuboidItemModelWrapper.Unbaked(
                                GameNight.getIdentifier("chess/pieces/black/k"),
                                Optional.empty(),
                                List.of()
                        ),
                        ClientItem.Properties.DEFAULT
                )
        );
        itemModels.itemModelOutput.register(
                GameNight.getIdentifier("chess/black_knight"),
                new ClientItem(
                        new CuboidItemModelWrapper.Unbaked(
                                GameNight.getIdentifier("chess/pieces/black/n"),
                                Optional.empty(),
                                List.of()
                        ),
                        ClientItem.Properties.DEFAULT
                )
        );
        itemModels.itemModelOutput.register(
                GameNight.getIdentifier("chess/black_pawn"),
                new ClientItem(
                        new CuboidItemModelWrapper.Unbaked(
                                GameNight.getIdentifier("chess/pieces/black/p"),
                                Optional.empty(),
                                List.of()
                        ),
                        ClientItem.Properties.DEFAULT
                )
        );
        itemModels.itemModelOutput.register(
                GameNight.getIdentifier("chess/black_queen"),
                new ClientItem(
                        new CuboidItemModelWrapper.Unbaked(
                                GameNight.getIdentifier("chess/pieces/black/q"),
                                Optional.empty(),
                                List.of()
                        ),
                        ClientItem.Properties.DEFAULT
                )
        );
        itemModels.itemModelOutput.register(
                GameNight.getIdentifier("chess/black_rook"),
                new ClientItem(
                        new CuboidItemModelWrapper.Unbaked(
                                GameNight.getIdentifier("chess/pieces/black/r"),
                                Optional.empty(),
                                List.of()
                        ),
                        ClientItem.Properties.DEFAULT
                )
        );

        // White pieces
        itemModels.itemModelOutput.register(
                GameNight.getIdentifier("chess/white_bishop"),
                new ClientItem(
                        new CuboidItemModelWrapper.Unbaked(
                                GameNight.getIdentifier("chess/pieces/white/b"),
                                Optional.empty(),
                                List.of()
                        ),
                        ClientItem.Properties.DEFAULT
                )
        );
        itemModels.itemModelOutput.register(
                GameNight.getIdentifier("chess/white_king"),
                new ClientItem(
                        new CuboidItemModelWrapper.Unbaked(
                                GameNight.getIdentifier("chess/pieces/white/k"),
                                Optional.empty(),
                                List.of()
                        ),
                        ClientItem.Properties.DEFAULT
                )
        );
        itemModels.itemModelOutput.register(
                GameNight.getIdentifier("chess/white_knight"),
                new ClientItem(
                        new CuboidItemModelWrapper.Unbaked(
                                GameNight.getIdentifier("chess/pieces/white/n"),
                                Optional.empty(),
                                List.of()
                        ),
                        ClientItem.Properties.DEFAULT
                )
        );
        itemModels.itemModelOutput.register(
                GameNight.getIdentifier("chess/white_pawn"),
                new ClientItem(
                        new CuboidItemModelWrapper.Unbaked(
                                GameNight.getIdentifier("chess/pieces/white/p"),
                                Optional.empty(),
                                List.of()
                        ),
                        ClientItem.Properties.DEFAULT
                )
        );
        itemModels.itemModelOutput.register(
                GameNight.getIdentifier("chess/white_queen"),
                new ClientItem(
                        new CuboidItemModelWrapper.Unbaked(
                                GameNight.getIdentifier("chess/pieces/white/q"),
                                Optional.empty(),
                                List.of()
                        ),
                        ClientItem.Properties.DEFAULT
                )
        );
        itemModels.itemModelOutput.register(
                GameNight.getIdentifier("chess/white_rook"),
                new ClientItem(
                        new CuboidItemModelWrapper.Unbaked(
                                GameNight.getIdentifier("chess/pieces/white/r"),
                                Optional.empty(),
                                List.of()
                        ),
                        ClientItem.Properties.DEFAULT
                )
        );
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
