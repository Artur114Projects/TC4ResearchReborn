package com.wonginnovations.oldresearch.client.renderer.item;

import com.wonginnovations.oldresearch.client.model.ModelArcaneWorkbench;
import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.items.ItemsTC;

public class TileDeconstructionTableItemRenderer extends TileEntityItemStackRenderer {
    private final ModelArcaneWorkbench tableModel = new ModelArcaneWorkbench();
    private final ResourceLocation tableTex = OldResearch.loc("textures/models/decontable.png");
    private IBakedModel tm = null;

    public TileDeconstructionTableItemRenderer() {}

    @Override
    public void renderByItem(ItemStack itemStackIn) {
        if (this.tm == null) {
            this.tm = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(new ItemStack(ItemsTC.thaumometer), null, null);
        }

        GL11.glPushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(tableTex);
        GL11.glTranslatef(0.5F, 1.0F, 0.5F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.tableModel.renderAll();
        GL11.glPopMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GL11.glPushMatrix();
        GL11.glTranslatef(0.5F + (2.06F / 16.0F), 1.0F + (3.5F / 16.0F), 0.5F + (2.06F / 16.0F));
        GL11.glScalef(4.14F / 16.0F, 4.14F / 16.0F, 4.14F / 16.0F);
        Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(ItemsTC.thaumometer), this.tm);
        GL11.glPopMatrix();
    }
}
