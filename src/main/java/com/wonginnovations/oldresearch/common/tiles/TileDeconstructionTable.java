package com.wonginnovations.oldresearch.common.tiles;

import com.wonginnovations.oldresearch.common.util.AspectUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileDeconstructionTable extends TileThaumcraft implements ITickable, ISidedInventory {
    public ItemStack breakItem = ItemStack.EMPTY;
    public Aspect aspect;
    public int breaktime;

    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
        if (this.aspect != null) nbt.setString("Aspect", this.aspect.getTag());
        nbt.setTag("BreakItem", this.breakItem.writeToNBT(new NBTTagCompound()));
        nbt.setInteger("breaktime", this.breaktime);
        return nbt;
    }

    @Override
    public void readSyncNBT(NBTTagCompound nbt) {
        this.aspect = Aspect.getAspect(nbt.getString("Aspect"));
        this.breakItem = new ItemStack(nbt.getCompoundTag("BreakItem"));
        this.breaktime = nbt.getInteger("breaktime");
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.aspect = Aspect.getAspect(nbt.getString("Aspect"));
        this.breakItem = new ItemStack(nbt.getCompoundTag("BreakItem"));
        this.breaktime = nbt.getInteger("breaktime");
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        if (this.aspect != null) nbt.setString("Aspect", this.aspect.getTag());
        nbt.setTag("BreakItem", this.breakItem.writeToNBT(new NBTTagCompound()));
        nbt.setInteger("breaktime", this.breaktime);
        return nbt;
    }

    @SideOnly(Side.CLIENT)
    public int getBreakTimeScaled(int par1) {
        return this.breaktime * par1 / 40;
    }

    @Override
    public void update() {
        boolean flag1 = false;
        if (!this.world.isRemote) {
            if (this.breaktime == 0 && this.canBreak()) {
                this.breaktime = 40;
                flag1 = true;
            }

            if (this.breaktime > 0 && this.canBreak()) {
                --this.breaktime;
                if (this.breaktime == 0) {
                    this.breakItem();
                    flag1 = true;
                }
            } else {
                this.breaktime = 0;
            }
        }

        if (flag1) {
            this.syncTile(false);
            this.markDirty();
        }
    }

    private boolean canBreak() {
        if (!this.breakItem.isEmpty() && this.aspect == null) {
            AspectList al = ThaumcraftCraftingManager.getObjectTags(this.breakItem);
            return al != null && al.size() != 0;
        } else {
            return false;
        }
    }

    public void breakItem() {
        if (this.canBreak()) {
            AspectList al = ThaumcraftCraftingManager.getObjectTags(this.breakItem);
            AspectList primal = AspectUtils.reduceToPrimals(al);
            if (this.world.rand.nextInt(80) < primal.visSize()) {
                this.aspect = primal.getAspects()[this.world.rand.nextInt(primal.getAspects().length)];
            }

            this.breakItem.shrink(1);
        }
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull EnumFacing side) {
        return new int[] {0};
    }

    @Override
    public boolean canInsertItem(int index, @NotNull ItemStack itemStackIn, @NotNull EnumFacing direction) {
        return index == 0 && this.isItemValidForSlot(0, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, @NotNull ItemStack stack, @NotNull EnumFacing direction) {
        return index == 0 && this.isItemValidForSlot(0, stack);
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.breakItem.isEmpty();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int index) {
        if (index == 0) {
            return this.breakItem;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack decrStackSize(int index, int count) {
        if (index == 0) {
            return this.breakItem.splitStack(count);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeStackFromSlot(int index) {
        if (index == 0) {
            ItemStack ret = this.breakItem;
            this.breakItem = ItemStack.EMPTY;
            return ret;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, @NotNull ItemStack stack) {
        if (index == 0) {
            this.breakItem = stack;
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(@NotNull EntityPlayer player) {
        return this.world.getTileEntity(this.getPos()) == this && player.getDistanceSqToCenter(this.getPos()) <= (double) 64.0F;
    }

    @Override
    public void openInventory(@NotNull EntityPlayer player) {}

    @Override
    public void closeInventory(@NotNull EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, @NotNull ItemStack stack) {
        AspectList al = ThaumcraftCraftingManager.getObjectTags(stack);
        return al != null && al.size() > 0;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.breakItem = ItemStack.EMPTY;
    }

    @Override
    public @NotNull ITextComponent getDisplayName() {
        return new TextComponentString(this.getName());
    }

    @Override
    public @NotNull String getName() {
        return "Deconstruction Table";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }
}
