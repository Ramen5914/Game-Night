package com.r4men.cobblemon_manufactory.datagen.block;

import com.r4men.cobblemon_manufactory.block.CMBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class CMBlockLootTableProvider extends BlockLootSubProvider {
    public CMBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(CMBlocks.EXP_QUARTZ_BLOCK.get());
        dropSelf(CMBlocks.EXP_QUARTZ_TILES.get());
        dropSelf(CMBlocks.SMALL_EXP_QUARTZ_TILES.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return CMBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
