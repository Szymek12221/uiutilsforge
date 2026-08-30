package com.ui_utils_forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(UIUtilsForge.MODID)
public class UIUtilsForge {
    public static final String MODID = "ui_utils_forge";
    public static final String NAME = "UI Utils";
    public static final String VERSION = "1.0.0";
    
    public static final Logger LOGGER = LoggerFactory.getLogger("ui_utils_forge");
    
    public UIUtilsForge() {
        // Forge mod constructor
    }
}
