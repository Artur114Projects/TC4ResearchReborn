package com.wonginnovations.oldresearch.common.network;

import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.common.research.storage.IOldResStorage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSyncAspects extends NBTPacketBase {
    public PacketSyncAspects() {}

    public PacketSyncAspects(IOldResStorage storage) {
        this.nbt = storage.serializeNBT();
    }

    public static class HandlerSA implements IMessageHandler<PacketSyncAspects, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSyncAspects message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> OldResearchApi.oldResStorage(Minecraft.getMinecraft().player).deserializeNBT(message.nbt));
            return null;
        }
    }
}
