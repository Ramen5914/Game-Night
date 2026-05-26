package com.r4men.game_night.client.models.chess;

import com.r4men.game_night.GameNight;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

public class ChessPieces {
    // Pawns
    public static final StandaloneModelKey<QuadCollection> WHITE_PAWN = new StandaloneModelKey<>(
            new ModelDebugName() {
                @Override
                public String debugName() {
                    return GameNight.ID + ":chess_white_pawn";
                }
            }
    );

    public static final StandaloneModelKey<QuadCollection> BLACK_PAWN = new StandaloneModelKey<>(
            new ModelDebugName() {
                @Override
                public String debugName() {
                    return GameNight.ID + ":chess_black_pawn";
                }
            }
    );

    // Rooks
    public static final StandaloneModelKey<QuadCollection> WHITE_ROOK = new StandaloneModelKey<>(
            new ModelDebugName() {
                @Override
                public String debugName() {
                    return GameNight.ID + ":chess_white_rook";
                }
            }
    );

    public static final StandaloneModelKey<QuadCollection> BLACK_ROOK = new StandaloneModelKey<>(
            new ModelDebugName() {
                @Override
                public String debugName() {
                    return GameNight.ID + ":chess_black_rook";
                }
            }
    );

    // Knights
    public static final StandaloneModelKey<QuadCollection> WHITE_KNIGHT = new StandaloneModelKey<>(
            new ModelDebugName() {
                @Override
                public String debugName() {
                    return GameNight.ID + ":chess_white_knight";
                }
            }
    );

    public static final StandaloneModelKey<QuadCollection> BLACK_KNIGHT = new StandaloneModelKey<>(
            new ModelDebugName() {
                @Override
                public String debugName() {
                    return GameNight.ID + ":chess_black_knight";
                }
            }
    );

    // Bishops
    public static final StandaloneModelKey<QuadCollection> WHITE_BISHOP = new StandaloneModelKey<>(
            new ModelDebugName() {
                @Override
                public String debugName() {
                    return GameNight.ID + ":chess_white_bishop";
                }
            }
    );

    public static final StandaloneModelKey<QuadCollection> BLACK_BISHOP = new StandaloneModelKey<>(
            new ModelDebugName() {
                @Override
                public String debugName() {
                    return GameNight.ID + ":chess_black_bishop";
                }
            }
    );

    // Queens
    public static final StandaloneModelKey<QuadCollection> WHITE_QUEEN = new StandaloneModelKey<>(
            new ModelDebugName() {
                @Override
                public String debugName() {
                    return GameNight.ID + ":chess_white_queen";
                }
            }
    );

    public static final StandaloneModelKey<QuadCollection> BLACK_QUEEN = new StandaloneModelKey<>(
            new ModelDebugName() {
                @Override
                public String debugName() {
                    return GameNight.ID + ":chess_black_queen";
                }
            }
    );

    // Kings
    public static final StandaloneModelKey<QuadCollection> WHITE_KING = new StandaloneModelKey<>(
            new ModelDebugName() {
                @Override
                public String debugName() {
                    return GameNight.ID + ":chess_white_king";
                }
            }
    );

    public static final StandaloneModelKey<QuadCollection> BLACK_KING = new StandaloneModelKey<>(
            new ModelDebugName() {
                @Override
                public String debugName() {
                    return GameNight.ID + ":chess_black_king";
                }
            }
    );
}