package com.wonginnovations.oldresearch.common.items;

import com.wonginnovations.oldresearch.common.research.curio.BaseCurio;
import com.wonginnovations.oldresearch.common.research.curio.RitesCurio;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;

import java.util.ArrayList;
import java.util.List;

public class ItemCurio extends thaumcraft.common.items.curios.ItemCurio {
    public final ArrayList<BaseCurio> curios = new ArrayList<>();

    public ItemCurio() {
        this.curios.add((new BaseCurio("arcane")).setCategory("AUROMANCY"));
        this.curios.add((new BaseCurio("preserved")).setCategory("ALCHEMY"));
        this.curios.add((new BaseCurio("ancient")).setCategory("GOLEMANCY"));
        this.curios.add((new BaseCurio("eldritch")).setCategory("ELDRITCH").setWarp(IPlayerWarp.EnumWarpType.NORMAL, 1).setWarp(IPlayerWarp.EnumWarpType.TEMPORARY, 5));
        this.curios.add((new BaseCurio("knowledge")).setCategory("INFUSION"));
        this.curios.add((new BaseCurio("twisted")).setCategory("ARTIFICE"));
        this.curios.add(new RitesCurio());
        BaseCurio basic = new BaseCurio("basic");
        for (Aspect aspect : Aspect.getPrimalAspects()) basic.aspect(aspect, 15);
        this.curios.add(basic);
    }

    @Override
    public @NotNull String getTranslationKey(ItemStack itemStack) {
        return super.getTranslationKey() + "." + this.curios.get(itemStack.getMetadata()).getName().toLowerCase();
    }

    @Override
    public void getSubItems(@NotNull CreativeTabs tab, @NotNull NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            for (int meta = 0; meta != this.curios.size(); meta++) {
                items.add(new ItemStack(this, 1, meta));
            }
        }
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World worldIn, EntityPlayer player, @NotNull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!worldIn.isRemote && this.curios.get(stack.getMetadata()).onItemRightClick(worldIn, player, hand)) {
            player.sendStatusMessage(new TextComponentTranslation("tc.knowledge.gained").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE).setItalic(true)), true);
            if (!player.isCreative()) stack.shrink(1);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        int i = 0;
        for (BaseCurio curio : this.curios) {
            ModelLoader.setCustomModelResourceLocation(ItemsTC.curio, i++, new ModelResourceLocation(curio.getTexture().toString()));
        }
    }
}
