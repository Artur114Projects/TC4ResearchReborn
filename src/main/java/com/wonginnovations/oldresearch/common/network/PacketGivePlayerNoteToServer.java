package com.wonginnovations.oldresearch.common.network;

import com.wonginnovations.oldresearch.common.research.OldResearchManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.research.ResearchManager;


public class PacketGivePlayerNoteToServer implements IMessage {
    private String key;

    public PacketGivePlayerNoteToServer() {}

    public PacketGivePlayerNoteToServer(String key) {
        this.key = key;
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, this.key);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.key = ByteBufUtils.readUTF8String(buffer);
    }

    public static class HandlerGPN implements IMessageHandler<PacketGivePlayerNoteToServer, IMessage> {

        @Override
        public IMessage onMessage(PacketGivePlayerNoteToServer message, MessageContext ctx) {
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
                EntityPlayer player = ctx.getServerHandler().player;
                World world = ctx.getServerHandler().player.world;

                if (world == null || player == null) {
                    return;
                }
                if (ThaumcraftCapabilities.knowsResearchStrict(player, message.key)) {
                    return;
                }
                if (ResearchManager.doesPlayerHaveRequisites(player, message.key)) {
                    OldResearchManager.givePlayerResearchNote(world, player, message.key);
                } else {
                    player.sendMessage(new TextComponentTranslation("tc.researcherror"));
                }
            });
            return null;
        }

    }
}
