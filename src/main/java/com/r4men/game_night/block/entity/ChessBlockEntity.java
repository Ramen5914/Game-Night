package com.r4men.game_night.block.entity;

import com.r4men.game_night.block.GNBlockEntities;
import com.r4men.game_night.gui.menu.ChessMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class ChessBlockEntity extends BlockEntity implements MenuProvider {
    private String fen;
    private boolean isSetup;
    private long timeControl; // Seconds
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
        isSetup = false;
        whiteTimeMs = timeControl * 1000; // 5 minutes
        blackTimeMs = timeControl * 1000;
        incrementMs = 0; // 0 seconds
        lastMoveTimestamp = System.currentTimeMillis();
        gameStarted = false;
        gameOver = false;
        winner = 0;
        gameOverReason = "";
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("game_night.games.chess.title");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new ChessMenu(i, this);
    }

    public String getFen() {
        return fen;
    }


    public void setFen(String fen) {
        this.fen = fen;
        setChanged();
    }

    public boolean getIsSetup() {
        return isSetup;
    }

    public void setIsSetup(boolean isSetup) {
        this.isSetup = isSetup;
        setChanged();
    }

    public long getTimeControlSeconds() {
        return timeControlSeconds;
    }

    public void setTimeControlSeconds(long timeControlSeconds) {
        this.timeControlSeconds = timeControlSeconds;
        setChanged();
    }

    public long getIncrementSeconds() {
        return incrementSeconds;
    }

    public void setIncrementSeconds(long incrementSeconds) {
        this.incrementSeconds = incrementSeconds;
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

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
        setChanged();
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
        setChanged();
    }
}
