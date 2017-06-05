package com.darkliz.lizzyanvil.container;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.darkliz.lizzyanvil.config.Config;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemCarrotOnAStick;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
public class AnvilMethods {

	
	/*
	 * HEAT SOURCE DETECTION METHODS START HERE
	 */
	
	/**
	 * Checks within 3 blocks (taxicab) of the anvil to see if there is any fire or lava
	 * @param world
	 * @param pos
	 * @return
	 */
	public static boolean isHeatPresent(World world, BlockPos pos) {
		
		if (!world.isRemote)
        {
			//System.out.println("checking for heat source");
			BlockPos scanYPos, scanPos1;
			EnumFacing facing;
			
			for(int y = -1; y <= 1; ++y)
			{
				scanYPos = pos.add(0, y, 0);
				for(int i = 0; i < 4; ++i)//loop through facings
				{
					facing = EnumFacing.getHorizontal(i);
					for(int j = 1; j <= 3; ++j)
					{
						int k;
						if(j == 1) k = 2;
						else if (j == 2) k = 1;
						else k = 0;
						
						scanPos1 = scanYPos.offset(facing, j);
						//do checks at scanpos1
						if(isBlockValidHeatSource(world.getBlockState(scanPos1).getBlock(), world, pos, scanPos1, y))
						{
							return true;
						}

						EnumFacing rotatedFacing = facing.rotateYCCW();
						BlockPos scanPos2;
						for(int l = 1; l <= k; ++l)
						{
							scanPos2 = scanPos1.offset(rotatedFacing, l);
							//do checks at scanpos2
							if(isBlockValidHeatSource(world.getBlockState(scanPos2).getBlock(), world, pos, scanPos2, y))
							{
								return true;
							}
						}
					}
				}
			}
        }
		
		return false;
	}
	
