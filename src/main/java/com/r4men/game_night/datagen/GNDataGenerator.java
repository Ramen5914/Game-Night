package com.r4men.game_night.datagen;

import com.r4men.game_night.GameNight;
import com.r4men.game_night.datagen.lang.GNEnUsLanguageProvider;
import com.r4men.game_night.datagen.recipe.GNRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = GameNight.ID)
public class GNDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new GNRecipeProvider(packOutput, lookupProvider));

        generator.addProvider(event.includeClient(), new GNEnUsLanguageProvider(packOutput));
    }
}
