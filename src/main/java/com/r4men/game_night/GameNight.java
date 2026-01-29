package com.r4men.game_night;

import com.mojang.logging.LogUtils;
import com.r4men.game_night.gui.GNMenuTypes;
import com.r4men.game_night.item.GNItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.slf4j.Logger;

@Mod(GameNight.ID)
public class GameNight {
    public static final String ID = "game_night";
    public static final String NAME = "Game Night";

    public static final Logger LOGGER = LogUtils.getLogger();

    public GameNight(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

//        CMTabs.register(modEventBus);
//
        GNItems.register(modEventBus);
//        CMBlocks.register(modEventBus);
//        CMFluids.register(modEventBus);
//        CMFluidTypes.register(modEventBus);
//
//        CMDataComponentTypes.register(modEventBus);
//
//        CMRecipes.register(modEventBus);
//
        GNMenuTypes.register(modEventBus);

        GNTabs.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, GNConfig.SERVER_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("{} initializing!", NAME);
    }

    @EventBusSubscriber(modid = GameNight.ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
//            CMItemProperties.addCustomItemProperties();
        }

        @SubscribeEvent
        public static void onClientExtensions(RegisterClientExtensionsEvent event) {
//            for (var fluid : CMFluidTypes.FLUID_TYPES.getEntries().stream().map(Holder::value).toList()) {
//                event.registerFluidType(((BaseFluidType) fluid).getClientFluidTypeExtensions(), fluid);
//            }
        }
    }
}
