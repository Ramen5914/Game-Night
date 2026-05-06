package com.r4men.game_night;

import com.r4men.game_night.block.entity.GNBlockEntities;
import com.r4men.game_night.block.entity.renderer.GNChessBlockEntityRenderer;
import com.r4men.game_night.client.render.chess.ChessPieceGeometryLoader;
import com.r4men.game_night.client.render.chess.GNShaders;
import com.r4men.game_night.gui.GNMenuTypes;
import com.r4men.game_night.gui.chess.screen.ChessScreen;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
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
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
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
        modEventBus.addListener(GameNightClient::registerGeometryLoaders);
        modEventBus.addListener(GameNightClient::registerShaders);
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(GNMenuTypes.CHESS_MENU.get(), ChessScreen::new);
    }

    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GNBlockEntities.CHESS_BE.get(), GNChessBlockEntityRenderer::new);
    }

    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        String[] pieces = {"b", "k", "n", "p", "q", "r"};
        String[] colors = {"black", "white"};

        for (String color : colors) {
            for (String piece : pieces) {
                event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess/pieces/" + color + "/" + piece), "standalone"));
            }
        }
    }

    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess_piece"), ChessPieceGeometryLoader.INSTANCE);
    }

    public static void registerShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(
                new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess_outline"),
                    DefaultVertexFormat.POSITION_COLOR
                ),
                shader -> GNShaders.CHESS_OUTLINE_SHADER = shader
            );
            event.registerShader(
                new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(GameNight.ID, "chess_edge"),
                    DefaultVertexFormat.POSITION_TEX
                ),
                shader -> GNShaders.CHESS_EDGE_SHADER = shader
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to register chess shaders", e);
        }
    }
}
