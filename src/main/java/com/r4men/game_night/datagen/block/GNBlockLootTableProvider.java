package com.r4men.game_night.datagen.block;

import com.r4men.game_night.block.GNBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GNBlockLootTableProvider extends BlockLootSubProvider {
    public GNBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(GNBlocks.CHESS.get());
        dropSelf(GNBlocks.MONOPOLY.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return GNBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
