package com.r4men.game_night;

import com.r4men.game_night.block.entity.GNBlockEntities;
import com.r4men.game_night.block.entity.renderer.GNChessBlockEntityRenderer;
import com.r4men.game_night.gui.GNMenuTypes;
import com.r4men.game_night.gui.screen.ChessScreen;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = GameNight.ID, dist = Dist.CLIENT)
public class GameNightClient {
    public GameNightClient(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modContainer.registerConfig(ModConfig.Type.CLIENT, GNConfig.CLIENT_SPEC);

        // Register mod bus events
        modEventBus.addListener(GameNightClient::registerScreens);
        modEventBus.addListener(GameNightClient::registerBER);
        modEventBus.addListener(GameNightClient::registerAdditionalModels);
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(GNMenuTypes.CHESS_MENU.get(), ChessScreen::new);
    }

    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GNBlockEntities.CHESS_BE.get(), GNChessBlockEntityRenderer::new);
    }

    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        // Register chess piece models
        String[] pieces = {"white_pawn", "white_knight", "white_bishop", "white_rook", "white_queen", "white_king",
                          "black_pawn", "black_knight", "black_bishop", "black_rook", "black_queen", "black_king"};

        for (String piece : pieces) {
            event.register(ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess/" + piece)
            ));
        }
    }
}
