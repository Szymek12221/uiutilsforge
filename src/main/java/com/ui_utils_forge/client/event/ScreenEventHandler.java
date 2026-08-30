package com.ui_utils_forge.client.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.ui_utils_forge.UIUtilsForge;
import com.ui_utils_forge.client.util.SharedVariables;

@Mod.EventBusSubscriber(modid = UIUtilsForge.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ScreenEventHandler {
    
    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (event.getScreen() != null && SharedVariables.enabled) {
            // Screen opened
        }
    }
    
    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.Closing event) {
        if (event.getScreen() != null && SharedVariables.enabled) {
            // Screen closed
        }
    }
}
