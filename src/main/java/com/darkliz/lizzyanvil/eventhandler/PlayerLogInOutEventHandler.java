package com.darkliz.lizzyanvil.eventhandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.darkliz.lizzyanvil.LizzyAnvil;
import com.darkliz.lizzyanvil.Reference;
import com.darkliz.lizzyanvil.config.Config;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class PlayerLogInOutEventHandler {

	private static final Logger logger = LogManager.getLogger();
	
	//It seems the PlayerLoggedInEvent and PlayerLoggedOutEvent events only run server side; the player is recognized as a EntityPlayerMP even when joining/leaving a SP world.
	
	@SubscribeEvent
	public void onLogIn(PlayerLoggedInEvent event)
	{
		EntityPlayer player = event.player;
		World world = player.worldObj;
		
		//Only run config sync if this is a dedicated server
		if(LizzyAnvil.proxy.isServer())
		{
			if(player instanceof EntityPlayerMP)
			{
				logger.info("Synchronizing player client to server configuration values for mod: " + Reference.MOD_ID);
				Config.sendConfigToClient( (EntityPlayerMP) player);
			}
		}
	}
	
	@SubscribeEvent
	public void onLogOut(PlayerLoggedOutEvent event)
	{
		
	}
	
	//This runs on the client when the player disconnects.
	@SubscribeEvent
	public void onClientDisconnect(ClientDisconnectionFromServerEvent event)
	{
		if(!event.manager.isLocalChannel())//only do this if not on in SP
		{
			logger.info("Re-synchronizing player client to client configuration values for mod: " + Reference.MOD_ID);
			Config.configSync();
		}
	}
	
	
	
}
