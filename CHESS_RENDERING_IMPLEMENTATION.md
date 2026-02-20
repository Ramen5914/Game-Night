# Chess Piece Rendering Implementation

## Overview
This implementation adds 3D rendering of chess pieces on the chess board block using Minecraft JSON models and textures.

## What Was Implemented

### 1. Chess Piece Models (JSON)
Created 12 JSON model files in `src/main/resources/assets/game_night/models/item/chess/`:
- **White pieces**: white_king.json, white_queen.json, white_bishop.json, white_knight.json, white_rook.json, white_pawn.json
- **Black pieces**: black_king.json, black_queen.json, black_bishop.json, black_knight.json, black_rook.json, black_pawn.json

Each model references its corresponding texture using the standard Minecraft item/generated parent model.

### 2. Chess Piece Textures (PNG)
Extracted 12 individual 16x16 pixel textures from the existing `pieces.png` sprite sheet:
- Located in: `src/main/resources/assets/game_night/textures/item/chess/`
- Each piece has its own PNG file matching the model names

### 3. ChessBlockEntityRenderer Updates
Enhanced the renderer with the following functionality:

#### FEN Parsing
- Parses the FEN (Forsyth-Edwards Notation) string from the ChessBlockEntity
- Converts FEN notation to an 8x8 board array
- Handles empty squares and piece positions correctly

#### Piece Rendering
- Maps FEN piece characters to model locations:
  - Uppercase letters (P, N, B, R, Q, K) → White pieces
  - Lowercase letters (p, n, b, r, q, k) → Black pieces
- Loads BakedModel instances using ModelResourceLocation
- Positions pieces on the correct squares based on FEN coordinates

#### 3D Positioning
- Centers the board rendering within the chess block boundaries
- Calculates square positions (8x8 grid)
- Positions pieces slightly above the board surface (13/16 blocks high)
- Scales pieces to 80% of square size for proper spacing
- Rotates pieces for correct orientation

### 4. Technical Details

#### Coordinate System
- **FEN Format**: Ranks 8-1 (top to bottom), Files a-h (left to right)
- **Array Mapping**: Row 0 = Rank 8 (black's side), Row 7 = Rank 1 (white's side)
- **Rendering**: Pieces are positioned based on their row/col indices

#### Board Dimensions
- Chess block size: 12x12 pixels (centered in 16x16 block)
- Offset: 2/16 blocks on X and Z axes
- Square size: 12/16 ÷ 8 = 0.1875 blocks per square
- Board height: 13/16 blocks

#### Model Loading
Uses `ModelResourceLocation` with "inventory" variant to load the piece models from the model manager.

## How It Works

1. **Game State**: The ChessBlockEntity stores the board state as a FEN string
2. **Rendering**: Each frame, the renderer:
   - Retrieves the FEN string
   - Parses it into a board array
   - Iterates through each square
   - Renders the appropriate piece model at the calculated position
3. **Display**: Players see the current board state with all pieces positioned correctly in 3D

## Testing
To test the implementation:
1. Build the project: `./gradlew build`
2. Run the game client
3. Place a chess block
4. Right-click to open the chess interface
5. The pieces should render on the 3D board based on the current game state

## Future Enhancements
Potential improvements:
- Add piece animations for moves
- Highlight selected pieces
- Show move indicators
- Add captured piece rendering
- Improve lighting/shading for better visual distinction

