package com.fakepixel.fpu;

import java.util.Base64;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

@Mod(modid = FakepixelUtilities.MODID, version = FakepixelUtilities.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class FakepixelUtilities {
    public static final String MODID = "fpu";
    public static final String VERSION = "1.0.0";
    
   
    private static final String API_KEY_B64 = "API_KEY_HERE"; 
    private static final String API_URL_B64 = "API_URL_HERE"; 

   
    public static String getApiKey() {
        return new String(Base64.getDecoder().decode(API_KEY_B64));
    }

    public static String getApiUrl() {
        return new String(Base64.getDecoder().decode(API_URL_B64));
    }

    public static boolean inSkyblock = false;
    public static boolean isShowTooltip = true;
    public static boolean isDebugMode = false;
    public static boolean isMinionOverlayEnabled = true;
    public static boolean isMinionUpgradeAdviceEnabled = true;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new TooltipListener());
        MinecraftForge.EVENT_BUS.register(new MinionOverlayListener());
        MinecraftForge.EVENT_BUS.register(new ChatListener());
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new FpuCommand());
    }

    @SubscribeEvent
    public void onPlayerJoin(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        inSkyblock = false;
        
        new Thread(() -> {
            try {
                Thread.sleep(4000); 
                if (Minecraft.getMinecraft().thePlayer != null) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(EnumChatFormatting.GREEN + "[FPU] Mod Loaded Successfully! You are using v" + VERSION + ".")
                    );
                }
            } catch (InterruptedException ignored) {}
        }).start();
    }
}