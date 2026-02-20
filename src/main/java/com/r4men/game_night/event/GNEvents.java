package com.r4men.game_night.event;

import com.r4men.game_night.GameNight;
import com.r4men.game_night.command.ChessCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.server.command.ConfigCommand;

@EventBusSubscriber(modid = GameNight.ID)
public class GNEvents {
    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new ChessCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        String key = GameNight.ID + ".homepos";

        event.getEntity().getPersistentData().putIntArray(key,
                event.getOriginal().getPersistentData().getIntArray(key));
    }
}
