package com.wonginnovations.oldresearch.client.renderer.tile;

import com.wonginnovations.oldresearch.client.model.ModelArcaneWorkbench;
import com.wonginnovations.oldresearch.common.tiles.TileDeconstructionTable;
import com.wonginnovations.oldresearch.main.OldResearch;
import com.wonginnovations.oldresearch.tc4legacy.client.UtilsFX;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import thaumcraft.api.items.ItemsTC;

@SideOnly(Side.CLIENT)
public class TileDeconstructionTableRenderer extends TileEntitySpecialRenderer<TileDeconstructionTable> {
    private final ModelArcaneWorkbench tableModel = new ModelArcaneWorkbench();
    private final ResourceLocation tableTex = OldResearch.loc("textures/models/decontable.png");
    private IBakedModel tmModel = null;
    private final ItemStack tm;

    public TileDeconstructionTableRenderer() {
        this.tm = new ItemStack(ItemsTC.thaumometer);
    }

    @Override
    public void render(TileDeconstructionTable table, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        this.bindTexture(tableTex);
        GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0F, (float)z + 0.5F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.tableModel.renderAll();
        GL11.glPopMatrix();
//        GL11.glPushMatrix();
//        GL11.glTranslatef((float) x, (float) y, (float) z);
//        GL11.glTranslatef(0.5F, 1.0F + (1.5F / 16.0F), 0.5F);
//        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
//        GL11.glTranslatef(0, -(10.5F / 16.0F), 0);
//        GL11.glScalef(1.2F, 1.2F, 1.2F);
//        GlStateManager.enableLighting();
//        EntityItem entityitem = new EntityItem(table.getWorld(), (double)0.0F, (double)0.0F, (double)0.0F, this.tm);
//        entityitem.hoverStart = 0.0F;
//        rendermanager.renderEntity(entityitem, (double)0.0F, (double)0.0F, (double)0.0F, 0.0F, 0.0F, false);
//        GL11.glPopMatrix();

        if (this.tmModel == null) {
            this.tmModel = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(this.tm, Minecraft.getMinecraft().world, null);
        }

        GL11.glPushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GL11.glTranslatef((float) x + 0.5F + (2.1F / 16.0F), (float) y + 1.0F + (3.5F / 16.0F), (float) z + 0.5F + (2.1F / 16.0F));
        GL11.glScalef(4.2F / 16.0F, 4.2F / 16.0F, 4.2F / 16.0F);
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        Minecraft.getMinecraft().getRenderItem().renderItem(this.tm, this.tmModel);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        Minecraft.getMinecraft().renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GL11.glPopMatrix();


        float ticks = (float) Minecraft.getMinecraft().player.ticksExisted + partialTicks;
        if (!table.breakItem.isEmpty()) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)x + 0.5F, (float)y + 1.15F, (float)z + 0.5F);
            GL11.glRotatef(ticks % 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 1);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75F);
            ItemStack is = table.breakItem.copy();
            is.setCount(1);
            EntityItem entityitem = new EntityItem(table.getWorld(), (double)0.0F, (double)0.0F, (double)0.0F, is);
            entityitem.hoverStart = MathHelper.sin(ticks / 14.0F) * 0.2F + 0.2F;
            rendermanager.renderEntity(entityitem, (double)0.0F, (double)0.0F, (double)0.0F, 0.0F, 0.0F, false);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        }

        if (table.aspect != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0F + (2.0F / 16.0F), (float)z + 0.5F);
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(ticks % 360.0F, 0.0F, 0.0F, 1.0F);
            GL11.glScaled(0.024, 0.024, 0.024);
            UtilsFX.drawTag(-8, -8, table.aspect, 0.0F, 0, (double)0.0F, 1, 0.8F, false);
            GL11.glPopMatrix();
        }
    }
}
