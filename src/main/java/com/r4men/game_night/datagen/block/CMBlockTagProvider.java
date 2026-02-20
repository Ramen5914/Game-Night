package com.r4men.cobblemon_manufactory.datagen.block;

import com.jcraft.jorbis.Block;
import com.r4men.cobblemon_manufactory.CobblemonManufactory;
import com.r4men.cobblemon_manufactory.block.CMBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CMBlockTagProvider extends BlockTagsProvider {
    public CMBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CobblemonManufactory.ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(CMBlocks.EXP_QUARTZ_BLOCK.get())
                .add(CMBlocks.EXP_QUARTZ_TILES.get())
                .add(CMBlocks.SMALL_EXP_QUARTZ_TILES.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(CMBlocks.EXP_QUARTZ_BLOCK.get())
                .add(CMBlocks.EXP_QUARTZ_TILES.get())
                .add(CMBlocks.SMALL_EXP_QUARTZ_TILES.get());
    }
}
