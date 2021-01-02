package com.zazsona.wearstatus;

import com.zazsona.wearstatus.messages.PlayerStatusMessage;
import com.zazsona.wearstatus.client.WearConnector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public WearStatusMod()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initialiseMod);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void initialiseMod(final FMLClientSetupEvent event)
    {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        new Thread(() -> WearConnector.getInstance().startServer()).start();
        MinecraftForge.EVENT_BUS.addListener(this::registerDamage);
        MinecraftForge.EVENT_BUS.addListener(this::registerHeal);
        MinecraftForge.EVENT_BUS.addListener(this::registerSpawn);
        MinecraftForge.EVENT_BUS.addListener(this::registerEat);
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
    public void registerDamage(final LivingDamageEvent event)
    {
        if (isLocalPlayer(event.getEntity()))
        {
            PlayerEntity playerEntity = (PlayerEntity) event.getEntity();
            float currentHealth = Math.min(Math.max(0.0f, playerEntity.getHealth()-event.getAmount()), playerEntity.getMaxHealth()); //Clamp between 0 and MaxHealth
            PlayerStatusMessage playerStatusMessage = new PlayerStatusMessage(currentHealth, -event.getAmount(), playerEntity.getMaxHealth(), playerEntity.getFoodStats().getFoodLevel());
            WearConnector.getInstance().sendMessage(playerStatusMessage);
        }

    }

    @SubscribeEvent
    public void registerHeal(final LivingHealEvent event)
    {
        if (isLocalPlayer(event.getEntity()))
        {
            PlayerEntity playerEntity = (PlayerEntity) event.getEntity();
            float currentHealth = Math.min(Math.max(0.0f, playerEntity.getHealth()-event.getAmount()), playerEntity.getMaxHealth()); //Clamp between 0 and MaxHealth
            PlayerStatusMessage playerStatusMessage = new PlayerStatusMessage(currentHealth, event.getAmount(), playerEntity.getMaxHealth(), playerEntity.getFoodStats().getFoodLevel());
            WearConnector.getInstance().sendMessage(playerStatusMessage);
        }
    }

    @SubscribeEvent
    public void registerSpawn(final PlayerEvent.PlayerRespawnEvent event)
    {
        if (isLocalPlayer(event.getEntity()))
        {
            PlayerStatusMessage playerStatusMessage = new PlayerStatusMessage(20.0f, 20.0f, 20.0f, 20);
            WearConnector.getInstance().sendMessage(playerStatusMessage);
        }
    }

    @SubscribeEvent
    public void registerEat(final LivingEntityUseItemEvent.Finish event)
    {
        if (isLocalPlayer(event.getEntity()))
        {
            PlayerEntity playerEntity = (PlayerEntity) event.getEntity();
            PlayerStatusMessage playerStatusMessage = new PlayerStatusMessage(playerEntity.getHealth(), 0.0f, playerEntity.getMaxHealth(), playerEntity.getFoodStats().getFoodLevel());
            WearConnector.getInstance().sendMessage(playerStatusMessage);
        }
    }
}
