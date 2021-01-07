package com.zazsona.wearstatus;

import com.zazsona.wearstatus.client.WearBroadcastListener;
import com.zazsona.wearstatus.messages.PlayerStatusMessage;
import com.zazsona.wearstatus.client.WearConnector;
import com.zazsona.wearstatus.messages.WorldStatusMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("wearstatus")
public class WearStatusMod
{
    private static final Logger LOGGER = LogManager.getLogger();
    private PlayerStatusMessage lastPlayerStatus;
    private WorldStatusMessage lastWorldStatus;

    public WearStatusMod()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initialiseMod);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void initialiseMod(final FMLClientSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.addListener(this::sendPlayerUpdate);
        MinecraftForge.EVENT_BUS.addListener(this::sendWorldChangeUpdate);
        new Thread(() -> WearConnector.getInstance().startServer()).start();
        new Thread(() -> WearBroadcastListener.getInstance().startListener()).start();
        new Thread(() -> runWorldTimeLoop()).start();
    }

    private boolean isLocalPlayer(Entity entity)
    {
        if (entity instanceof PlayerEntity)
        {
            ClientPlayerEntity localPlayer = Minecraft.getInstance().player;
            if (localPlayer != null)
            {
                int localPlayerId = localPlayer.getEntityId();
                int entityId = entity.getEntityId();
                return (localPlayerId == entityId);
            }
        }
        return false;
    }

    @SubscribeEvent
    public void sendPlayerUpdate(final TickEvent.PlayerTickEvent event)
    {
        if (event.player != null && isLocalPlayer(event.player))
        {
            //In the event of any health changes, this does mean the message will be sent twice (b/c health change will be 0 on the next tick). It's lightweight, so not too bad, but worth being aware of.
            float healthChange = (lastPlayerStatus == null) ? 0 : event.player.getHealth()-lastPlayerStatus.getHealth();
            PlayerStatusMessage playerStatusMessage = new PlayerStatusMessage(event.player.getHealth(), healthChange, event.player.getMaxHealth(), event.player.getFoodStats().getFoodLevel());
            if (lastPlayerStatus == null || !lastPlayerStatus.equals(playerStatusMessage))
            {
                WearConnector.getInstance().sendMessage(playerStatusMessage);
                this.lastPlayerStatus = playerStatusMessage;
            }
        }
    }


    @SubscribeEvent
    public void sendWorldChangeUpdate(final EntityJoinWorldEvent event)
    {
        if (isLocalPlayer(event.getEntity()))
        {
            sendWorldUpdate();
        }
    }

    public void sendWorldUpdate()
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null)
        {
            World world = player.getEntityWorld();
            if (world != null)
            {
                WorldStatusMessage worldStatusMessage = new WorldStatusMessage(world.getDimensionKey().getLocation().getPath(), world.getDayTime());
                if (lastWorldStatus == null || !lastWorldStatus.equals(worldStatusMessage))
                {
                    WearConnector.getInstance().sendMessage(worldStatusMessage);
                    this.lastWorldStatus = worldStatusMessage;
                }
            }
        }
    }

    private void runWorldTimeLoop()
    {
        try
        {
            while (true)
            {
                Thread.sleep(1000);
                sendWorldUpdate();
            }
        }
        catch (InterruptedException e)
        {
            LOGGER.info("World update thread stopped.");
            e.printStackTrace();
        }
    }
}
