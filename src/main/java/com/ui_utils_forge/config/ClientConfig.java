package com.ui_utils_forge.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = "uiutilsforge", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientConfig {
    static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_DEBUG_MODE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_PACKET_LOG;

    static {
        BUILDER.push("client");

        ENABLE_DEBUG_MODE = BUILDER
                .comment("Enable debug mode for UI Utils")
                .define("enableDebugMode", true);

        ENABLE_PACKET_LOG = BUILDER
                .comment("Enable packet logging")
                .define("enablePacketLog", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        // Config loaded
    }
}
