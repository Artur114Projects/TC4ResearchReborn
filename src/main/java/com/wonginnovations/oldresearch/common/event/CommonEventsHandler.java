package com.wonginnovations.oldresearch.common.event;

import com.wonginnovations.oldresearch.common.event.managers.LootTableLoadManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CommonEventsHandler {
    public static final LootTableLoadManager LOOT_TABLE_LOAD_MANAGER = new LootTableLoadManager();

    @SubscribeEvent
    public static void LootLoadEvent(LootTableLoadEvent e) {
        LOOT_TABLE_LOAD_MANAGER.lootTableLoadEvent(e);
    }
}
