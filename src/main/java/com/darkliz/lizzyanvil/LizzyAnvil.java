package com.darkliz.lizzyanvil;

import com.darkliz.lizzyanvil.config.Config;
import com.darkliz.lizzyanvil.eventhandler.ConfigChangedEventHandler;
import com.darkliz.lizzyanvil.eventhandler.PlayerInteractEventHandler;
import com.darkliz.lizzyanvil.eventhandler.PlayerLogInOutEventHandler;
import com.darkliz.lizzyanvil.gui.GuiHandler;
import com.darkliz.lizzyanvil.init.LizzyAnvilBlocks;
import com.darkliz.lizzyanvil.init.LizzyAnvilCraftingRecipes;
import com.darkliz.lizzyanvil.packethandler.AnvilRenamePacket;
import com.darkliz.lizzyanvil.packethandler.ConfigSyncPacket;
import com.darkliz.lizzyanvil.packethandler.HasHeatPacket;
import com.darkliz.lizzyanvil.packethandler.RequestConfigSyncPacket;
import com.darkliz.lizzyanvil.proxy.CommonProxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;



@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, acceptableRemoteVersions = "[1.0]", acceptedMinecraftVersions = "[1.8, )", 
dependencies = "required-after:Forge@[11.14.4.1563,)", guiFactory = "com.darkliz.lizzyanvil.config.GuiFactoryLizzyAnvil")
public class LizzyAnvil {
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;
	
	@Instance(Reference.MOD_ID)
	public static LizzyAnvil instance;
	
	//Lizzy Anvil Gui ID
	public static final int guiIDLizzyAnvil = 0;
	
	//Network
    public static SimpleNetworkWrapper network;

	
//Initialization
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Config.configInit(event);
		
		LizzyAnvilBlocks.init();
		LizzyAnvilCraftingRecipes.doCraftingRecipes();
		
		//Event Handlers
		MinecraftForge.EVENT_BUS.register(new PlayerInteractEventHandler()); //effectively prevents the anvil gui and container from being opened, but still acts like a container for placing blocks on it (must be sneaking)
		FMLCommonHandler.instance().bus().register(new ConfigChangedEventHandler());
		FMLCommonHandler.instance().bus().register(new PlayerLogInOutEventHandler());
		
		//Network
		network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
		network.registerMessage(AnvilRenamePacket.Handler.class, AnvilRenamePacket.class, 0, Side.SERVER);
		network.registerMessage(HasHeatPacket.Handler.class, HasHeatPacket.class, 1, Side.CLIENT);
		network.registerMessage(ConfigSyncPacket.Handler.class, ConfigSyncPacket.class, 2, Side.CLIENT);
		network.registerMessage(RequestConfigSyncPacket.Handler.class, RequestConfigSyncPacket.class, 3, Side.SERVER);	

	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		//Register Lizzy Anvil Block Renders
		proxy.registerRenders();
		
		//Gui Handler
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		
	}
	
	
}
