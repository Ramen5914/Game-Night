package com.r4men.game_night.block.entity;

import com.r4men.game_night.gui.menu.ChessMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChessBlockEntity extends BlockEntity implements MenuProvider {
    private String fen;
    private long whiteTimeMs;
    private long blackTimeMs;
    private long lastMoveTimestamp;
    private long incrementMs;
    private boolean gameStarted;
    private boolean gameOver;
    private char winner;
    private String gameOverReason;

    public ChessBlockEntity(BlockPos pos, BlockState blockState) {
        super(GNBlockEntities.CHESS_BE.get(), pos, blockState);

        fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        whiteTimeMs = 5 * 60 * 1000; // 5 minutes
        blackTimeMs = 5 * 60 * 1000;
        incrementMs = 0; // 0 seconds
        lastMoveTimestamp = System.currentTimeMillis();
        gameStarted = false;
        gameOver = false;
        winner = 0;
        gameOverReason = "";
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Chess");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new ChessMenu(i, this);
    }

    // Getters and setters
    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
        setChanged();
    }

    public long getWhiteTimeMs() {
        return whiteTimeMs;
    }

    public void setWhiteTimeMs(long whiteTimeMs) {
        this.whiteTimeMs = whiteTimeMs;
        setChanged();
    }

    public long getBlackTimeMs() {
        return blackTimeMs;
    }

    public void setBlackTimeMs(long blackTimeMs) {
        this.blackTimeMs = blackTimeMs;
        setChanged();
    }

    public long getLastMoveTimestamp() {
        return lastMoveTimestamp;
    }

    public void setLastMoveTimestamp(long lastMoveTimestamp) {
        this.lastMoveTimestamp = lastMoveTimestamp;
        setChanged();
    }

    public long getIncrementMs() {
        return incrementMs;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
        setChanged();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        setChanged();
    }

    public char getWinner() {
        return winner;
    }

    public void setWinner(char winner) {
        this.winner = winner;
        setChanged();
    }

    public String getGameOverReason() {
        return gameOverReason;
    }

    public void setGameOverReason(String gameOverReason) {
        this.gameOverReason = gameOverReason;
        setChanged();
    }

    public BlockPos getBlockPos() {
        return this.worldPosition;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        tag.putString("fen", fen);
        tag.putLong("whiteTimeMs", whiteTimeMs);
        tag.putLong("blackTimeMs", blackTimeMs);
        tag.putLong("lastMoveTimestamp", lastMoveTimestamp);
        tag.putLong("incrementMs", incrementMs);
        tag.putBoolean("gameStarted", gameStarted);
        tag.putBoolean("gameOver", gameOver);
        tag.putString("winner", String.valueOf(winner));
        tag.putString("gameOverReason", gameOverReason);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("fen")) {
            fen = tag.getString("fen");
        }
        if (tag.contains("whiteTimeMs")) {
            whiteTimeMs = tag.getLong("whiteTimeMs");
        }
        if (tag.contains("blackTimeMs")) {
            blackTimeMs = tag.getLong("blackTimeMs");
        }
        if (tag.contains("lastMoveTimestamp")) {
            lastMoveTimestamp = tag.getLong("lastMoveTimestamp");
        }
        if (tag.contains("incrementMs")) {
            incrementMs = tag.getLong("incrementMs");
        }
        if (tag.contains("gameStarted")) {
            gameStarted = tag.getBoolean("gameStarted");
        }
        if (tag.contains("gameOver")) {
            gameOver = tag.getBoolean("gameOver");
        }
        if (tag.contains("winner")) {
            String winnerStr = tag.getString("winner");
            winner = winnerStr.isEmpty() ? 0 : winnerStr.charAt(0);
        }
        if (tag.contains("gameOverReason")) {
            gameOverReason = tag.getString("gameOverReason");
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
