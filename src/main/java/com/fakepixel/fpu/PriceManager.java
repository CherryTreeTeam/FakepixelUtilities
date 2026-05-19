package com.fakepixel.fpu;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class PriceManager {
    private static final HashMap<String, ItemData> priceCache = new HashMap<>();
    private static final HashMap<String, Long> cooldownMap = new HashMap<>();
    private static final long COOLDOWN_MS = 10000;
    private static final HashMap<String, Long> lastFetchMap = new HashMap<>();
    private static final long FETCH_COOLDOWN_MS = 4000;

    public static boolean isServerOnline = true;

    public static class ItemData {
        public String bzBuy, bzSell, ahHigh, ahLow, ahAvg, lastUpdated;
        public ItemData(String bzB, String bzS, String ahH, String ahL, String ahA, String lu) {
            this.bzBuy = bzB; this.bzSell = bzS; this.ahHigh = ahH;
            this.ahLow = ahL; this.ahAvg = ahA; this.lastUpdated = lu;
        }
    }

    public static ItemData getCachedPrice(String itemName) {
        long now = System.currentTimeMillis();
        if (!lastFetchMap.containsKey(itemName) || (now - lastFetchMap.get(itemName) > FETCH_COOLDOWN_MS)) {
            lastFetchMap.put(itemName, now);
            triggerBackgroundFetch(itemName);
        }
        return priceCache.get(itemName);
    }

    public static void fetchPriceAsync(String itemName, Runnable onComplete) {
        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL(FakepixelUtilities.getApiUrl() + "/api/price/" + itemName.replace(" ", "%20"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(2000);
                if (conn.getResponseCode() == 200) {
                    isServerOnline = true;
                    JsonObject json = new JsonParser().parse(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
                    priceCache.put(itemName, new ItemData(json.get("bzBuy").getAsString(), json.get("bzSell").getAsString(), json.get("ahHigh").getAsString(), json.get("ahLow").getAsString(), json.get("ahAvg").getAsString(), json.get("lastUpdated").getAsString()));
                }
            } catch (Exception e) { isServerOnline = false; }
            if (onComplete != null) onComplete.run();
        });
    }

    public static ItemData getPrice(String itemName) {
        if (priceCache.containsKey(itemName)) { triggerBackgroundFetch(itemName); return priceCache.get(itemName); }
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    URL url = new URL(FakepixelUtilities.getApiUrl() + "/api/price/" + itemName.replace(" ", "%20"));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(2000);
                    if (conn.getResponseCode() == 200) {
                        isServerOnline = true;
                        JsonObject json = new JsonParser().parse(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
                        ItemData data = new ItemData(json.get("bzBuy").getAsString(), json.get("bzSell").getAsString(), json.get("ahHigh").getAsString(), json.get("ahLow").getAsString(), json.get("ahAvg").getAsString(), json.get("lastUpdated").getAsString());
                        priceCache.put(itemName, data); return data;
                    }
                } catch (Exception e) { isServerOnline = false; }
                return null;
            }).get();
        } catch (Exception e) { return null; }
    }

    private static void triggerBackgroundFetch(String itemName) {
        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL(FakepixelUtilities.getApiUrl() + "/api/price/" + itemName.replace(" ", "%20"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(2000);
                if (conn.getResponseCode() == 200) {
                    isServerOnline = true;
                    JsonObject json = new JsonParser().parse(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
                    priceCache.put(itemName, new ItemData(json.get("bzBuy").getAsString(), json.get("bzSell").getAsString(), json.get("ahHigh").getAsString(), json.get("ahLow").getAsString(), json.get("ahAvg").getAsString(), json.get("lastUpdated").getAsString()));
                }
            } catch (Exception e) { isServerOnline = false; }
        });
    }

    public static void sendPriceData(String itemName, String bzBuy, String bzSell, String ahPrice) {
        long currentTime = System.currentTimeMillis();
        if (cooldownMap.containsKey(itemName) && (currentTime - cooldownMap.get(itemName) < COOLDOWN_MS)) return;
        cooldownMap.put(itemName, currentTime);
        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL(FakepixelUtilities.getApiUrl() + "/api/update");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("X-API-Key", FakepixelUtilities.getApiKey());
                conn.setDoOutput(true);
                conn.setConnectTimeout(3000);
                JsonObject json = new JsonObject();
                json.addProperty("itemName", itemName);
                if (bzBuy != null) json.addProperty("bzBuy", bzBuy);
                if (bzSell != null) json.addProperty("bzSell", bzSell);
                if (ahPrice != null) { json.addProperty("ahHigh", ahPrice); json.addProperty("ahLow", ahPrice); }
                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.flush(); os.close();
                if (conn.getResponseCode() == 200) isServerOnline = true;
                if (FakepixelUtilities.isDebugMode && Minecraft.getMinecraft().thePlayer != null) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "[FPU-Debug] Info sent: " + itemName));
                }
            } catch (Exception e) { isServerOnline = false; }
        });
    }
}