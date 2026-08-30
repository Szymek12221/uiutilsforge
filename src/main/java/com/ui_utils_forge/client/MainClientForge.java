package com.ui_utils_forge.client;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import com.ui_utils_forge.UIUtilsForge;
import com.ui_utils_forge.client.util.SharedVariables;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

@Mod.EventBusSubscriber(modid = UIUtilsForge.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MainClientForge {
    public static Font monospace;
    public static Color darkWhite;
    
    public static Minecraft mc = Minecraft.getInstance();
    
    static {
        if (!Minecraft.IS_RUNNING_ON_MAC) {
            System.setProperty("java.awt.headless", "false");
            monospace = new Font(Font.MONOSPACED, Font.PLAIN, 10);
            darkWhite = new Color(220, 220, 220);
        }
    }
    
    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof AbstractContainerScreen screen && SharedVariables.enabled) {
            createWidgets(mc, screen);
        }
    }
    
    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (event.getScreen() instanceof AbstractContainerScreen screen && SharedVariables.enabled) {
            createText(mc, event.getGuiGraphics(), mc.font);
        }
    }
    
    @SuppressWarnings("all")
    public static void createText(Minecraft mc, GuiGraphics context, Font textRenderer) {
        if (mc.player != null && mc.player.containerMenu != null) {
            context.drawString(textRenderer, "Sync Id: " + mc.player.containerMenu.containerId, 200, 5, Color.WHITE.getRGB(), false);
            context.drawString(textRenderer, "Revision: " + mc.player.containerMenu.getStateId(), 200, 35, Color.WHITE.getRGB(), false);
        }
    }
    
    public static void createWidgets(Minecraft mc, Screen screen) {
        // Close without packet button
        screen.addRenderableWidget(new Button(5, 5, 115, 20, Component.literal("Close without packet"), (button) -> {
            mc.setScreen(null);
        }));
        
        // De-sync button
        screen.addRenderableWidget(new Button(5, 35, 115, 20, Component.literal("De-sync"), (button) -> {
            if (mc.getConnection() != null && mc.player != null) {
                mc.getConnection().send(new ServerboundContainerClosePacket(mc.player.containerMenu.containerId));
            } else {
                UIUtilsForge.LOGGER.warn("Network handler or player was null while using De-sync");
            }
        }));
        
        // Send packets button
        screen.addRenderableWidget(new Button(5, 65, 115, 20, Component.literal("Send packets: " + SharedVariables.sendUIPackets), (button) -> {
            SharedVariables.sendUIPackets = !SharedVariables.sendUIPackets;
            button.setMessage(Component.literal("Send packets: " + SharedVariables.sendUIPackets));
        }));
        
        // Delay packets button
        screen.addRenderableWidget(new Button(5, 95, 115, 20, Component.literal("Delay packets: " + SharedVariables.delayUIPackets), (button) -> {
            SharedVariables.delayUIPackets = !SharedVariables.delayUIPackets;
            button.setMessage(Component.literal("Delay packets: " + SharedVariables.delayUIPackets));
            if (!SharedVariables.delayUIPackets && !SharedVariables.delayedUIPackets.isEmpty() && mc.getConnection() != null) {
                for (Packet<?> packet : SharedVariables.delayedUIPackets) {
                    mc.getConnection().send(packet);
                }
                if (mc.player != null) {
                    mc.player.displayClientMessage(Component.literal("Sent " + SharedVariables.delayedUIPackets.size() + " packets."), false);
                }
                SharedVariables.delayedUIPackets.clear();
            }
        }));
        
        // Save GUI button
        screen.addRenderableWidget(new Button(5, 125, 115, 20, Component.literal("Save GUI"), (button) -> {
            if (mc.player != null) {
                SharedVariables.storedScreen = mc.screen;
                SharedVariables.storedContainerMenu = mc.player.containerMenu;
            }
        }));
        
        // Disconnect and send packets button
        screen.addRenderableWidget(new Button(5, 155, 160, 20, Component.literal("Disconnect and send packets"), (button) -> {
            SharedVariables.delayUIPackets = false;
            if (mc.getConnection() != null) {
                for (Packet<?> packet : SharedVariables.delayedUIPackets) {
                    mc.getConnection().send(packet);
                }
                mc.getConnection().disconnect(Component.literal("Disconnecting (UI-UTILS)"));
            } else {
                UIUtilsForge.LOGGER.warn("Network handler is null while disconnecting");
            }
            SharedVariables.delayedUIPackets.clear();
        }));
        
        // Fabricate packet button
        Button fabricatePacketButton = new Button(5, 185, 115, 20, Component.literal("Fabricate packet"), (button) -> {
            if (!Minecraft.IS_RUNNING_ON_MAC) {
                createFabricatePacketFrame(mc);
            }
        });
        screen.addRenderableWidget(fabricatePacketButton);
        
        // Copy GUI Title JSON button
        screen.addRenderableWidget(new Button(5, 215, 115, 20, Component.literal("Copy GUI Title JSON"), (button) -> {
            try {
                if (mc.screen == null) {
                    throw new IllegalStateException("Current screen is null");
                }
                Gson gson = new Gson();
                String json = gson.toJson(mc.screen.getTitle().getString());
                mc.keyboardHandler.setClipboard(json);
            } catch (Exception e) {
                UIUtilsForge.LOGGER.error("Error while copying title JSON", e);
            }
        }));
    }
    
    private static void createFabricatePacketFrame(Minecraft mc) {
        JFrame frame = new JFrame("Choose Packet");
        frame.setBounds(0, 0, 450, 100);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        
        JButton clickSlotButton = new JButton("Click Slot");
        clickSlotButton.setFocusable(false);
        clickSlotButton.setBorder(BorderFactory.createEtchedBorder());
        clickSlotButton.setBackground(darkWhite);
        clickSlotButton.setFont(monospace);
        clickSlotButton.setBounds(100, 25, 110, 20);
        clickSlotButton.addActionListener((event) -> {
            frame.setVisible(false);
            createClickSlotPacketFrame(mc);
        });
        
        JButton buttonClickButton = new JButton("Button Click");
        buttonClickButton.setFocusable(false);
        buttonClickButton.setBorder(BorderFactory.createEtchedBorder());
        buttonClickButton.setBackground(darkWhite);
        buttonClickButton.setFont(monospace);
        buttonClickButton.setBounds(250, 25, 110, 20);
        buttonClickButton.addActionListener((event) -> {
            frame.setVisible(false);
            createButtonClickPacketFrame(mc);
        });
        
        frame.add(clickSlotButton);
        frame.add(buttonClickButton);
        frame.setVisible(true);
    }
    
    private static void createClickSlotPacketFrame(Minecraft mc) {
        JFrame frame = new JFrame("Click Slot Packet");
        frame.setBounds(0, 0, 450, 300);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        
        JLabel syncIdLabel = new JLabel("Sync Id:");
        syncIdLabel.setFocusable(false);
        syncIdLabel.setFont(monospace);
        syncIdLabel.setBounds(25, 25, 100, 20);
        
        JLabel revisionLabel = new JLabel("Revision:");
        revisionLabel.setFocusable(false);
        revisionLabel.setFont(monospace);
        revisionLabel.setBounds(25, 50, 100, 20);
        
        JLabel slotLabel = new JLabel("Slot:");
        slotLabel.setFocusable(false);
        slotLabel.setFont(monospace);
        slotLabel.setBounds(25, 75, 100, 20);
        
        JLabel buttonLabel = new JLabel("Button:");
        buttonLabel.setFocusable(false);
        buttonLabel.setFont(monospace);
        buttonLabel.setBounds(25, 100, 100, 20);
        
        JLabel actionLabel = new JLabel("Action:");
        actionLabel.setFocusable(false);
        actionLabel.setFont(monospace);
        actionLabel.setBounds(25, 125, 100, 20);
        
        JLabel timesToSendLabel = new JLabel("Times to send:");
        timesToSendLabel.setFocusable(false);
        timesToSendLabel.setFont(monospace);
        timesToSendLabel.setBounds(25, 190, 100, 20);
        
        JTextField syncIdField = new JTextField(1);
        syncIdField.setFont(monospace);
        syncIdField.setBounds(125, 25, 100, 20);
        
        JTextField revisionField = new JTextField(1);
        revisionField.setFont(monospace);
        revisionField.setBounds(125, 50, 100, 20);
        
        JTextField slotField = new JTextField(1);
        slotField.setFont(monospace);
        slotField.setBounds(125, 75, 100, 20);
        
        JTextField buttonField = new JTextField(1);
        buttonField.setFont(monospace);
        buttonField.setBounds(125, 100, 100, 20);
        
        JComboBox<String> actionField = new JComboBox<>(new Vector<>(ImmutableList.of(
                "PICKUP",
                "QUICK_MOVE",
                "SWAP",
                "CLONE",
                "THROW",
                "QUICK_CRAFT",
                "PICKUP_ALL"
        )));
        actionField.setFocusable(false);
        actionField.setEditable(false);
        actionField.setBorder(BorderFactory.createEmptyBorder());
        actionField.setBackground(darkWhite);
        actionField.setFont(monospace);
        actionField.setBounds(125, 125, 100, 20);
        
        JLabel statusLabel = new JLabel();
        statusLabel.setVisible(false);
        statusLabel.setFocusable(false);
        statusLabel.setFont(monospace);
        statusLabel.setBounds(210, 150, 190, 20);
        
        JCheckBox delayBox = new JCheckBox("Delay");
        delayBox.setBounds(115, 150, 85, 20);
        delayBox.setSelected(false);
        delayBox.setFont(monospace);
        delayBox.setFocusable(false);
        
        JTextField timesToSendField = new JTextField("1");
        timesToSendField.setFont(monospace);
        timesToSendField.setBounds(125, 190, 100, 20);
        
        JButton sendButton = new JButton("Send");
        sendButton.setFocusable(false);
        sendButton.setBounds(25, 150, 75, 20);
        sendButton.setBorder(BorderFactory.createEtchedBorder());
        sendButton.setBackground(darkWhite);
        sendButton.setFont(monospace);
        sendButton.addActionListener((event0) -> {
            if (isInteger(syncIdField.getText()) &&
                    isInteger(revisionField.getText()) &&
                    isInteger(slotField.getText()) &&
                    isInteger(buttonField.getText()) &&
                    isInteger(timesToSendField.getText()) &&
                    actionField.getSelectedItem() != null) {
                try {
                    int syncId = Integer.parseInt(syncIdField.getText());
                    int revision = Integer.parseInt(revisionField.getText());
                    int slot = Integer.parseInt(slotField.getText());
                    int button = Integer.parseInt(buttonField.getText());
                    ClickType action = stringToClickType(actionField.getSelectedItem().toString());
                    int timesToSend = Integer.parseInt(timesToSendField.getText());
                    
                    if (action != null) {
                        ServerboundContainerClickPacket packet = new ServerboundContainerClickPacket(syncId, revision, slot, button, action, new Int2ObjectArrayMap<>(), null);
                        for (int i = 0; i < timesToSend; i++) {
                            if (mc.getConnection() != null) {
                                mc.getConnection().send(packet);
                            }
                        }
                        statusLabel.setVisible(true);
                        statusLabel.setForeground(Color.GREEN.darker());
                        statusLabel.setText("Sent successfully!");
                        queueTask(() -> {
                            statusLabel.setVisible(false);
                            statusLabel.setText("");
                        }, 1500L);
                    }
                } catch (Exception e) {
                    statusLabel.setVisible(true);
                    statusLabel.setForeground(Color.RED.darker());
                    statusLabel.setText("Error: " + e.getMessage());
                    queueTask(() -> {
                        statusLabel.setVisible(false);
                        statusLabel.setText("");
                    }, 1500L);
                }
            } else {
                statusLabel.setVisible(true);
                statusLabel.setForeground(Color.RED.darker());
                statusLabel.setText("Invalid arguments!");
                queueTask(() -> {
                    statusLabel.setVisible(false);
                    statusLabel.setText("");
                }, 1500L);
            }
        });
        
        frame.add(syncIdLabel);
        frame.add(revisionLabel);
        frame.add(slotLabel);
        frame.add(buttonLabel);
        frame.add(actionLabel);
        frame.add(timesToSendLabel);
        frame.add(syncIdField);
        frame.add(revisionField);
        frame.add(slotField);
        frame.add(buttonField);
        frame.add(actionField);
        frame.add(sendButton);
        frame.add(statusLabel);
        frame.add(delayBox);
        frame.add(timesToSendField);
        frame.setVisible(true);
    }
    
    private static void createButtonClickPacketFrame(Minecraft mc) {
        JFrame frame = new JFrame("Button Click Packet");
        frame.setBounds(0, 0, 450, 250);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        
        JLabel syncIdLabel = new JLabel("Sync Id:");
        syncIdLabel.setFocusable(false);
        syncIdLabel.setFont(monospace);
        syncIdLabel.setBounds(25, 25, 100, 20);
        
        JLabel buttonIdLabel = new JLabel("Button Id:");
        buttonIdLabel.setFocusable(false);
        buttonIdLabel.setFont(monospace);
        buttonIdLabel.setBounds(25, 50, 100, 20);
        
        JTextField syncIdField = new JTextField(1);
        syncIdField.setFont(monospace);
        syncIdField.setBounds(125, 25, 100, 20);
        
        JTextField buttonIdField = new JTextField(1);
        buttonIdField.setFont(monospace);
        buttonIdField.setBounds(125, 50, 100, 20);
        
        JLabel statusLabel = new JLabel();
        statusLabel.setVisible(false);
        statusLabel.setFocusable(false);
        statusLabel.setFont(monospace);
        statusLabel.setBounds(210, 95, 190, 20);
        
        JCheckBox delayBox = new JCheckBox("Delay");
        delayBox.setBounds(115, 95, 85, 20);
        delayBox.setSelected(false);
        delayBox.setFont(monospace);
        delayBox.setFocusable(false);
        
        JLabel timesToSendLabel = new JLabel("Times to send:");
        timesToSendLabel.setFocusable(false);
        timesToSendLabel.setFont(monospace);
        timesToSendLabel.setBounds(25, 130, 100, 20);
        
        JTextField timesToSendField = new JTextField("1");
        timesToSendField.setFont(monospace);
        timesToSendField.setBounds(125, 130, 100, 20);
        
        JButton sendButton = new JButton("Send");
        sendButton.setFocusable(false);
        sendButton.setBounds(25, 95, 75, 20);
        sendButton.setBorder(BorderFactory.createEtchedBorder());
        sendButton.setBackground(darkWhite);
        sendButton.setFont(monospace);
        sendButton.addActionListener((event0) -> {
            if (isInteger(syncIdField.getText()) &&
                    isInteger(buttonIdField.getText()) &&
                    isInteger(timesToSendField.getText())) {
                try {
                    int syncId = Integer.parseInt(syncIdField.getText());
                    int buttonId = Integer.parseInt(buttonIdField.getText());
                    int timesToSend = Integer.parseInt(timesToSendField.getText());
                    
                    ServerboundButtonClickPacket packet = new ServerboundButtonClickPacket(syncId, buttonId);
                    for (int i = 0; i < timesToSend; i++) {
                        if (mc.getConnection() != null) {
                            mc.getConnection().send(packet);
                        }
                    }
                    statusLabel.setVisible(true);
                    statusLabel.setForeground(Color.GREEN.darker());
                    statusLabel.setText("Sent successfully!");
                    queueTask(() -> {
                        statusLabel.setVisible(false);
                        statusLabel.setText("");
                    }, 1500L);
                } catch (Exception e) {
                    statusLabel.setVisible(true);
                    statusLabel.setForeground(Color.RED.darker());
                    statusLabel.setText("Error: " + e.getMessage());
                    queueTask(() -> {
                        statusLabel.setVisible(false);
                        statusLabel.setText("");
                    }, 1500L);
                }
            } else {
                statusLabel.setVisible(true);
                statusLabel.setForeground(Color.RED.darker());
                statusLabel.setText("Invalid arguments!");
                queueTask(() -> {
                    statusLabel.setVisible(false);
                    statusLabel.setText("");
                }, 1500L);
            }
        });
        
        frame.add(syncIdLabel);
        frame.add(buttonIdLabel);
        frame.add(syncIdField);
        frame.add(timesToSendLabel);
        frame.add(buttonIdField);
        frame.add(sendButton);
        frame.add(statusLabel);
        frame.add(delayBox);
        frame.add(timesToSendField);
        frame.setVisible(true);
    }
    
    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static ClickType stringToClickType(String string) {
        return switch (string) {
            case "PICKUP" -> ClickType.PICKUP;
            case "QUICK_MOVE" -> ClickType.QUICK_MOVE;
            case "SWAP" -> ClickType.SWAP;
            case "CLONE" -> ClickType.CLONE;
            case "THROW" -> ClickType.THROW;
            case "QUICK_CRAFT" -> ClickType.QUICK_CRAFT;
            case "PICKUP_ALL" -> ClickType.PICKUP_ALL;
            default -> null;
        };
    }
    
    public static void queueTask(Runnable runnable, long delayMs) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Minecraft.getInstance().execute(runnable);
            }
        };
        timer.schedule(task, delayMs);
    }
}
