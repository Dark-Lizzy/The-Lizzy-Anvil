package com.darkliz.lizzyanvil.config;

import java.io.File;

import com.darkliz.lizzyanvil.LizzyAnvil;
import com.darkliz.lizzyanvil.packethandler.ConfigSyncPacket;
import com.darkliz.lizzyanvil.packethandler.RequestConfigSyncPacket;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public final class Config {

	public static File configFile;
	public static Configuration config;

    //Configuration categories
	public static final String CATEGORY_NOTE = "1: enchantment cost configuration notes";
    public static final String CATEGORY_ENCH_COSTS = "2: enchantment costs per level";
    public static final String CATEGORY_GENERAL = "3: general"; 
	
	
	//////////////// CONFIGURABLE FIELDS ///////////////////

	//General
	public static int breakChancePercent;
	public static int repairBonusPercent;
	public static int costLimit;
	public static int unenchantItemID;
	public static boolean heatRequired;
	
	//Enchantment Costs
	public static int protectionCost;
	public static int fireProtectionCost;
	public static int featherFallingCost;	
	public static int blastProtectionCost;	
	public static int projectileProtectionCost;
	public static int respirationCost;
	public static int aquaAffinityCost;
	public static int thornsCost;			
	public static int depthStriderCost;
	
	public static int sharpnessCost;	
	public static int smiteCost;
	public static int baneOfArthropodsCost;	
	public static int knockbackCost;	
	public static int fireAspectCost;
	public static int lootingCost;
	
	public static int efficiencyCost;	
	public static int silkTouchCost;
	public static int unbreakingCost;
	public static int fortuneCost;	
	
	public static int powerCost;	
	public static int punchCost;
	public static int flameCost;	
	public static int infinityCost;	
	
	public static int luckOfTheSeaCost;
	public static int lureCost;
	
	
	//////////////// DEFAULT VALUES ///////////////////////
	
	//General
	public static final int breakChancePercentDefault		= 0; 	//Vanilla = 12
	public static final int repairBonusPercentDefault		= 24; 	//Vanilla = 12
	public static final int costLimitDefault				= 0;	//vanilla = 40
	public static final int unenchantItemIDDefault			= Item.getIdFromItem(Items.redstone); //331
	public static final boolean heatRequiredDefault			= true;
	
	//Enchantment Costs					
	public static final int protectionCostDefault			= 1; //Max Level = 4 	//Vanilla = 1	ID = 0 		weight = 10	
	public static final int fireProtectionCostDefault		= 1; //Max Level = 4 	//Vanilla = 2	ID = 1 		weight = 5 		
	public static final int featherFallingCostDefault		= 1; //Max Level = 4 	//Vanilla = 2	ID = 2 		weight = 5 	
	public static final int blastProtectionCostDefault 		= 1; //Max Level = 4 	//Vanilla = 4	ID = 3 		weight = 2	
	public static final int projectileProtectionCostDefault	= 1; //Max Level = 4 	//Vanilla = 2	ID = 4 		weight = 5	
	public static final int respirationCostDefault			= 2; //Max Level = 3 	//Vanilla = 4	ID = 5 		weight = 2	
	public static final int aquaAffinityCostDefault			= 6; //Max Level = 1 	//Vanilla = 4	ID = 6 		weight = 2	
	public static final int thornsCostDefault				= 3; //Max Level = 3 	//Vanilla = 8	ID = 7 		weight = 1			
	public static final int depthStriderCostDefault			= 2; //Max Level = 3 	//Vanilla = 4	ID = 8 		weight = 2	
	
	public static final int sharpnessCostDefault			= 1; //Max Level = 5	//Vanilla = 1	ID = 16 	weight = 10 
	public static final int smiteCostDefault 				= 1; //Max Level = 5 	//Vanilla = 2	ID = 17 	weight = 5 	
	public static final int baneOfArthropodsCostDefault 	= 1; //Max Level = 5 	//Vanilla = 2	ID = 18 	weight = 5	
	public static final int knockbackCostDefault 			= 3; //Max Level = 2 	//Vanilla = 2	ID = 19 	weight = 5	
	public static final int fireAspectCostDefault 			= 3; //Max Level = 2 	//Vanilla = 4	ID = 20 	weight = 2	
	public static final int lootingCostDefault 				= 3; //Max Level = 3 	//Vanilla = 4	ID = 21 	weight = 2	
	
	public static final int efficiencyCostDefault 			= 1; //Max Level = 5 	//Vanilla = 1	ID = 32 	weight = 10	
	public static final int silkTouchCostDefault 			= 6; //Max Level = 1 	//Vanilla = 8	ID = 33 	weight = 1	
	public static final int unbreakingCostDefault 			= 2; //Max Level = 3 	//Vanilla = 2	ID = 34 	weight = 5	
	public static final int fortuneCostDefault				= 3; //Max Level = 3 	//Vanilla = 4	ID = 35 	weight = 2	
    
	public static final int powerCostDefault 				= 1; //Max Level = 5 	//Vanilla = 1	ID = 48 	weight = 10	
	public static final int punchCostDefault 				= 3; //Max Level = 2	//Vanilla = 4	ID = 49 	weight = 2		
	public static final int flameCostDefault 				= 6; //Max Level = 1 	//Vanilla = 4	ID = 50 	weight = 2	
	public static final int infinityCostDefault 			= 6; //Max Level = 1 	//Vanilla = 8	ID = 51 	weight = 1	
    
	public static final int luckOfTheSeaCostDefault 		= 2; //Max Level = 3 	//Vanilla = 4	ID = 61 	weight = 2 		
	public static final int lureCostDefault 				= 2; //Max Level = 3	//Vanilla = 4	ID = 62 	weight = 2
	
	

    ///////////////// Functions /////////////////////////////////////////////////
    
	/**
	 * Creates the config file for this mod then runs configSync()
	 * @param event
	 */
	public static void configInit(FMLPreInitializationEvent event){
		
		configFile = event.getSuggestedConfigurationFile();
		config = new Configuration(configFile);
        
		configSync();
	}
	
	/**
	 * Loads the configurable values from the config file and sets the configurable fields to those values. 
	 * If the values in the file are not valid values, they will be replaced with the default values. 
	 * For a new config file, this will populate the file. 
	 */
	public static void configSync()
	{
		config.load();
        Property prop;
        
        //System.out.println("Loading config values from file"); //Debug message
        
        //Enchantment Cost Notes
        prop = config.get(CATEGORY_NOTE, "note", "end");
        prop.comment = "The costs below are based on the vanilla values with adjustments to make it fit"
        		+ "\nthis mod's repair system and my perception of actual in-game value."
        		+ "\nThey represent a fair cost system overall. But your perception of in-game value may differ."
        		+ "\nChange the costs to whatever you like, but keep the following in mind:"
        		+ "\nMinimum acceptable value is 1. If 0 or less is used, the cost will be 1."
        		+ "\nA cost increase of +1 can increase the overall cost sharply, depending on max level."
        		+ "\nGo to Minecraft Wiki/Enchanting for enchantment level information."
        		+ "\nHigher costs may result in limiting the amount of enchantments an item can have if the"
        		+ "\ncost limit is enabled (default is no cost limit)."
        		+ "\n\nTo reset all values to default, simply delete this config file";
 
        //General
    	prop = config.get(CATEGORY_GENERAL, "1. Set Break Chance Percent", breakChancePercentDefault);
        prop.comment = "Break Chance Percent (0-100)\n0 = will never break\nVanilla value: 12";
        breakChancePercent =  prop.getInt(breakChancePercentDefault);
        
        prop = config.get(CATEGORY_GENERAL, "2. Set Repair Bonus Percent", repairBonusPercentDefault);
        prop.comment = "Repair Bonus Percent (0-100)\nVanilla value: 12";
        repairBonusPercent = prop.getInt(repairBonusPercentDefault);
        
        prop = config.get(CATEGORY_GENERAL, "3. Set Cost Limit", costLimitDefault);
        prop.comment = "Cost Limit\n0 = disabled (no cost limit)\nVanilla value: 40";
        costLimit = prop.getInt(costLimitDefault);
        
        prop = config.get(CATEGORY_GENERAL, "4. Set Unenchant Item ID", unenchantItemIDDefault);
        prop.comment = "Unenchant Item ID\nDefault: Redstone (331)\nsee Minecraft Wiki for IDs/Data Values (dec)";
        unenchantItemID =  prop.getInt(unenchantItemIDDefault);
        
        prop = config.get(CATEGORY_GENERAL, "5. Set Heat Requirement", heatRequiredDefault);
        prop.comment = "Heat Requirement\ntrue = heat required to work on anvil\nfalse = heat requirement disabled";
        heatRequired = prop.getBoolean(heatRequiredDefault);
        
        
        //Enchantment Costs
        protectionCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 			"Protection", 			protectionCostDefault, 			"");
        fireProtectionCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 		"Fire_Protection", 		fireProtectionCostDefault, 		"");
        featherFallingCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 		"Feather_Falling", 		featherFallingCostDefault, 		"");
        blastProtectionCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 	"Blast_Protection", 	blastProtectionCostDefault, 	"");
        projectileProtectionCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS,"Projectile_Protection",projectileProtectionCostDefault,"");
        respirationCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 		"Respiration", 			respirationCostDefault, 		"");
        aquaAffinityCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 		"Aqua_Affinity", 		aquaAffinityCostDefault, 		"");
        thornsCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 				"Thorns", 				thornsCostDefault, 				"");
        depthStriderCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 		"Depth_Strider", 		depthStriderCostDefault, 		"");
        
        sharpnessCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 			"Sharpness", 			sharpnessCostDefault, 			"");
        smiteCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 				"Smite", 				smiteCostDefault, 				"");
        baneOfArthropodsCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 	"Bane_Of_Arthropods", 	baneOfArthropodsCostDefault, 	"");
        knockbackCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 			"Knockback", 			knockbackCostDefault, 			"");
        fireAspectCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 			"Fire_Aspect", 			fireAspectCostDefault, 			"");
        lootingCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 			"Looting", 				lootingCostDefault, 			"");
        
        efficiencyCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 			"Efficiency", 			efficiencyCostDefault, 			"");
        silkTouchCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 			"Silk_Touch", 			silkTouchCostDefault, 			"");
        unbreakingCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 			"Unbreaking", 			unbreakingCostDefault, 			"");
        fortuneCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 			"Fortune", 				fortuneCostDefault, 			"");
        
        powerCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 				"Power", 				powerCostDefault, 				"");
        punchCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 				"Punch", 				punchCostDefault, 				"");
        flameCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 				"Flame", 				flameCostDefault, 				"");
        infinityCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 			"Infinity", 			infinityCostDefault, 			"");
        
        luckOfTheSeaCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 		"Luck_Of_The_Sea", 		luckOfTheSeaCostDefault, 		"");
        lureCost = setupCustomConfigInt(config, CATEGORY_ENCH_COSTS, 				"Lure", 				lureCostDefault, 				"");
        
        config.save();
	}
	
	/**
	 * Run from the client to ask the server to update the config values on that client. Will only initiate the sync if the server is a dedicated server.
	 */
	public static void sendConfigSyncRequest()
	{
		//System.out.println("--- sending config sync request to server ---"); //Debug message
		
		//Send a RequestConfigSyncPacket to the server
		LizzyAnvil.network.sendToServer(new RequestConfigSyncPacket());
	}
	
	/**
	 * Run from the server to send all the config values to the client.
	 * @param key
	 * @param value
	 * @param player - the client to send to
	 */
	public static void sendConfigToClient(EntityPlayerMP player)
	{
		//System.out.println("--- sending config values to client ---"); //Debug message
		
		//send a ConfigSyncPacket for each configuration property
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("breakChancePercent", breakChancePercent), player); //get the value of breakChancePercent on the server and send it to the client.
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("repairBonusPercent", repairBonusPercent), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("costLimit", costLimit), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("unenchantItemID", unenchantItemID), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("heatRequired", heatRequired ? 1 : 0), player);
		
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("protectionCost", protectionCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("fireProtectionCost", fireProtectionCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("featherFallingCost", featherFallingCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("blastProtectionCost", blastProtectionCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("projectileProtectionCost", projectileProtectionCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("respirationCost", respirationCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("aquaAffinityCost", aquaAffinityCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("thornsCost", thornsCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("depthStriderCost", depthStriderCost), player);
		
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("sharpnessCost", sharpnessCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("smiteCost", smiteCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("baneOfArthropodsCost", baneOfArthropodsCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("knockbackCost", knockbackCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("fireAspectCost", fireAspectCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("lootingCost", lootingCost), player);
		
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("efficiencyCost", efficiencyCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("silkTouchCost", silkTouchCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("unbreakingCost", unbreakingCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("fortuneCost", fortuneCost), player);
		
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("powerCost", powerCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("punchCost", punchCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("flameCost", flameCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("infinityCost", infinityCost), player);
		
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("luckOfTheSeaCost", luckOfTheSeaCost), player);
		LizzyAnvil.network.sendTo(new ConfigSyncPacket("lureCost", lureCost), player);
		
	}
	
	/**
	 * This is run on the client when it receives the packets sent by sendConfigToClient(). It updates the config values on the client to match the ones on the server. 
	 * @param key
	 * @param value
	 */
	public static void syncConfigToServer(String key, int value)
	{
		//System.out.println("--- config values from server received ---"); //Debug message
		
		if(key.contentEquals("breakChancePercent")) breakChancePercent = value; //set the variable breakChancePercent on the client to the incoming value (the value on the server)
		else if(key.contentEquals("repairBonusPercent")) repairBonusPercent = value;
		else if(key.contentEquals("costLimit")) costLimit = value;
		else if(key.contentEquals("unenchantItemID")) unenchantItemID = value;
		else if(key.contentEquals("heatRequired")) heatRequired = value == 0 ? false : true;
		
		else if(key.contentEquals("protectionCost")) protectionCost = value;
		else if(key.contentEquals("fireProtectionCost")) fireProtectionCost = value;
		else if(key.contentEquals("featherFallingCost")) featherFallingCost = value;
		else if(key.contentEquals("blastProtectionCost")) blastProtectionCost = value;
		else if(key.contentEquals("projectileProtectionCost")) projectileProtectionCost = value;
		else if(key.contentEquals("respirationCost")) respirationCost = value;
		else if(key.contentEquals("aquaAffinityCost")) aquaAffinityCost = value;
		else if(key.contentEquals("thornsCost")) thornsCost = value;
		else if(key.contentEquals("depthStriderCost")) depthStriderCost = value;
		
		else if(key.contentEquals("sharpnessCost")) sharpnessCost = value;
		else if(key.contentEquals("smiteCost")) smiteCost = value;
		else if(key.contentEquals("baneOfArthropodsCost")) baneOfArthropodsCost = value;
		else if(key.contentEquals("knockbackCost")) knockbackCost = value;
		else if(key.contentEquals("fireAspectCost")) fireAspectCost = value;
		else if(key.contentEquals("lootingCost")) lootingCost = value;
		
		else if(key.contentEquals("efficiencyCost")) efficiencyCost = value;
		else if(key.contentEquals("silkTouchCost")) silkTouchCost = value;
		else if(key.contentEquals("unbreakingCost")) unbreakingCost = value;
		else if(key.contentEquals("fortuneCost")) fortuneCost = value;
		
		else if(key.contentEquals("powerCost")) powerCost = value;
		else if(key.contentEquals("punchCost")) punchCost = value;
		else if(key.contentEquals("flameCost")) flameCost = value;
		else if(key.contentEquals("infinityCost")) infinityCost = value;
		
		else if(key.contentEquals("luckOfTheSeaCost")) luckOfTheSeaCost = value;
		else if(key.contentEquals("lureCost")) lureCost = value;
		
	}
	
	public static int setupCustomConfigInt(Configuration config, String category, String key, int defaultValue, String comment) {
		
		Property prop;
    	prop = config.get(category, key, defaultValue);
    	if(!comment.isEmpty()) prop.comment = comment;
    	return Math.max(prop.getInt(defaultValue), 1); //returns either the cost in the config file, the default if that is not an int, and 1 if that is an int lower than 1.
    }

}
