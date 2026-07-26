package com.wonginnovations.oldresearch.client.gui;

import com.wonginnovations.oldresearch.common.container.ContainerDeconstructionTable;
import com.wonginnovations.oldresearch.common.tiles.TileDeconstructionTable;
import com.wonginnovations.oldresearch.tc4legacy.client.UtilsFX;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.SoundsTC;

import java.io.IOException;
import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class GuiDeconstructionTable extends GuiContainer {
    private final TileDeconstructionTable tableInventory;

    public GuiDeconstructionTable(InventoryPlayer inventoryPlayer, TileDeconstructionTable tile) {
        super(new ContainerDeconstructionTable(inventoryPlayer, tile));
        this.tableInventory = tile;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        UtilsFX.bindTexture("textures/gui/gui_decontable.png");
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        if (this.tableInventory.breaktime > 0) {
            int i1 = this.tableInventory.getBreakTimeScaled(46);
            this.drawTexturedModalRect(x + 93, y + 15 + 46 - i1, 176, 46 - i1, 9, i1);
        }

        if (this.tableInventory.aspect != null) {
            UtilsFX.drawTag(x + 64, y + 48, this.tableInventory.aspect, 0.0F, 0, this.zLevel);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            RenderHelper.disableStandardItemLighting();
            int var7 = mouseX - (x + 64);
            int var8 = mouseY - (y + 48);
            if (var7 >= 0 && var8 >= 0 && var7 < 16 && var8 < 16) {
                UtilsFX.drawCustomTooltip(this, itemRender, this.fontRenderer, Arrays.asList(this.tableInventory.aspect.getName(), this.tableInventory.aspect.getLocalizedDescription()), mouseX, mouseY - 8, 11);
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
    }

    protected void mouseClicked(int mx, int my, int par3) throws IOException {
        super.mouseClicked(mx, my, par3);
        int gx = (this.width - this.xSize) / 2;
        int gy = (this.height - this.ySize) / 2;
        int var7 = mx - (gx + 64);
        int var8 = my - (gy + 48);
        if (var7 >= 0 && var8 >= 0 && var7 < 16 && var8 < 16 && this.tableInventory.aspect != null) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 1);
            this.playButtonAspect();
        }
    }

    private void playButtonAspect() {
        this.mc.getRenderViewEntity().world.playSound(this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY, this.mc.getRenderViewEntity().posZ, SoundsTC.hhoff, SoundCategory.BLOCKS, 0.2F, 1.0F + this.mc.getRenderViewEntity().world.rand.nextFloat() * 0.1F, false);
    }
}
