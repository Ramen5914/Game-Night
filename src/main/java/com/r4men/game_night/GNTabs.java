package com.r4men.game_night;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class GNTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, GameNight.ID);

    public static final Supplier<CreativeModeTab> GAME_NIGHT_TAB = CREATIVE_MODE_TABS.register(
            "gn_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.game_night.gn_tab"))
                    .icon(() -> new ItemStack(Items.DIAMOND))
                    .displayItems((params, output) -> {
                        output.accept(Items.DIAMOND);
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
