package com.r4men.game_night;

import com.r4men.game_night.block.entity.GNBlockEntities;
import com.r4men.game_night.client.renderer.ChessBlockEntityRenderer;
import com.r4men.game_night.gui.GNMenuTypes;
import com.r4men.game_night.gui.chess.screen.ChessScreen;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
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
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(GNMenuTypes.CHESS_MENU.get(), ChessScreen::new);
    }

    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GNBlockEntities.CHESS_BE.get(), ChessBlockEntityRenderer::create);
//        event.registerBlockEntityRenderer(GNBlockEntities.MONOPOLY_BE.get(), MonopolyBlockEntityRenderer::new);
    }
}
