package com.wonginnovations.oldresearch.common.config;

import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = OldResearch.MODID)
@Mod.EventBusSubscriber(modid = OldResearch.MODID)
public class OldConfig {
    public static String[] aspectBlackList = new String[0];
    public static double researchDifficultyMultiplier = 0.5D;
    public static double aspectObtainMultiplier = 0.5D;
    public static int notificationDelay = 2000;
    public static int notificationMax = 10;
    public static int aspectTotalCap = 10000;
    public static boolean instantScans = false;

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(OldResearch.MODID)) {
            ConfigManager.sync(OldResearch.MODID, Config.Type.INSTANCE);
        }
    }
}
