package com.wonginnovations.oldresearch.common.blocks;

import com.wonginnovations.oldresearch.client.renderer.item.TileDeconstructionTableItemRenderer;
import com.wonginnovations.oldresearch.common.tiles.TileDeconstructionTable;
import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTCDevice;

import java.lang.reflect.Field;
import java.util.Objects;

public class BlockDeconstructionTable extends BlockTCDevice {
    public final ItemBlock item;

    public BlockDeconstructionTable() {
        super(Material.WOOD, TileDeconstructionTable.class, "deconstruction_table");
        try {
            Field field = Impl.class.getDeclaredField("registryName");
            field.setAccessible(true);
            field.set(this, OldResearch.loc("deconstruction_table"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.setSoundType(SoundType.WOOD);
        this.item = (ItemBlock) new ItemBlock(this).setRegistryName(Objects.requireNonNull(this.getRegistryName()));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i != list.size(); i++) {
            ItemStack stack = list.get(i);
            if (stack.getItem() instanceof ItemBlock && (((ItemBlock) stack.getItem()).getBlock()) == BlocksTC.arcaneWorkbench) {
                list.add(i, new ItemStack(this));
                return;
            }
        }
        list.add(new ItemStack(this));
    }

    @SideOnly(Side.CLIENT)
    public void bindItemRenderer() {
        this.item.setTileEntityItemStackRenderer(new TileDeconstructionTableItemRenderer());
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (player.isSneaking()) {
                TileEntity tile = world.getTileEntity(pos);
                if (tile instanceof TileDeconstructionTable) {
                    return ((TileDeconstructionTable) tile).takeAspect(player);
                }
            } else {
                player.openGui(OldResearch.INSTANCE, 2, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileDeconstructionTable) {
            InventoryHelper.dropInventoryItems(world, pos, (TileDeconstructionTable) tileEntity);
        }

        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }
}
