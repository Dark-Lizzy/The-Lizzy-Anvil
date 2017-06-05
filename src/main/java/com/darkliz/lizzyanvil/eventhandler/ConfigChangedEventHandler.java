package com.darkliz.lizzyanvil.eventhandler;

import com.darkliz.lizzyanvil.LizzyAnvil;
import com.darkliz.lizzyanvil.Reference;
import com.darkliz.lizzyanvil.config.Config;

import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigChangedEventHandler {
	
	
	//This happens when the 'done' button is pressed on a GuiConfig screen
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onConfigChange(OnConfigChangedEvent event)
	{
		if(event.modID.equals(Reference.MOD_ID))
		{
			Config.config.save();
			Config.configSync();
			
			//Only run the following code if a world is running and if running on a client; this should make it so the sync only runs when accessing the "mod options..." button from the pause menu
			if(event.isWorldRunning && LizzyAnvil.proxy.isClient())
			{
				Config.sendConfigSyncRequest(); //send an empty packet to the server that initiates the config sync (only initiates if it is a dedicated server)
			}
		}
	}
	
	
}
