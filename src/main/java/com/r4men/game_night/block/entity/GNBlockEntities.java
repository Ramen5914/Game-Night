package com.r4men.game_night.block.entity;

import com.r4men.game_night.GameNight;
import com.r4men.game_night.block.GNBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class GNBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, GameNight.ID);

    public static final Supplier<BlockEntityType<ChessBlockEntity>> CHESS_BE =
            BLOCK_ENTITIES.register("chess_be", () -> BlockEntityType.Builder.of(
                    ChessBlockEntity::new, GNBlocks.CHESS.get()).build(null));

    public static final Supplier<BlockEntityType<MonopolyBlockEntity>> MONOPOLY_BE =
            BLOCK_ENTITIES.register("monopoly_be", () -> BlockEntityType.Builder.of(
                    MonopolyBlockEntity::new, GNBlocks.MONOPOLY.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
