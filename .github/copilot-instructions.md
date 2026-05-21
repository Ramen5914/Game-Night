# Game Night – Copilot Instructions

## Project Overview

NeoForge mod for Minecraft 1.21.4 that adds interactive board games (Chess, Monopoly) as placeable blocks with custom GUIs and a fully custom rendering pipeline for 3D chess pieces.

- **Mod ID**: `game_night` | **Group**: `com.r4men.game_night`
- **NeoForge**: 26.1.2.43-beta | **Java**: 25 | **Minecraft**: 21.6.2

## Build Commands

```bash
./gradlew build              # Compile and package the mod JAR
./gradlew client             # Launch Minecraft client in dev mode
./gradlew server             # Launch dedicated server in dev mode
./gradlew runData            # Run data generators (blockstates, loot tables, lang, recipes)
./gradlew gameTestServer     # Run game tests
./gradlew generateModMetadata  # Process metadata templates (neoforge.mods.toml)
```

There are no unit test tasks beyond `gameTestServer`. No lint tasks exist.

## Architecture

### Registration Pattern
All registries use NeoForge's `DeferredRegister` and are declared as static `public static final` fields in `GN*` registry classes. Each registry class exposes a `register(IEventBus)` method called from `GameNight` constructor.

```
GameNight (constructor)
  ├── GNItems.register(modEventBus)
  ├── GNBlocks.register(modEventBus)      ← also auto-registers BlockItems via GNItems
  ├── GNBlockEntities.register(modEventBus)
  ├── GNMenuTypes.register(modEventBus)
  └── GNTabs.register(modEventBus)
```

Adding a new block requires: `GNBlocks` entry → the `registerBlock()` helper auto-creates the `BlockItem` in `GNItems`.

### Event Handling
- Mod bus events: subscribe via `modEventBus.addListener(this::method)` in constructor, or use `@EventBusSubscriber(modid = GameNight.ID, bus = Bus.MOD)` + `@SubscribeEvent`.
- Forge/game bus events: use `@EventBusSubscriber(modid = GameNight.ID)` (bus defaults to `FORGE`).
- Client-only events: annotate with `value = Dist.CLIENT`.

### Custom Rendering Pipeline (Chess)
The chess board uses a fully custom rendering stack:
- **`ChessPieceGeometryLoader`** – custom `IGeometryLoader` registered on the mod bus; loads chess piece model geometry.
- **`ChessPieceBakedModel`** – produces `BakedModel` instances with per-piece transforms.
- **`GNShaders`** – registers custom GLSL shaders via `RegisterShadersEvent`; shader sources live in `src/main/resources/assets/game_night/shaders/`.
- **`GNRenderTypes`** – defines custom `RenderType` instances that use those shaders.
- **`ChessBlockEntityRenderer`** (in `block/entity/renderer/`) – the BER that drives piece placement and shader usage.

All client rendering is wired up in `GameNightClient`.

### GUI Layer
- **Menus** (server + client): `src/main/java/.../gui/chess/menu/` — extend `AbstractContainerMenu`.
- **Screens** (client only): `src/main/java/.../gui/chess/screen/` — extend `AbstractContainerScreen`.
- Menu types are registered in `GNMenuTypes`; screens are bound in `GameNightClient`.

### Config
`GNConfig` declares two specs:
- `SERVER_SPEC` – registered as `ModConfig.Type.SERVER`.
- `CLIENT_SPEC` – registered as `ModConfig.Type.CLIENT` (in `GameNightClient`).

Color config values use `#aarrggbb` hex strings with the `validateHexColor` validator.

## Game Implementations

### Chess

**State** is stored in `ChessBlockEntity` as:
- `fen` – full board state in standard FEN notation (persisted via NBT, synced via `ClientboundBlockEntityDataPacket`)
- `whiteTimeMs` / `blackTimeMs` / `lastMoveTimestamp` / `incrementMs` – time control
- `gameStarted`, `gameOver`, `winner`, `gameOverReason`, `isSetup`

**Game logic lives in `ChessMenu`** (server-side container). It contains the full chess engine:
- FEN parsing → `char[][]` 8×8 board (uppercase = white, lowercase = black)
- Per-piece legal move generation (pawns with en passant, castling, sliding pieces)
- Check detection by simulating attacks from every piece type against the king
- Move filtering: each candidate move is simulated to verify the king isn't left in check
- Checkmate/stalemate detection, pawn promotion (pending state before piece selection), time control

**`ChessScreen`** (client-only) renders the GUI:
- 24×24 px tiles; colors pulled from `GNConfig` (`WHITE_SQUARE_COLOR` / `BLACK_SQUARE_COLOR`)
- Texture atlas blit for pieces; click-to-select + click-to-move or drag-to-move
- Highlights: green = selected square, grey dots = valid move targets, red = capture squares
- Side panels: per-player clocks (yellow = active), "Check!" indicator, game-over overlay, pawn promotion modal (Q/R/B/N)

**`ChessSetupMenu` / `ChessSetupScreen`** are currently near-identical duplicates of their non-setup counterparts — the two classes are a known refactoring opportunity.

**3D piece rendering** (`GNChessBlockEntityRenderer`):
- Parses FEN each frame to build the 8×8 piece map
- Looks up `BakedModel` by key `game_night:chess/pieces/{white|black}/{piece}` (lazy-cached)
- Two-pass render per piece:
  1. **Outline pass** – model scaled 1.10×, vertex winding reversed (back-face becomes front-face), rendered with `CHESS_OUTLINE` render type / custom shader → produces silhouette outline
  2. **Normal pass** – standard `cutoutMipped` render
- Black pieces rotated 180° around Y-axis

**`/gameNight chess`** command opens a standalone `ChessMenu` not linked to any block (for testing/playing without placing a board).

### Monopoly

Currently a **skeleton only**. `MonopolyBlock` and `MonopolyBlockEntity` exist but `MonopolyBlockEntity` has no fields or logic. No menu, screen, renderer, or commands are implemented yet. Use the Chess implementation as the reference pattern when building it out.

## Key Conventions

- **Registry class naming**: `GN<Thing>` (e.g., `GNBlocks`, `GNItems`, `GNBlockEntities`, `GNMenuTypes`).
- **Block + BlockItem**: always use `GNBlocks.registerBlock()` private helper — it auto-registers the `BlockItem` into `GNItems.ITEMS`; never register block items separately.
- **Commented-out stubs**: The codebase contains many commented-out sections (fluids, recipes, tabs, item properties) that serve as templates for future features — do not delete them.
- **`noOcclusion()`**: All game board blocks use `BlockBehaviour.Properties.of().noOcclusion()` because they are non-full blocks.
- **Access Transformer**: `META-INF/accesstransformer.cfg` is configured; add AT entries there when private/protected Minecraft fields need access.
- **Mixins**: mixin config is `gn.mixins.json`; mixin classes belong in a `mixin` package.
- **Assets namespace**: all assets under `assets/game_night/`; shader files go in `assets/game_night/shaders/`.
- **Data generation**: datagen classes live in `src/main/java/.../datagen/`; run `./gradlew runData` to regenerate.
