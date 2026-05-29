package com.r4men.game_night.block.entity;

import com.r4men.game_night.block.GNBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MonopolyBlockEntity extends BlockEntity {
    public MonopolyBlockEntity(BlockPos pos, BlockState blockState) {
        super(GNBlockEntities.MONOPOLY_BE.get(), pos, blockState);
    }
}
