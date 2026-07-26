package com.wonginnovations.oldresearch.common.container;

import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.common.network.PacketAspectPool;
import com.wonginnovations.oldresearch.common.research.storage.IOldResStorage;
import com.wonginnovations.oldresearch.common.tiles.TileDeconstructionTable;
import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.container.slot.SlotLimitedHasAspects;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class ContainerDeconstructionTable extends Container {
    private final TileDeconstructionTable table;
    private int lastBreakTime;

    public ContainerDeconstructionTable(InventoryPlayer inventoryPlayer, TileDeconstructionTable tileEntity) {
        this.table = tileEntity;
        this.addSlotToContainer(new SlotLimitedHasAspects(tileEntity, 0, 64, 16));

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlotToContainer(new Slot(inventoryPlayer, var5, 8 + var5 * 18, 142));
        }
    }

    @Override
    public boolean enchantItem(@NotNull EntityPlayer player, int button) {
        if (button == 1 && this.table.aspect != null) {
            IOldResStorage storage = OldResearchApi.oldResStorage(player);
            storage.addToAspectPool(this.table.aspect, 1);
            OldResearch.NETWORK.sendTo(new PacketAspectPool(this.table.aspect.getTag(), 1, storage.aspectCount(this.table.aspect)), (EntityPlayerMP) player);
            this.table.aspect = null;
            this.table.syncTile(false);
        }

        return false;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (IContainerListener listener : this.listeners) {
            if (this.lastBreakTime != this.table.breaktime) {
                listener.sendWindowProperty(this, 0, this.table.breaktime);
            }
        }

        this.lastBreakTime = this.table.breaktime;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        if (par1 == 0) {
            this.table.breaktime = par2;
        }
    }

    @Override
    public boolean canInteractWith(@NotNull EntityPlayer player) {
        return this.table.isUsableByPlayer(player);
    }

    @Override
    public @NotNull ItemStack transferStackInSlot(@NotNull EntityPlayer player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index != 0) {
                AspectList al = ThaumcraftCraftingManager.getObjectTags(itemstack1);
                if (al != null && al.size() > 0) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 28) {
                    if (!this.mergeItemStack(itemstack1, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 37 && !this.mergeItemStack(itemstack1, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 1, 37, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }
}