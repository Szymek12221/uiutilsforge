package com.ui_utils_forge.client.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.ui_utils_forge.UIUtilsForge;
import com.ui_utils_forge.client.util.SharedVariables;

@Mod.EventBusSubscriber(modid = UIUtilsForge.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeyInputHandler {
    
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        // Key input handling
    }
}
