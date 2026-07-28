package com.wonginnovations.oldresearch.common.network;

import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.common.items.ItemResearchNote;
import com.wonginnovations.oldresearch.main.OldResearch;
import com.wonginnovations.oldresearch.common.init.InitItems;
import com.wonginnovations.oldresearch.common.research.ResearchNoteData;
import com.wonginnovations.oldresearch.common.tiles.TileResearchTable;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.InventoryUtils;

public class PacketCopyPlayerNoteToServer implements IMessage, IMessageHandler<PacketCopyPlayerNoteToServer, IMessage> {
    private long pos;

    public PacketCopyPlayerNoteToServer() {
    }

    public PacketCopyPlayerNoteToServer(BlockPos pos) {
        this.pos = pos.toLong();
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeLong(this.pos);
    }

    public void fromBytes(ByteBuf buffer) {
        this.pos = buffer.readLong();
    }

    public IMessage onMessage(PacketCopyPlayerNoteToServer message, MessageContext ctx) {
        IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            public void run() {
                World world = ctx.getServerHandler().player.world;
                EntityPlayerMP player = ctx.getServerHandler().player;
                if(world == null || player == null) return;

                BlockPos blockPos = BlockPos.fromLong(message.pos);
                TileEntity te = world.getTileEntity(blockPos);
                if (!(te instanceof TileResearchTable)) return;

                ItemStack tools = ((IInventory) te).getStackInSlot(0);
                ItemStack note = ((IInventory) te).getStackInSlot(1);
                if (note.isEmpty() || note.getItem() != InitItems.RESEARCH_NOTE) return;

                boolean failed = false;
                if (tools.isEmpty()) {
                    player.sendMessage(new TextComponentString("§c" + I18n.format("researchnote.missing.tools")));
                    failed = true;
                } else if (!((TileResearchTable) te).canConsumeInkFromTable()) {
                    player.sendMessage(new TextComponentString("§c" + I18n.format("tile.researchtable.noink.0")));
                    player.sendMessage(new TextComponentString("§c" + I18n.format("tile.researchtable.noink.1")));
                    failed = true;
                }
                if (!InventoryUtils.isPlayerCarryingAmount(player, new ItemStack(Items.PAPER), true)) {
                    player.sendMessage(new TextComponentString("§c" + I18n.format("researchnote.missing.paper")));
                    failed = true;
                }
                ResearchNoteData data = ItemResearchNote.noteData(note);
                int cost = data.copies + 1;
                for (Aspect aspect : data.aspects.getAspects()) {
                    if (OldResearchApi.oldResStorage(player).aspectCount(aspect) < cost && ((TileResearchTable) te).bonusAspects.getAmount(aspect) < cost) {
                        player.sendMessage(new TextComponentString("§c" + I18n.format("tc.research.copy.failure", aspect.getName())));
                        failed = true;
                    }
                }
                if (!failed) {
                    ((TileResearchTable) te).consumeInkFromTable();
                    InventoryUtils.consumePlayerItem(player, new ItemStack(Items.PAPER), false, true);

                    for (Aspect aspect : data.aspects.getAspects()) {
                        if (OldResearchApi.oldResStorage(player).aspectCount(aspect) >= cost) {
                            OldResearchApi.oldResStorage(player).addToAspectPool(aspect, -cost);
                            OldResearch.NETWORK.sendTo(new PacketAspectPool(aspect.getTag(), 0, OldResearchApi.oldResStorage(player).aspectCount(aspect)), player);
                        } else {
                            ((TileResearchTable) te).bonusAspects.remove(aspect, cost);
                            player.world.notifyBlockUpdate(blockPos, world.getBlockState(blockPos), world.getBlockState(blockPos), 3);
                            te.markDirty();
                        }
                    }

                    data.copies += 1;
                    ItemResearchNote.setNoteData(note, data);

                    if(!player.inventory.addItemStackToInventory(note.copy())) {
                        ForgeHooks.onPlayerTossEvent(player, note.copy(), false);
                    }

                    player.inventoryContainer.detectAndSendChanges();
                    world.playSound(null, blockPos, SoundsTC.write, SoundCategory.PLAYERS, 0.75F, 1.0F);
                }
            }
        });
        return null;
    }
}
