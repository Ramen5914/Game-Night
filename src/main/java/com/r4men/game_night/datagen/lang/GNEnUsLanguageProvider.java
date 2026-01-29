package com.r4men.game_night.datagen.lang;

import com.r4men.game_night.GameNight;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

public class GNEnUsLanguageProvider extends LanguageProvider {
    public GNEnUsLanguageProvider(PackOutput output) {
        super(output, GameNight.ID, "en_us");
    }

    @Override
    public @NotNull String getName() {
        return GameNight.NAME + "'s English (US) Translations";
    }

    @Override
    protected void addTranslations() {
        // Creative Mode Tabs
        add("itemGroup.game_night.gn_tab", GameNight.NAME);
    }
}
