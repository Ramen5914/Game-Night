package com.r4men.game_night;

import com.r4men.game_night.block.entity.GNBlockEntities;
import com.r4men.game_night.block.entity.renderer.GNChessBlockEntityRenderer;
import com.r4men.game_night.gui.GNMenuTypes;
import com.r4men.game_night.gui.screen.ChessScreen;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = GameNight.ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = GameNight.ID, value = Dist.CLIENT)
public class GameNightClient {
    public GameNightClient(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        modContainer.registerConfig(ModConfig.Type.CLIENT, GNConfig.CLIENT_SPEC);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(GNMenuTypes.CHESS_MENU.get(), ChessScreen::new);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GNBlockEntities.CHESS_BE.get(), GNChessBlockEntityRenderer::new);
    }
}
