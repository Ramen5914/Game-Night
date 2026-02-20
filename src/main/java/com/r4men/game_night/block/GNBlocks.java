package com.r4men.game_night.block;

import com.r4men.game_night.GameNight;
import com.r4men.game_night.block.custom.ChessBlock;
    import com.r4men.game_night.block.custom.MonopolyBlock;
import com.r4men.game_night.item.GNItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class GNBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GameNight.ID);

    public static final DeferredBlock<Block> CHESS = registerBlock("chess",
            () -> new ChessBlock(BlockBehaviour.Properties.of().noOcclusion()));

    public static final DeferredBlock<Block> MONOPOLY = registerBlock("monopoly",
            () -> new MonopolyBlock(BlockBehaviour.Properties.of().noOcclusion()));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        GNItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
