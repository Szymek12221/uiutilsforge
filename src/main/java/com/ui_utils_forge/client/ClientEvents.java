package com.ui_utils_forge.client;

import com.ui_utils_forge.UIUtilsForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = UIUtilsForge.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Open event) {
        // GUI opened - ready for UI debugging
        if (event.getScreen() != null) {
            LOGGER.debug("Screen opened: {}", event.getScreen().getClass().getSimpleName());
        }
    }

    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.Close event) {
        // GUI closed
        if (event.getScreen() != null) {
            LOGGER.debug("Screen closed: {}", event.getScreen().getClass().getSimpleName());
        }
    }
}
