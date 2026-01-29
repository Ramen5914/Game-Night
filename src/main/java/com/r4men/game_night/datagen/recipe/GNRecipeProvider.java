package com.r4men.game_night.datagen.recipe;

import com.r4men.game_night.GameNight;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GNRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public static final int BUCKET = FluidType.BUCKET_VOLUME;
    public static final int BOTTLE = BUCKET / 4;
    public static final int BLOCK = 1296;
    public static final int INGOT = BLOCK / 9;
    public static final int NUGGET = INGOT / 9;

    public GNRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public @NotNull String getName() {
        return GameNight.NAME + "'s Recipes";
    }
}
