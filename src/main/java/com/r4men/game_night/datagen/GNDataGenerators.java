package com.r4men.game_night.datagen;

import com.r4men.game_night.GameNight;
import com.r4men.game_night.datagen.models.GNModelProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = GameNight.ID)
public class GNDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(GNModelProvider::new);
    }
}
