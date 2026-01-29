package com.r4men.game_night.gui.menu;

import com.r4men.game_night.gui.GNMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChessMenu extends AbstractContainerMenu {
    // Board state: 8x8 array, 0 = empty, otherwise piece char
    private final char[][] board = new char[8][8];

    // Whose turn it is (true = white, false = black)
    private boolean whiteToMove = true;

    // En passant target square (the square a pawn can move to for en passant capture)
    // null if no en passant is possible
    private int[] enPassantSquare = null;

    // Time control (in milliseconds)
    private long whiteTimeMs;
    private long blackTimeMs;
    private long lastMoveTimestamp;
    private final long incrementMs; // Time added after each move
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private char winner = 0; // 'w' for white, 'b' for black, 0 for none
    private String gameOverReason = "";

    public ChessMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId);
    }

    public ChessMenu(int pContainerId) {
        super(GNMenuTypes.CHESS_MENU.get(), pContainerId);
        // Initialize board from starting FEN
        initBoardFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");

        // Initialize time control: 10 minutes each, 5 second increment
        long initialTimeMs = 5 * 60 * 1000; // 10 minutes
        this.incrementMs = 0 * 1000; // 5 seconds
        this.whiteTimeMs = initialTimeMs;
        this.blackTimeMs = initialTimeMs;
        this.lastMoveTimestamp = System.currentTimeMillis();
    }

    private void initBoardFromFen(String fenPosition) {
        String[] rows = fenPosition.split("/");
        for (int row = 0; row < 8; row++) {
            int col = 0;
            for (char c : rows[row].toCharArray()) {
                if (Character.isDigit(c)) {
                    int emptyCount = Character.getNumericValue(c);
                    for (int i = 0; i < emptyCount; i++) {
                        board[row][col++] = 0;
                    }
                } else {
                    board[row][col++] = c;
                }
            }
        }
    }

    /**
     * Get the piece at a specific board position.
     * @param row Row (0-7)
     * @param col Column (0-7)
     * @return Piece character (uppercase = white, lowercase = black) or 0 if empty
     */
    public char getPiece(int row, int col) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            return board[row][col];
        }
        return 0;
    }

    /**
     * Check if there is a piece at the specified position.
     */
    public boolean hasPiece(int row, int col) {
        return getPiece(row, col) != 0;
    }

    /**
     * Check if it's white's turn to move.
     */
    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    /**
     * Get white's remaining time in milliseconds.
     */
    public long getWhiteTimeMs() {
        if (!gameStarted || gameOver) {
            return whiteTimeMs;
        }
        if (whiteToMove) {
            long elapsed = System.currentTimeMillis() - lastMoveTimestamp;
            return Math.max(0, whiteTimeMs - elapsed);
        }
        return whiteTimeMs;
    }

    /**
     * Get black's remaining time in milliseconds.
     */
    public long getBlackTimeMs() {
        if (!gameStarted || gameOver) {
            return blackTimeMs;
        }
        if (!whiteToMove) {
            long elapsed = System.currentTimeMillis() - lastMoveTimestamp;
            return Math.max(0, blackTimeMs - elapsed);
        }
        return blackTimeMs;
    }

    /**
     * Check if the game is over (time ran out or checkmate).
     */
    public boolean isGameOver() {
        if (gameOver) return true;
        // Check for timeout
        if (gameStarted && (getWhiteTimeMs() <= 0 || getBlackTimeMs() <= 0)) {
            gameOver = true;
            if (getWhiteTimeMs() <= 0) {
                winner = 'b';
                gameOverReason = "White ran out of time";
            } else {
                winner = 'w';
                gameOverReason = "Black ran out of time";
            }
            return true;
        }
        return false;
    }

    /**
     * Get the winner: 'w' for white, 'b' for black, 0 for no winner yet.
     */
    public char getWinner() {
        return winner;
    }

    /**
     * Get the reason the game ended.
     */
    public String getGameOverReason() {
        return gameOverReason;
    }

    /**
     * Check if the current player is in check.
     */
    public boolean isInCheck() {
        return isKingInCheck(whiteToMove);
    }

    /**
     * Check if a piece is white (uppercase).
     */
    public boolean isWhite(char piece) {
        return piece != 0 && Character.isUpperCase(piece);
    }

    /**
     * Check if a piece is black (lowercase).
     */
    private boolean isBlack(char piece) {
        return piece != 0 && Character.isLowerCase(piece);
    }

    /**
     * Check if two pieces are enemies.
     */
    private boolean isEnemy(char piece1, char piece2) {
        if (piece1 == 0 || piece2 == 0) return false;
        return isWhite(piece1) != isWhite(piece2);
    }

    /**
     * Check if a square is valid on the board.
     */
    private boolean isValidSquare(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Find the king's position for the given color.
     * @param white true for white king, false for black king
     * @return int array [row, col] or null if not found
     */
    private int[] findKing(boolean white) {
        char king = white ? 'K' : 'k';
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == king) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    /**
     * Check if a square is attacked by any piece of the given color.
     * @param row Target row
     * @param col Target column
     * @param byWhite true to check if white attacks, false for black
     * @return true if the square is under attack
     */
    private boolean isSquareAttacked(int row, int col, boolean byWhite) {
        // Check for pawn attacks
        int pawnDir = byWhite ? 1 : -1; // White pawns attack upward (from higher row), black downward
        char enemyPawn = byWhite ? 'P' : 'p';
        for (int dc : new int[]{-1, 1}) {
            int pawnRow = row + pawnDir;
            int pawnCol = col + dc;
            if (isValidSquare(pawnRow, pawnCol) && board[pawnRow][pawnCol] == enemyPawn) {
                return true;
            }
        }

        // Check for knight attacks
        char enemyKnight = byWhite ? 'N' : 'n';
        int[][] knightOffsets = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        for (int[] offset : knightOffsets) {
            int knightRow = row + offset[0];
            int knightCol = col + offset[1];
            if (isValidSquare(knightRow, knightCol) && board[knightRow][knightCol] == enemyKnight) {
                return true;
            }
        }

        // Check for king attacks (for adjacent squares)
        char enemyKing = byWhite ? 'K' : 'k';
        int[][] kingOffsets = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        for (int[] offset : kingOffsets) {
            int kingRow = row + offset[0];
            int kingCol = col + offset[1];
            if (isValidSquare(kingRow, kingCol) && board[kingRow][kingCol] == enemyKing) {
                return true;
            }
        }

        // Check for sliding piece attacks (rook, bishop, queen)
        char enemyRook = byWhite ? 'R' : 'r';
        char enemyBishop = byWhite ? 'B' : 'b';
        char enemyQueen = byWhite ? 'Q' : 'q';

        // Rook/Queen directions (straight lines)
        int[][] rookDirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : rookDirs) {
            int r = row + dir[0];
            int c = col + dir[1];
            while (isValidSquare(r, c)) {
                char piece = board[r][c];
                if (piece != 0) {
                    if (piece == enemyRook || piece == enemyQueen) {
                        return true;
                    }
                    break; // Blocked by another piece
                }
                r += dir[0];
                c += dir[1];
            }
        }

        // Bishop/Queen directions (diagonals)
        int[][] bishopDirs = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] dir : bishopDirs) {
            int r = row + dir[0];
            int c = col + dir[1];
            while (isValidSquare(r, c)) {
                char piece = board[r][c];
                if (piece != 0) {
                    if (piece == enemyBishop || piece == enemyQueen) {
                        return true;
                    }
                    break; // Blocked by another piece
                }
                r += dir[0];
                c += dir[1];
            }
        }

        return false;
    }

    /**
     * Check if the king of the given color is in check.
     */
    private boolean isKingInCheck(boolean white) {
        int[] kingPos = findKing(white);
        if (kingPos == null) return false;
        return isSquareAttacked(kingPos[0], kingPos[1], !white);
    }

    /**
     * Test if a move would leave the king in check (used to filter legal moves).
     */
    private boolean wouldLeaveKingInCheck(int fromRow, int fromCol, int toRow, int toCol, boolean white) {
        // Make the move temporarily
        char movingPiece = board[fromRow][fromCol];
        char capturedPiece = board[toRow][toCol];

        // Handle en passant capture
        int enPassantCaptureRow = -1;
        int enPassantCaptureCol = -1;
        char enPassantCapturedPawn = 0;
        if (Character.toLowerCase(movingPiece) == 'p' && enPassantSquare != null &&
            toRow == enPassantSquare[0] && toCol == enPassantSquare[1]) {
            enPassantCaptureRow = white ? toRow + 1 : toRow - 1;
            enPassantCaptureCol = toCol;
            enPassantCapturedPawn = board[enPassantCaptureRow][enPassantCaptureCol];
            board[enPassantCaptureRow][enPassantCaptureCol] = 0;
        }

        board[fromRow][fromCol] = 0;
        board[toRow][toCol] = movingPiece;

        // Check if king is in check
        boolean inCheck = isKingInCheck(white);

        // Undo the move
        board[fromRow][fromCol] = movingPiece;
        board[toRow][toCol] = capturedPiece;

        // Undo en passant capture
        if (enPassantCapturedPawn != 0) {
            board[enPassantCaptureRow][enPassantCaptureCol] = enPassantCapturedPawn;
        }

        return inCheck;
    }

    /**
     * Get all possible moves for a piece at the given position.
     * Only returns legal moves (moves that don't leave the king in check).
     * @param row Row of the piece
     * @param col Column of the piece
     * @return List of int arrays [row, col] representing possible destination squares
     */
    public List<int[]> getPossibleMoves(int row, int col) {
        List<int[]> moves = new ArrayList<>();
        char piece = getPiece(row, col);
        if (piece == 0) return moves;

        char lowerPiece = Character.toLowerCase(piece);
        boolean white = isWhite(piece);

        switch (lowerPiece) {
            case 'p' -> addPawnMoves(moves, row, col, white);
            case 'n' -> addKnightMoves(moves, row, col, piece);
            case 'b' -> addBishopMoves(moves, row, col, piece);
            case 'r' -> addRookMoves(moves, row, col, piece);
            case 'q' -> addQueenMoves(moves, row, col, piece);
            case 'k' -> addKingMoves(moves, row, col, piece);
        }

        // Filter out moves that would leave the king in check
        moves.removeIf(move -> wouldLeaveKingInCheck(row, col, move[0], move[1], white));

        return moves;
    }

    /**
     * Check if the current player has any legal moves.
     */
    private boolean hasLegalMoves(boolean white) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char piece = board[row][col];
                if (piece != 0 && isWhite(piece) == white) {
                    List<int[]> moves = getPossibleMoves(row, col);
                    if (!moves.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check for checkmate or stalemate after a move.
     */
    private void checkGameEndConditions() {
        boolean currentPlayerWhite = whiteToMove;
        boolean inCheck = isKingInCheck(currentPlayerWhite);
        boolean hasLegal = hasLegalMoves(currentPlayerWhite);

        if (!hasLegal) {
            gameOver = true;
            if (inCheck) {
                // Checkmate - the player who just moved wins
                winner = currentPlayerWhite ? 'b' : 'w';
                gameOverReason = (currentPlayerWhite ? "White" : "Black") + " is checkmated";
            } else {
                // Stalemate - draw
                winner = 0;
                gameOverReason = "Stalemate";
            }
        }
    }

    private void addPawnMoves(List<int[]> moves, int row, int col, boolean white) {
        int direction = white ? -1 : 1; // White moves up (decreasing row), black moves down
        int startRow = white ? 6 : 1;

        // Forward move
        int newRow = row + direction;
        if (isValidSquare(newRow, col) && getPiece(newRow, col) == 0) {
            moves.add(new int[]{newRow, col});

            // Double move from starting position
            if (row == startRow) {
                int doubleRow = row + 2 * direction;
                if (getPiece(doubleRow, col) == 0) {
                    moves.add(new int[]{doubleRow, col});
                }
            }
        }

        // Captures (including en passant)
        for (int dc : new int[]{-1, 1}) {
            int newCol = col + dc;
            if (isValidSquare(newRow, newCol)) {
                char target = getPiece(newRow, newCol);
                // Normal capture
                if (target != 0 && isWhite(target) != white) {
                    moves.add(new int[]{newRow, newCol});
                }
                // En passant capture
                else if (enPassantSquare != null && enPassantSquare[0] == newRow && enPassantSquare[1] == newCol) {
                    moves.add(new int[]{newRow, newCol});
                }
            }
        }
    }

    private void addKnightMoves(List<int[]> moves, int row, int col, char piece) {
        int[][] offsets = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        for (int[] offset : offsets) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];
            if (isValidSquare(newRow, newCol)) {
                char target = getPiece(newRow, newCol);
                if (target == 0 || isEnemy(piece, target)) {
                    moves.add(new int[]{newRow, newCol});
                }
            }
        }
    }

    private void addBishopMoves(List<int[]> moves, int row, int col, char piece) {
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        addSlidingMoves(moves, row, col, piece, directions);
    }

    private void addRookMoves(List<int[]> moves, int row, int col, char piece) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        addSlidingMoves(moves, row, col, piece, directions);
    }

    private void addQueenMoves(List<int[]> moves, int row, int col, char piece) {
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        addSlidingMoves(moves, row, col, piece, directions);
    }

    private void addSlidingMoves(List<int[]> moves, int row, int col, char piece, int[][] directions) {
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            while (isValidSquare(newRow, newCol)) {
                char target = getPiece(newRow, newCol);
                if (target == 0) {
                    moves.add(new int[]{newRow, newCol});
                } else {
                    if (isEnemy(piece, target)) {
                        moves.add(new int[]{newRow, newCol});
                    }
                    break;
                }
                newRow += dir[0];
                newCol += dir[1];
            }
        }
    }

    private void addKingMoves(List<int[]> moves, int row, int col, char piece) {
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isValidSquare(newRow, newCol)) {
                char target = getPiece(newRow, newCol);
                if (target == 0 || isEnemy(piece, target)) {
                    moves.add(new int[]{newRow, newCol});
                }
            }
        }
    }

    /**
     * Move a piece from one position to another (no validation).
     * @param fromRow Source row
     * @param fromCol Source column
     * @param toRow Destination row
     * @param toCol Destination column
     */
    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow >= 0 && fromRow < 8 && fromCol >= 0 && fromCol < 8 &&
            toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {
            char piece = board[fromRow][fromCol];
            char lowerPiece = Character.toLowerCase(piece);
            boolean white = isWhite(piece);

            // Handle en passant capture
            if (lowerPiece == 'p' && enPassantSquare != null &&
                toRow == enPassantSquare[0] && toCol == enPassantSquare[1]) {
                // Remove the captured pawn (it's on the same row as the capturing pawn, not the destination)
                int capturedPawnRow = white ? toRow + 1 : toRow - 1;
                board[capturedPawnRow][toCol] = 0;
            }

            // Clear en passant square before potentially setting a new one
            enPassantSquare = null;

            // Set en passant square if pawn moves two squares
            if (lowerPiece == 'p' && Math.abs(toRow - fromRow) == 2) {
                // En passant target is the square the pawn passed through
                int enPassantRow = (fromRow + toRow) / 2;
                enPassantSquare = new int[]{enPassantRow, fromCol};
            }

            // Move the piece
            board[fromRow][fromCol] = 0;
            board[toRow][toCol] = piece;

            // Handle time control
            long currentTime = System.currentTimeMillis();
            if (gameStarted) {
                long elapsed = currentTime - lastMoveTimestamp;
                if (whiteToMove) {
                    whiteTimeMs = Math.max(0, whiteTimeMs - elapsed) + incrementMs;
                } else {
                    blackTimeMs = Math.max(0, blackTimeMs - elapsed) + incrementMs;
                }
            } else {
                gameStarted = true;
            }
            lastMoveTimestamp = currentTime;

            // Toggle turn
            whiteToMove = !whiteToMove;

            // Check for checkmate or stalemate
            checkGameEndConditions();
        }
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
