package com.r4men.game_night;

import com.mojang.logging.LogUtils;
import com.r4men.game_night.block.GNBlocks;
import com.r4men.game_night.block.GNBlockEntities;
import com.r4men.game_night.gui.GNMenuTypes;
import com.r4men.game_night.item.GNItems;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(GameNight.ID)
public class GameNight {
    public static final String ID = "game_night";
    public static final String NAME = "Game Night";

    public static final Logger LOGGER = LogUtils.getLogger();

    public GameNight(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        GNTabs.register(modEventBus);

        GNBlocks.register(modEventBus);
        GNItems.register(modEventBus);

        GNBlockEntities.register(modEventBus);

        GNMenuTypes.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, GNConfig.SERVER_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("{} initializing!", NAME);
    }

    public static Identifier getIdentifier(String regName) {
        return Identifier.fromNamespaceAndPath(ID, regName);
    }
}
