package com.wonginnovations.oldresearch.client.renderer.item;

import com.wonginnovations.oldresearch.client.model.ModelArcaneWorkbench;
import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.items.ItemsTC;

public class TileDeconstructionTableItemRenderer extends TileEntityItemStackRenderer {
    private final ModelArcaneWorkbench tableModel = new ModelArcaneWorkbench();
    private final ResourceLocation tableTex = OldResearch.loc("textures/models/decontable.png");
    private final ItemStack tm;

    public TileDeconstructionTableItemRenderer() {
        this.tm = new ItemStack(ItemsTC.thaumometer);
    }

    @Override
    public void renderByItem(ItemStack itemStackIn) {
        GL11.glPushMatrix();
        int x = 0, y = 0, z = 0;
        Minecraft.getMinecraft().renderEngine.bindTexture(tableTex);
        GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0F, (float)z + 0.5F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.tableModel.renderAll();
        GL11.glPopMatrix();
    }
}
