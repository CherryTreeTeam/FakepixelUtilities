package com.fakepixel.fpu;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TooltipListener {

    private static final Pattern PRICE_PATTERN = Pattern.compile("([\\d,.]+)");
    private static final ConcurrentHashMap<String, Boolean> fetchingItems = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
      
        if (!FakepixelUtilities.inSkyblock) return;

        ItemStack item = event.itemStack;
        if (item == null || item.getItem() == null) return;

        String displayName = EnumChatFormatting.getTextWithoutFormattingCodes(item.getDisplayName()).toUpperCase().trim();
        String internalName = getSkyblockId(item);

        if (internalName == null || internalName.equals("POTION") || internalName.equals("POTION_POTION_1")) {
            String cleanDisplay = displayName.replaceAll("^(SPICY|FIERCE|PURE|HEROIC|SHARP|FAIR|GENTLE|ODD|FAST|AWKWARD|RICH|CLEAN|DEADLY|FINE|GRAND|HASTY|NEAT|RAPID|UNREAL|FIERY|EXCELLENT|FABLED|SUSPICIOUS|GILDED|WARPED|WITHERED|BULKY|HEAVY|LIGHT|MYTHIC|SALMON|WISE|STRONG|SUPERIOR|UNYIELDING|ZEALOUS|LEGENDARY|SUBMERGED|JADED|CUBIC|REINFORCED|LOVING|RIDICULOUS|NECROTIC|SPIKED|RENOWNED|BITING|ITCHY|UNPLEASANT|FORCEFUL|HURTFUL|GODLY|DEMONIC)\\s+", "");
            cleanDisplay = cleanDisplay.replace(" ", "_");
            internalName = cleanDisplay;
        }

        if (!internalName.contains("PET_SKIN_") && !internalName.contains("POTION") && !internalName.contains("BOOK") && !internalName.contains("CAT") && !internalName.contains("SKIN")) {
            internalName = internalName.replaceAll("[0-9]", "");
        }
        
        internalName = internalName.replace(";_ ", "_").replace(";", "_").replace("__", "_");
        if (internalName.endsWith("_")) {
            internalName = internalName.substring(0, internalName.length() - 1);
        }

        List<String> tooltip = event.toolTip;
        boolean isBazaar = false;
        boolean isAH = false;
        String bzBuy = null, bzSell = null, ahPrice = null;

        for (String line : tooltip) {
            String cleanLine = EnumChatFormatting.getTextWithoutFormattingCodes(line).trim();

            if (cleanLine.startsWith("Buy price:") || cleanLine.startsWith("Price:")) {
                isBazaar = true; 
                bzBuy = extractPrice(cleanLine);
            } else if (cleanLine.startsWith("Sell price:") || cleanLine.startsWith("Price per unit:")) {
                isBazaar = true; 
                bzSell = extractPrice(cleanLine);
            } else if (cleanLine.startsWith("Buy it now:") || cleanLine.startsWith("Starting bid:") || cleanLine.startsWith("Top bid:")) { 
                isAH = true; 
                ahPrice = extractPrice(cleanLine);
            }
        }

        if (isBazaar || isAH) {
            PriceManager.sendPriceData(internalName, bzBuy, bzSell, ahPrice);
        }

        if (FakepixelUtilities.isShowTooltip) {
            tooltip.add("");
            tooltip.add(EnumChatFormatting.GOLD + "[FPU PRICES]");

            PriceManager.ItemData data = PriceManager.getCachedPrice(internalName);

            if (data != null) {
                tooltip.add(EnumChatFormatting.GRAY + "Bazaar Buy: " + EnumChatFormatting.YELLOW + data.bzBuy);
                tooltip.add(EnumChatFormatting.GRAY + "Bazaar Sell: " + EnumChatFormatting.YELLOW + data.bzSell);
                tooltip.add(EnumChatFormatting.GRAY + "Ah Highest: " + EnumChatFormatting.YELLOW + data.ahHigh);
                tooltip.add(EnumChatFormatting.GRAY + "Ah Lowest: " + EnumChatFormatting.YELLOW + data.ahLow);
                tooltip.add(EnumChatFormatting.GRAY + "Ah Average: " + EnumChatFormatting.YELLOW + data.ahAvg);
                tooltip.add(EnumChatFormatting.DARK_GRAY + "Last updated: " + data.lastUpdated);
            } else {
                triggerAsyncFetchIfNeeded(internalName);
                tooltip.add(EnumChatFormatting.DARK_GRAY + "Loading price metrics...");
            }

            tooltip.add(EnumChatFormatting.GOLD + "--------------------");
        }
    }

    private void triggerAsyncFetchIfNeeded(String itemName) {
        if (fetchingItems.putIfAbsent(itemName, true) == null) {
            PriceManager.fetchPriceAsync(itemName, () -> fetchingItems.remove(itemName));
        }
    }

    private String extractPrice(String line) {
        Matcher matcher = PRICE_PATTERN.matcher(line);
        if (matcher.find()) return matcher.group(1);
        return null;
    }

    private String getSkyblockId(ItemStack item) {
        if (item.hasTagCompound()) {
            NBTTagCompound tag = item.getTagCompound();
            if (tag.hasKey("ExtraAttributes")) {
                NBTTagCompound ea = tag.getCompoundTag("ExtraAttributes");
                if (ea.hasKey("id")) {
                    String baseId = ea.getString("id").toUpperCase().trim();
                    
                    if (baseId.contains("SKIN") || baseId.endsWith("_CAT") || baseId.contains("PET_SKIN_")) {
                        return baseId; 
                    }

                    if (baseId.contains("PET_") || baseId.contains("DEV_PET_")) {
                        String tierSuffix = "_COMMON";
                        if (baseId.endsWith(";5")) tierSuffix = "_MYTHIC";
                        else if (baseId.endsWith(";4")) tierSuffix = "_LEGENDARY";
                        else if (baseId.endsWith(";3")) tierSuffix = "_EPIC";
                        else if (baseId.endsWith(";2")) tierSuffix = "_RARE";
                        else if (baseId.endsWith(";1")) tierSuffix = "_UNCOMMON";
                        
                        String cleanPetName = baseId.split(";")[0].replace("PET_", "");
                        return "PET_" + cleanPetName + tierSuffix;
                    }
                    
                    if (baseId.equals("ENCHANTED_BOOK") && ea.hasKey("enchantments")) {
                        NBTTagCompound enchants = ea.getCompoundTag("enchantments");
                        StringBuilder bookUniqueId = new StringBuilder("ENCHANTED_BOOK");
                        
                        for (String enchantKey : enchants.getKeySet()) {
                            int level = enchants.getInteger(enchantKey);
                            bookUniqueId.append("_").append(enchantKey.toUpperCase()).append("_").append(level);
                        }
                        return bookUniqueId.toString();
                    }
                    
                    if (ea.hasKey("potion") || baseId.contains("POTION")) {
                        String potionType = ea.hasKey("potion") ? ea.getString("potion").toUpperCase() : baseId;
                        int level = ea.hasKey("potion_level") ? ea.getInteger("potion_level") : 1;
                        return "POTION_" + potionType + "_" + level;
                    }

                    if (ea.hasKey("attributes")) {
                        NBTTagCompound attr = ea.getCompoundTag("attributes");
                        StringBuilder attrUniqueId = new StringBuilder(baseId);
                        for (String attrKey : attr.getKeySet()) {
                            attrUniqueId.append("_").append(attrKey.toUpperCase());
                        }
                        return attrUniqueId.toString();
                    }
                    
                    return baseId;
                }
            }
        }
        return null;
    }
}