package com.ui_utils_forge.client.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.ui_utils_forge.UIUtilsForge;
import com.ui_utils_forge.client.MainClientForge;
import com.ui_utils_forge.client.util.SharedVariables;

import java.util.regex.Pattern;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    
    @Unique
    private EditBox addressField;
    
    @Inject(at = @At("TAIL"), method = "init")
    public void init(CallbackInfo ci) {
        if (SharedVariables.enabled) {
            MainClientForge.createWidgets(MainClientForge.mc, (AbstractContainerScreen<?>) (Object) this);
        }
    }
    
    @Inject(at = @At("TAIL"), method = "render")
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (SharedVariables.enabled) {
            MainClientForge.createText(MainClientForge.mc, guiGraphics, MainClientForge.mc.font);
        }
    }
}
