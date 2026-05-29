package com.r4men.game_night.gui.menu;

import com.r4men.game_night.block.entity.ChessBlockEntity;
import com.r4men.game_night.gui.GNMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.NonNull;

public class ChessMenu extends AbstractContainerMenu {
    public static ChessBlockEntity blockEntity;

    // Client Constructor
    public ChessMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        BlockEntity be = playerInv.player.level().getBlockEntity(extraData.readBlockPos());
        this(containerId, playerInv, be);
    }

    // Server Constructor
    public ChessMenu(int containerId, Inventory playerInv, BlockEntity be) {
        super(GNMenuTypes.CHESS_MENU.get(), containerId);

        if (be instanceof ChessBlockEntity chessBe) {
            blockEntity = chessBe;
        }
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return true;
    }

    public String getSimplifiedFen() {
        return blockEntity.getSimplifiedFen();
    }
}
