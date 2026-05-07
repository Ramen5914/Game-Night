package com.r4men.game_night.gui.chess.menu;

import com.r4men.game_night.block.entity.ChessBlockEntity;
import com.r4men.game_night.gui.GNMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ChessSetupMenu extends AbstractContainerMenu {
    private final ChessBlockEntity blockEntity;

    // Expose block entity to allow client screens to open the normal ChessMenu
    public ChessBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public ChessSetupMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, getBlockEntityFromBuffer(inv, extraData));
    }

    private static ChessBlockEntity getBlockEntityFromBuffer(Inventory inv, FriendlyByteBuf extraData) {
        if (extraData != null) {
            BlockPos pos = extraData.readBlockPos();
            if (inv.player.level().getBlockEntity(pos) instanceof ChessBlockEntity be) {
                return be;
            }
        }
        return null;
    }

    public ChessSetupMenu(int containerId, ChessBlockEntity blockEntity) {
        super(GNMenuTypes.CHESS_SETUP_MENU.get(), containerId);
        this.blockEntity = blockEntity;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public void setSetup(boolean isSetup) {
        blockEntity.setSetup(isSetup);
    }
}
