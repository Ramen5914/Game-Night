package com.r4men.game_night.datagen.block;

import com.r4men.game_night.block.GNBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GNBlockLootSubProvider extends BlockLootSubProvider {
    public GNBlockLootSubProvider(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected void generate() {
        this.dropSelf(GNBlocks.CHESS.get());
        this.dropSelf(GNBlocks.MONOPOLY.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return GNBlocks.BLOCKS.getEntries()
                .stream()
                .map(e -> (Block) e.value())
                .toList();
    }
}
