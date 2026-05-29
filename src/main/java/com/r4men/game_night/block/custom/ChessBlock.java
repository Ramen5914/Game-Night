package com.r4men.game_night.block.custom;

import com.mojang.serialization.MapCodec;
import com.r4men.game_night.block.entity.ChessBlockEntity;
import com.r4men.game_night.gui.menu.ChessMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class ChessBlock extends SquareBoardBlock {
    public static final MapCodec<ChessBlock> CODEC = simpleCodec(ChessBlock::new);
    
    public ChessBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends SquareBoardBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new ChessBlockEntity(blockPos, blockState);
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos) {
        return new SimpleMenuProvider(
                (containerId, playerInv, player) -> new ChessMenu(containerId, playerInv, level.getBlockEntity(pos)),
                Component.translatable("game_night.games.chess.title"));
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(
                    state.getMenuProvider(level, pos),
                    buf -> buf.writeBlockPos(pos)
            );
        }

        return InteractionResult.SUCCESS;
    }
}
