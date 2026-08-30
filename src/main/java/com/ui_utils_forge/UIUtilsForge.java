package com.ui_utils_forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafxmod.FXModLanguageProvider;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.IModEventBus;
import net.minecraftforge.fml.javafxmod.FXModLanguageProvider;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod("uiutilsforge")
public class UIUtilsForge {

    public static final String MODID = "uiutilsforge";
    private static final Logger LOGGER = LogManager.getLogger();

    public UIUtilsForge(IModEventBus modEventBus) {
        LOGGER.info("UI Utils Forge initializing...");
        
        // Register mod setup listeners
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("UI Utils Forge common setup");
    }

    @OnlyIn(Dist.CLIENT)
    private void clientSetup(FMLClientSetupEvent event) {
        LOGGER.info("UI Utils Forge client setup");
    }
}
