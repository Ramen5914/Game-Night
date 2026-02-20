package com.r4men.cobblemon_manufactory.datagen.block;

import com.r4men.cobblemon_manufactory.CobblemonManufactory;
import com.r4men.cobblemon_manufactory.block.CMBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class CMBlockStateProvider extends BlockStateProvider {
    public CMBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CobblemonManufactory.ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        customBlockWithItem(CMBlocks.EXP_QUARTZ_BLOCK, models()
                .cubeColumn("exp_quartz_block",
                        modLoc("block/exp_quartz_block_side"),
                        modLoc("block/exp_quartz_block_top")));

        blockWithItem(CMBlocks.EXP_QUARTZ_TILES);
        blockWithItem(CMBlocks.SMALL_EXP_QUARTZ_TILES);
    }

    private void blockWithItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void customBlockWithItem(DeferredBlock<Block> deferredBlock, ModelFile model) {
        simpleBlockWithItem(deferredBlock.get(), model);
    }
}