	/**
	 * Checks if the block is a heat source and calls isHeatAccessible to see if there is a clear line of sight to the anvil.
	 * @param block
	 * @param world
	 * @param anvilPos
	 * @param heatPos
	 * @param yOffset
	 * @return
	 */
	public static boolean isBlockValidHeatSource(Block block, World world, BlockPos anvilPos, BlockPos heatPos, int yOffset) {
		
		if(block == Blocks.lava && yOffset <= 0)
		{
			//System.out.println("Found lava at: " + heatPos);
			//determine if there is an appropriate line of sight from the anvil to the heat source
			if(isHeatAccessible(world, anvilPos, heatPos, 1))
			{
				return true;
			}
		}
		else if(block == Blocks.fire && yOffset >= 0)
		{
			//System.out.println("Found fire at: " + heatPos);
			//determine if there is an appropriate line of sight from the anvil to the heat source
			if(isHeatAccessible(world, anvilPos, heatPos, 0))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks the blocks between the anvil and the heat source to make sure there is a line of sight between the two, with exceptions for
	 * certain types of blocks like torches, redstone wires, carpets, etc.
	 * @param world
	 * @param anvilPos
	 * @param heatPos
	 * @param accessCheckOffset 1 for lava, 0 for fire
	 * @return
	 */
	public static boolean isHeatAccessible(World world, BlockPos anvilPos, BlockPos heatPos, int accessCheckOffset) {
		
		EnumFacing anvilToHeat = getDirectionTo(anvilPos, heatPos);
		EnumFacing heatToAnvil = getDirectionTo(heatPos, anvilPos);
		
		BlockPos posToCheckAnvil = new BlockPos(anvilPos.getX(), heatPos.getY() + accessCheckOffset, anvilPos.getZ()).offset(anvilToHeat); //the position one block from the anvil in the direction of the heat source, one level above if lava
		BlockPos posToCheckHeat = heatPos.add(0, accessCheckOffset, 0).offset(heatToAnvil); //the position one block from the heat source in the direction of the anvil, one level above if lava
		BlockPos posAboveLava = heatPos.up(); //the position above the heat source, only used for lava
		
		//First, if lava, check if the block above it is accessible - if not, stop checking
		if(accessCheckOffset == 1 && !checkHeatAccess(world, posAboveLava, true)) //accessCheckOffset 1 is for lava
		{
			//System.out.println("something is above the lava"); //Debug message
			return false;
		}
		//Next, check if the blocks between the heat and anvil are accessible
		int xDiff = Math.abs(anvilPos.getX() - heatPos.getX());
		int zDiff = Math.abs(anvilPos.getZ() - heatPos.getZ());
		if(xDiff == 1 && zDiff == 1) //if heat is one away from the anvil diagonally
		{
			if(checkHeatAccess(world, posToCheckAnvil, false) || checkHeatAccess(world, posToCheckHeat, false)) 
			{
				return true;
			}
		}
		else if(xDiff == 1 && zDiff == 2 || xDiff == 2 && zDiff == 1)//if heat is 1 away on one axis and 2 away on the other axis
		{
			if((checkHeatAccess(world, posToCheckHeat, false) 
					&& (checkHeatAccess(world, posToCheckHeat.offset(heatToAnvil), false) || checkHeatAccess(world, posToCheckAnvil, false)))
					|| (checkHeatAccess(world, posToCheckAnvil, false) && checkHeatAccess(world, posToCheckAnvil.offset(anvilToHeat), false))) 
			{
				return true;
			}	
		}
		else
		{
			if(checkHeatAccess(world, posToCheckAnvil, false) && checkHeatAccess(world, posToCheckHeat, false)) 
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Check the position pos and return true if the block at pos doesn't block access to the heat source
	 * @param world
	 * @param pos
	 * @return
	 */
	public static boolean checkHeatAccess(World world, BlockPos pos, boolean isLavaUpPos){
		
		Block block = world.getBlockState(pos).getBlock();
		Material material = block.getMaterial();
		
		if(material == Material.air)
		{
			return true;
		}
		if(block.isOpaqueCube() || block.isSolidFullCube())
		{
			return false;
		}
		if(material == Material.glass || material == Material.piston || material == Material.leaves || material == Material.web || material == Material.cactus || material == Material.portal)
		{
			return false;
		}
		if(block instanceof BlockStairs || block instanceof BlockWall || block instanceof BlockFence || block instanceof BlockPane)
		{
			return false;
		}
		if(isLavaUpPos && material == Material.carpet || block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockBed  || block instanceof BlockEnchantmentTable
				|| block instanceof BlockBrewingStand || block instanceof BlockCauldron || block instanceof BlockDaylightDetector)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Get the direction from fromDirection to toDirection, only considers horizontal directions
	 * @param fromDirection
	 * @param toDirection
	 * @return EnumFacing (north, east, south, west)
	 */
	public static EnumFacing getDirectionTo(BlockPos fromDirection, BlockPos toDirection) {
		
		int xVec = toDirection.getX() - fromDirection.getX();
		int zVec = toDirection.getZ() - fromDirection.getZ();

		return EnumFacing.getFacingFromVector((float)xVec, (float)0, (float)zVec);
	}
	
	
	
	/*
	 * ANVIL MECHANIC METHODS START HERE
	 */
	
	/**
	 * Compares the displayName with the names of all the enchantments on map1 (without the numerals). Returns the enchantment ID if there is a match, otherwise returns -1.
	 * @param map1
	 * @param displayName
	 * @return
	 */
	public static int getMatchingEnchantmentIDByName(Map map1, String displayName)
	{
		int matchingID = -1;
		
		Iterator iterator = map1.keySet().iterator();
        while(iterator.hasNext())
        {
        	int enchantmentID = ((Integer)iterator.next()).intValue();
        	Enchantment enchantment = Enchantment.getEnchantmentById(enchantmentID);
            int enchantmentLevel = ((Integer)map1.get(Integer.valueOf(enchantmentID))).intValue();
            
            String enchantmentName = getNameWithoutNumerals(enchantment, enchantmentLevel);
            
            //check if the display name is the same as the enchantment name, ignoring case
            if(displayName.equalsIgnoreCase(enchantmentName)) 
            {
            	matchingID = enchantmentID;
            }
        }
		return matchingID;
	}
	
	/**
	 * Gets the name of the enchantment from getTranslatedName(int level) and removes the numeral suffix
	 * @param enchantment
	 * @param enchantmentLevel
	 * @return String enchantmentName (without the numerals)
	 */
	public static String getNameWithoutNumerals(Enchantment enchantment, int enchantmentLevel) {
		
		String enchantmentName = enchantment.getTranslatedName(enchantmentLevel);
	    
	    if(enchantmentName.endsWith(" I")) enchantmentName = StringUtils.removeEnd(enchantmentName, " I");
	    else if(enchantmentName.endsWith(" II")) enchantmentName = StringUtils.removeEnd(enchantmentName, " II");
	    else if(enchantmentName.endsWith(" III")) enchantmentName = StringUtils.removeEnd(enchantmentName, " III");
	    else if(enchantmentName.endsWith(" IV")) enchantmentName = StringUtils.removeEnd(enchantmentName, " IV");
	    else if(enchantmentName.endsWith(" V")) enchantmentName = StringUtils.removeEnd(enchantmentName, " V");
    
	    return enchantmentName;
	}

	/**
	 * Gets the cost of enchantments added to the output item, omitting the cost of any additional enchantments on the book that do not go on the item or are redundant duplicates
	 * @param itemMap
	 * @param bookMap
	 * @return
	 */
	public static int getEnchantWithBookCost(Map inputItemMap, Map outputItemMap, Map bookMap) {
		int baseCost = 0; 
		
		Iterator iterator1 = bookMap.keySet().iterator();
        while(iterator1.hasNext())
        {
        	int bookEnchantmentID = ((Integer)iterator1.next()).intValue();
        	
            Iterator iterator2 = outputItemMap.keySet().iterator();
            while(iterator2.hasNext())
            {
            	int itemEnchantmentID = ((Integer)iterator2.next()).intValue();
            	//if the enchantment on the book is also on the output item
            	if(bookEnchantmentID == itemEnchantmentID)
            	{
            		Enchantment bookEnchantment = Enchantment.getEnchantmentById(bookEnchantmentID);
                    int bookEnchantmentLevel = ((Integer)bookMap.get(Integer.valueOf(bookEnchantmentID))).intValue();
                    int enchantmentCost = getEnchantmentCost(bookEnchantment);
                    
                    //if the enchantment is also on the input item
                    if(inputItemMap.containsKey(Integer.valueOf(bookEnchantmentID)))
                    {
                    	int inputItemEnchantmentLevel = ((Integer)inputItemMap.get(Integer.valueOf(bookEnchantmentID))).intValue();
                    	//if the level on the input and the book are the same and not max level the level on the output will be increased; apply cost
                    	if(inputItemEnchantmentLevel == bookEnchantmentLevel && bookEnchantmentLevel != bookEnchantment.getMaxLevel())
                    	{
                    		baseCost += enchantmentCost * bookEnchantmentLevel;
                    	}
                    	else //the levels are not the same or they are max level
                    	{
                    		//if the book level is higher than the item level, the book increases the level on the output; apply cost
                    		if(bookEnchantmentLevel > inputItemEnchantmentLevel)
                    		{
                    			baseCost += enchantmentCost * bookEnchantmentLevel;
                    		}
                    	}
                    }
                    else //the enchantment is not also on the output item, which means it came from the book; apply cost
                    {
                    	baseCost += enchantmentCost * bookEnchantmentLevel;
                    }
            	}
            }
        }

		return baseCost;
	}
	
	/**
	 * Gets the total cost for all the enchantments on Map map1
	 * @param map1 - a Map with enchantments (ID, Level) on it
	 * @return baseCost = enchantmentCost * enchantmentLevel per enchantment
	 */
	public static int getTotalEnchantmentCost(Map map1) {
		int baseCost = 0;

		Iterator iterator = map1.keySet().iterator();
        while(iterator.hasNext())
        {
        	int enchantmentID = ((Integer)iterator.next()).intValue();
        	Enchantment enchantment = Enchantment.getEnchantmentById(enchantmentID);
            int enchantmentLevel = ((Integer)map1.get(Integer.valueOf(enchantmentID))).intValue();
            int enchantmentCost = getEnchantmentCost(enchantment);
            
            baseCost += enchantmentCost * enchantmentLevel;
        }
		return baseCost;
	}
	
	/**
	 * Checks to see if there are any enchantments on book that are compatible with the item itemStack
	 * @param itemStack
	 * @param book
	 * @return true if the book has at least one compatible enchantment, otherwise false
	 */
	public static boolean isBookCompatibleWithItem(ItemStack itemStack, ItemStack book){
		
		Item item = itemStack.getItem();
		boolean isShovel = false, isPickaxe = false, isAxe = false, isHoe = false, isSword = false, 
				isHelmet = false, isChestplate = false, isLeggings = false, isBoots = false,
				isFishingRod = false, isShears = false, isBow = false, isLighter = false, isCarrotOnStick = false;
		
		if(itemStack.getItem() instanceof ItemSpade) isShovel = true;
    	else if(item instanceof ItemPickaxe) isPickaxe = true;
    	else if(item instanceof ItemAxe) isAxe = true;
    	else if(item instanceof ItemHoe) isHoe = true;
    	else if(item instanceof ItemSword) isSword = true;
    	else if(item instanceof ItemArmor)
    	{	
    		if(item.isValidArmor(itemStack, 0, null)) isHelmet = true; //Helmet
    		else if(item.isValidArmor(itemStack, 1, null)) isChestplate = true; //Chestplate
    		else if(item.isValidArmor(itemStack, 2, null)) isLeggings = true; //Leggings
    		else if(item.isValidArmor(itemStack, 3, null)) isBoots = true; //Boots		
    	}
    	else if(item instanceof ItemFishingRod) isFishingRod = true;
    	else if(item instanceof ItemShears) isShears = true;
    	else if(item instanceof ItemBow) isBow = true;
    	else if(item instanceof ItemFlintAndSteel) isLighter = true;
    	else if(item instanceof ItemCarrotOnAStick) isCarrotOnStick = true;
		

		Map map = EnchantmentHelper.getEnchantments(book);
		Iterator iterator = map.keySet().iterator();
        while(iterator.hasNext())
        {
        	int ID = ((Integer)iterator.next()).intValue();
        	Enchantment ench = Enchantment.getEnchantmentById(ID);

        	//List of accepted items by enchantment
        	if(		ench == Enchantment.protection && (isHelmet || isChestplate || isLeggings || isBoots)) return true;
        	else if(ench == Enchantment.fireProtection && (isHelmet || isChestplate || isLeggings || isBoots)) return true;
        	else if(ench == Enchantment.featherFalling && isBoots) return true;
        	else if(ench == Enchantment.blastProtection && (isHelmet || isChestplate || isLeggings || isBoots)) return true;
        	else if(ench == Enchantment.projectileProtection && (isHelmet || isChestplate || isLeggings || isBoots)) return true;
        	else if(ench == Enchantment.respiration && isHelmet) return true;
        	else if(ench == Enchantment.aquaAffinity && isHelmet) return true;
        	else if(ench == Enchantment.thorns && (isHelmet || isChestplate || isLeggings || isBoots)) return true;
        	else if(ench == Enchantment.depthStrider && isBoots) return true;
        	else if(ench == Enchantment.sharpness && (isSword || isAxe)) return true;
        	else if(ench == Enchantment.smite && (isSword || isAxe)) return true;
        	else if(ench == Enchantment.baneOfArthropods && (isSword || isAxe)) return true;
        	else if(ench == Enchantment.knockback && isSword) return true;
        	else if(ench == Enchantment.fireAspect && isSword) return true;
        	else if(ench == Enchantment.looting && isSword) return true;
        	
        	else if(ench == Enchantment.efficiency && (isShovel || isPickaxe || isAxe)) return true;
        	else if(ench == Enchantment.silkTouch && (isShovel || isPickaxe || isAxe)) return true;
        	else if(ench == Enchantment.unbreaking && (isShovel || isPickaxe || isAxe || isSword || isBow ||
        												isHelmet || isChestplate || isLeggings || isBoots || 
        												isFishingRod || isHoe || isShears || isLighter || isCarrotOnStick)) return true;
        	else if(ench == Enchantment.fortune && (isShovel || isPickaxe || isAxe)) return true;
        	
        	else if(ench == Enchantment.power && isBow) return true;
        	else if(ench == Enchantment.punch && isBow) return true;
        	else if(ench == Enchantment.flame && isBow) return true;
        	else if(ench == Enchantment.infinity && isBow) return true;
        	
        	else if(ench == Enchantment.luckOfTheSea && isFishingRod) return true;
        	else if(ench == Enchantment.lure && isFishingRod) return true; 	
        }    
		return false;
	}	
	
	/**
	 * Gets the cost per level of the enchantment from a cost table
	 * @param enchantment
	 * @return int cost
	 */
	public static int getEnchantmentCost(Enchantment enchantment) {
		int cost = 0;
		
		if		(enchantment == Enchantment.protection) 			cost = Config.protectionCost;			//ID = 0 	weight = 10 	Max Level = 4	classification = 0
		else if	(enchantment == Enchantment.fireProtection) 		cost = Config.fireProtectionCost; 		//ID = 1 	weight = 5 		Max Level = 4	classification = 1
		else if	(enchantment == Enchantment.featherFalling) 		cost = Config.featherFallingCost; 		//ID = 2 	weight = 5 		Max Level = 4	classification = 2
		else if	(enchantment == Enchantment.blastProtection) 		cost = Config.blastProtectionCost; 		//ID = 3 	weight = 2 		Max Level = 4	classification = 3
		else if	(enchantment == Enchantment.projectileProtection)	cost = Config.projectileProtectionCost; //ID = 4 	weight = 5 		Max Level = 4	classification = 4
		else if	(enchantment == Enchantment.respiration) 			cost = Config.respirationCost; 			//ID = 5 	weight = 2		Max Level = 3	
		else if	(enchantment == Enchantment.aquaAffinity) 			cost = Config.aquaAffinityCost; 		//ID = 6 	weight = 2		Max Level = 1	
		else if	(enchantment == Enchantment.thorns) 				cost = Config.thornsCost; 				//ID = 7 	weight = 1		Max Level = 3			
		else if	(enchantment == Enchantment.depthStrider) 			cost = Config.depthStriderCost; 		//ID = 8 	weight = 2		Max Level = 3	
		
		else if	(enchantment == Enchantment.sharpness) 				cost = Config.sharpnessCost; 			//ID = 16 	weight = 10 	Max Level = 5	classification = 0
		else if	(enchantment == Enchantment.smite) 					cost = Config.smiteCost; 				//ID = 17 	weight = 5 		Max Level = 5	classification = 1
		else if	(enchantment == Enchantment.baneOfArthropods) 		cost = Config.baneOfArthropodsCost; 	//ID = 18 	weight = 5 		Max Level = 5	classification = 2
		else if	(enchantment == Enchantment.knockback) 				cost = Config.knockbackCost; 			//ID = 19 	weight = 5		Max Level = 2	
		else if	(enchantment == Enchantment.fireAspect) 			cost = Config.fireAspectCost; 			//ID = 20 	weight = 2		Max Level = 2	
		else if	(enchantment == Enchantment.looting) 				cost = Config.lootingCost; 				//ID = 21 	weight = 2 		Max Level = 3	EnumEnchantmentType.WEAPON
		
		else if	(enchantment == Enchantment.efficiency) 			cost = Config.efficiencyCost; 			//ID = 32 	weight = 10		Max Level = 5	
		else if	(enchantment == Enchantment.silkTouch) 				cost = Config.silkTouchCost; 			//ID = 33 	weight = 1		Max Level = 1	
		else if	(enchantment == Enchantment.unbreaking) 			cost = Config.unbreakingCost; 			//ID = 34 	weight = 5		Max Level = 3	
		else if	(enchantment == Enchantment.fortune) 				cost = Config.fortuneCost; 				//ID = 35 	weight = 2 		Max Level = 3	EnumEnchantmentType.DIGGER
	    
		else if	(enchantment == Enchantment.power) 					cost = Config.powerCost; 				//ID = 48 	weight = 10		Max Level = 5	
	    else if	(enchantment == Enchantment.punch) 					cost = Config.punchCost; 				//ID = 49 	weight = 2		Max Level = 2	
	    else if	(enchantment == Enchantment.flame) 					cost = Config.flameCost; 				//ID = 50 	weight = 2		Max Level = 1	
	    else if	(enchantment == Enchantment.infinity) 				cost = Config.infinityCost; 			//ID = 51 	weight = 1		Max Level = 1	
	    
	    else if	(enchantment == Enchantment.luckOfTheSea) 			cost = Config.luckOfTheSeaCost; 		//ID = 61 	weight = 2 		Max Level = 3	EnumEnchantmentType.FISHING_ROD
	    else if	(enchantment == Enchantment.lure) 					cost = Config.lureCost; 				//ID = 62 	weight = 2 		Max Level = 3	EnumEnchantmentType.FISHING_ROD			
		
		return cost;
	}
	
	/**
	 * Return whether an item is reparable in an anvil. This is an expansion of the items made available for repair by getIsRepairable().
	 * @param itemstack
	 * @param materialIn
	 * @return true if materialIn is correct type of item to repair itemstack, otherwise false
	 */
	public static boolean getIsRepairableAdditions(ItemStack itemstack, ItemStack materialIn) {
		
		Item item = itemstack.getItem();
		Item materialItem = materialIn.getItem();
		
		if(item == Items.diamond_hoe && materialItem == Items.diamond) return true;
		else if(item == Items.golden_hoe && materialItem == Items.gold_ingot) return true;
		else if(item == Items.iron_hoe && materialItem == Items.iron_ingot) return true;
		else if(item == Items.stone_hoe && materialItem == Item.getItemFromBlock(Blocks.cobblestone)) return true;
		else if(item == Items.wooden_hoe && materialItem == Item.getItemFromBlock(Blocks.planks)) return true;
		
		else if(item == Items.shears && materialItem == Items.iron_ingot) return true;
		else if(item == Items.bow && materialItem == Items.string) return true;
		else if(item == Items.flint_and_steel && materialItem == Items.flint) return true;
		else if(item == Items.fishing_rod && materialItem == Items.string) return true;
		else if(item == Items.carrot_on_a_stick && materialItem == Items.carrot) return true;
		
		return false;
	}
	
	/**
	 * Gets the amount of materials needed to fully repair an item. This amount is the same as what is needed to make the item.
	 * @param itemStack
	 * @return materialCostFactor
	 */
	public static int getMaterialCostFactor(ItemStack itemStack) {
		int materialCostFactor = 4; //the vanilla value for all items (this will apply to any I may have missed; should be none)
		
		if(itemStack.getItem() instanceof ItemSpade) materialCostFactor = 1;
		else if(itemStack.getItem() instanceof ItemPickaxe) materialCostFactor = 3;
		else if(itemStack.getItem() instanceof ItemAxe) materialCostFactor = 3;
		else if(itemStack.getItem() instanceof ItemHoe) materialCostFactor = 2;
		else if(itemStack.getItem() instanceof ItemSword) materialCostFactor = 2;
		else if(itemStack.getItem() instanceof ItemArmor)
		{
			Item item = itemStack.getItem();
			
			if(item.isValidArmor(itemStack, 0, null)) materialCostFactor = 5; //Helmet
			else if(item.isValidArmor(itemStack, 1, null)) materialCostFactor = 8; //Chestplate
			else if(item.isValidArmor(itemStack, 2, null)) materialCostFactor = 7; //Leggings
			else if(item.isValidArmor(itemStack, 3, null)) materialCostFactor = 4; //Boots		
		}
    	else if(itemStack.getItem() instanceof ItemShears) materialCostFactor = 2;
    	else if(itemStack.getItem() instanceof ItemBow) materialCostFactor = 3;
    	else if(itemStack.getItem() instanceof ItemFlintAndSteel) materialCostFactor = 1;
    	else if(itemStack.getItem() instanceof ItemFishingRod) materialCostFactor = 2;
    	else if(itemStack.getItem() instanceof ItemCarrotOnAStick) materialCostFactor = 1;
		
		return materialCostFactor;
	}

	
	
	
	
}
