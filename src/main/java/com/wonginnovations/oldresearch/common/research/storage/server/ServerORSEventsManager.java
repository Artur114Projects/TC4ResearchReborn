package com.wonginnovations.oldresearch.common.research.storage.server;

import com.artur114.bananalib.mc.cap.BananaCapProv;
import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.common.init.InitCapabilities;
import com.wonginnovations.oldresearch.common.research.storage.OldResStorage;
import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class ServerORSEventsManager {
    public void attachCapabilitiesEventEntity(AttachCapabilitiesEvent<Entity> e) {
        e.addCapability(OldResearch.loc("research_storage"), new BananaCapProv<>(new OldResStorage((EntityPlayer) e.getObject()), InitCapabilities.OLD_RES_STORAGE));
    }

    public void playerEventPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent e) {
        OldResearchApi.oldResStorage(e.player).sync();
    }

    public void playerEventPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent e) {
        OldResearchApi.oldResStorage(e.player).sync();
    }

    public void playerEventPlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent e) {
        OldResearchApi.oldResStorage(e.player).sync();
    }

    public void playerEventClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone e) {
        try {
            NBTTagCompound nbt = OldResearchApi.oldResStorage(e.getOriginal()).serializeNBT();
            OldResearchApi.oldResStorage(e.getEntityPlayer()).deserializeNBT(nbt);
        } catch (Exception var3) {
            OldResearch.LOGGER.error("Could not clone player [{}] knowledge", e.getOriginal().getName(), var3);
        }
    }
}
