package com.r4men.game_night.gui.menu;

import com.r4men.game_night.gui.GNMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ChessMenu extends AbstractContainerMenu {
    public ChessMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId);
    }

    public ChessMenu(int pContainerId) {
        super(GNMenuTypes.CHESS_MENU.get(), pContainerId);

    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
