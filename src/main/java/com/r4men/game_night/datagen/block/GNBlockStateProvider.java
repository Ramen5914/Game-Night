package com.r4men.game_night.datagen.block;

import com.r4men.game_night.GameNight;
import com.r4men.game_night.block.GNBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class GNBlockStateProvider extends BlockStateProvider {
    public GNBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, GameNight.ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        customBlockWithItem(GNBlocks.MONOPOLY, models().getExistingFile(modLoc("block/monopoly")));
        customBlockWithItem(GNBlocks.CHESS, models().getExistingFile(modLoc("block/chess")));
    }

    private void blockWithItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void customBlockWithItem(DeferredBlock<Block> deferredBlock, ModelFile model) {
        simpleBlockWithItem(deferredBlock.get(), model);
    }
}
