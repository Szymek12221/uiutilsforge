package com.ui_utils_forge.client.mixin;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateSignPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.ui_utils_forge.client.util.SharedVariables;

@Mixin(targets = "net.minecraft.client.network.ClientPacketListener")
public class ClientPacketListenerMixin {
    
    @Inject(at = @At("HEAD"), method = "send", cancellable = true)
    public void send(Packet<?> packet, CallbackInfo ci) {
        if (!SharedVariables.sendUIPackets && (packet instanceof ServerboundContainerClickPacket || packet instanceof ServerboundButtonClickPacket)) {
            ci.cancel();
            return;
        }
        
        if (SharedVariables.delayUIPackets && (packet instanceof ServerboundContainerClickPacket || packet instanceof ServerboundButtonClickPacket)) {
            SharedVariables.delayedUIPackets.add(packet);
            ci.cancel();
        }
    }
}
