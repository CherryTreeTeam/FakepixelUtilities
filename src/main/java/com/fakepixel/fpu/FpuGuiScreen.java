package com.fakepixel.fpu;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import java.io.IOException;

public class FpuGuiScreen extends GuiScreen {
    private int currentPage = 0;
    private GuiTextField searchField;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        
        int boxWidth = 250;
        int boxHeight = 220;
        int x = (this.width - boxWidth) / 2;
        int y = (this.height - boxHeight) / 2;

        this.buttonList.add(new GuiButton(10, x + 10, y + 30, 74, 20, (currentPage == 0 ? EnumChatFormatting.GOLD + "▶ FPU" : "FPU")));
        this.buttonList.add(new GuiButton(11, x + 88, y + 30, 74, 20, (currentPage == 1 ? EnumChatFormatting.GOLD + "▶ Price" : "Price")));
        this.buttonList.add(new GuiButton(12, x + 166, y + 30, 74, 20, (currentPage == 2 ? EnumChatFormatting.GOLD + "▶ About" : "About")));
        
        if (currentPage == 0) {
            String tooltipStatus = FakepixelUtilities.isShowTooltip ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
            this.buttonList.add(new GuiButton(1, x + 25, y + 65, 200, 20, "Show Tooltips: " + tooltipStatus));

            String debugStatus = FakepixelUtilities.isDebugMode ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
            this.buttonList.add(new GuiButton(2, x + 25, y + 100, 200, 20, "Debug Logs: " + debugStatus));

            String minionStatus = FakepixelUtilities.isMinionOverlayEnabled ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
            this.buttonList.add(new GuiButton(3, x + 25, y + 135, 200, 20, "Minion Chest Prices: " + minionStatus));

            String adviceStatus = FakepixelUtilities.isMinionUpgradeAdviceEnabled ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
            this.buttonList.add(new GuiButton(4, x + 25, y + 170, 200, 20, "Minion Upgrades: " + adviceStatus));
        } else if (currentPage == 1) { 
            this.searchField = new GuiTextField(0, this.fontRendererObj, x + 25, y + 65, 200, 20);
            this.searchField.setMaxStringLength(30);
            this.searchField.setFocused(true);
            this.searchField.setText("");
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        
        int boxWidth = 250;
        int boxHeight = 220;
        int x = (this.width - boxWidth) / 2;
        int y = (this.height - boxHeight) / 2;

        drawRect(x, y, x + boxWidth, y + boxHeight, 0xF5101014); 
        drawRect(x - 1, y - 1, x + boxWidth + 1, y, 0xFF4A4A5A); 
        drawRect(x - 1, y, x, y + boxHeight, 0xFF4A4A5A);
        drawRect(x + boxWidth, y, x + boxWidth + 1, y + boxHeight, 0xFF4A4A5A);
        drawRect(x - 1, y + boxHeight, x + boxWidth + 1, y + boxHeight + 1, 0xFF4A4A5A);

        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.RED + "FakepixelUtilities", this.width / 2, y + 12, 0xFFFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (currentPage == 1 && searchField != null) {
            searchField.drawTextBox();
            
            String query = searchField.getText().trim().toUpperCase().replace(" ", "_");
            int renderY = y + 95;

            if (!query.isEmpty()) {
                PriceManager.ItemData data = PriceManager.getCachedPrice(query);
                if (data != null) {
                    this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GOLD + "[FPU LIVE LOOKUP]", x + 25, renderY, 0xFFFFFFFF);
                    this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + "Bazaar Buy: " + EnumChatFormatting.YELLOW + data.bzBuy, x + 25, renderY + 15, 0xFFFFFFFF);
                    this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + "Bazaar Sell: " + EnumChatFormatting.YELLOW + data.bzSell, x + 25, renderY + 28, 0xFFFFFFFF);
                    this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + "Ah Highest: " + EnumChatFormatting.YELLOW + data.ahHigh, x + 25, renderY + 41, 0xFFFFFFFF);
                    this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + "Ah Lowest: " + EnumChatFormatting.YELLOW + data.ahLow, x + 25, renderY + 54, 0xFFFFFFFF);
                    this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + "Ah Average: " + EnumChatFormatting.YELLOW + data.ahAvg, x + 25, renderY + 67, 0xFFFFFFFF);
                    this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "Last updated: " + data.lastUpdated, x + 25, renderY + 82, 0xFFFFFFFF);
                } else {
                    this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "Indexing database metrics...", x + 25, renderY, 0xFFFFFFFF);
                }
            } else {
                this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "Type item name (e.g., WHEAT)", x + 25, renderY, 0xFFFFFFFF);
            }
        }
        
        else if (currentPage == 2) {
            int renderY = y + 65;
            
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GOLD + "CherryTree Team", x + 20, renderY, 0xFFFFFFFF);
            drawRect(x + 20, renderY + 11, x + boxWidth - 20, renderY + 12, 0xFF4A4A5A);
            
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.LIGHT_PURPLE + "c1727.c", x + 25, renderY + 22, 0xFFFFFFFF);
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + " -> " + EnumChatFormatting.WHITE + "Project Founder", x + 25, renderY + 33, 0xFFFFFFFF);
            
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.AQUA + "_jatin_e", x + 25, renderY + 52, 0xFFFFFFFF);
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + " -> " + EnumChatFormatting.WHITE + "Project Manager", x + 25, renderY + 63, 0xFFFFFFFF);
            
            drawRect(x + 20, renderY + 85, x + boxWidth - 20, renderY + 86, 0xFF4A4A5A);
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + "Made with " + EnumChatFormatting.RED + "❤" + EnumChatFormatting.GRAY + " by " + EnumChatFormatting.LIGHT_PURPLE + "CherryTree Team", x + 48, renderY + 95, 0xFFFFFFFF);
            this.fontRendererObj.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "Fully secure API | Mod v1.0.0", x + 53, renderY + 108, 0xFFFFFFFF);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 10) { currentPage = 0; initGui(); }
        else if (button.id == 11) { currentPage = 1; initGui(); }
        else if (button.id == 12) { currentPage = 2; initGui(); }
        else if (currentPage == 0) {
            if (button.id == 1) FakepixelUtilities.isShowTooltip = !FakepixelUtilities.isShowTooltip;
            else if (button.id == 2) FakepixelUtilities.isDebugMode = !FakepixelUtilities.isDebugMode;
            else if (button.id == 3) FakepixelUtilities.isMinionOverlayEnabled = !FakepixelUtilities.isMinionOverlayEnabled;
            else if (button.id == 4) FakepixelUtilities.isMinionUpgradeAdviceEnabled = !FakepixelUtilities.isMinionUpgradeAdviceEnabled;
            initGui();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (currentPage == 1 && searchField != null && searchField.isFocused()) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                super.keyTyped(typedChar, keyCode);
            } else {
                searchField.textboxKeyTyped(typedChar, keyCode);
            }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (currentPage == 1 && searchField != null) {
            searchField.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (currentPage == 1 && searchField != null) {
            searchField.updateCursorCounter();
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}