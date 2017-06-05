package com.darkliz.lizzyanvil.container;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.darkliz.lizzyanvil.LizzyAnvil;
import com.darkliz.lizzyanvil.blocks.BlockLizzyAnvil;
import com.darkliz.lizzyanvil.config.Config;
import com.darkliz.lizzyanvil.init.LizzyAnvilBlocks;
import com.darkliz.lizzyanvil.packethandler.HasHeatPacket;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerLizzyRepair extends Container
{
    //private static final Logger logger = LogManager.getLogger();
    /** Here comes out item you merged and/or renamed. */
    private IInventory outputSlot;
    /** The 2 slots where you put your items in that you want to merge and/or rename. */
    private IInventory inputSlots;
    private World theWorld;
    private BlockPos selfPosition;
    /** The maximum cost of repairing/renaming in the anvil. */
    public int maximumCost;
    /** determined by damage of input item and stackSize of repair materials */
    public int materialCost;
    public String repairedItemName;
    /** The player that has this container open. */
    private final EntityPlayer thePlayer;

    public boolean hasHeatSource;
    private ItemStack prevItemStackLeft;
    private boolean initialHeatCheckDone;
    public boolean heatRequired;
    public boolean renamingOnly;
	

    @SideOnly(Side.CLIENT)
    public ContainerLizzyRepair(InventoryPlayer playerInventory, World worldIn, EntityPlayer player)
    {
        this(playerInventory, worldIn, BlockPos.ORIGIN, player);
    }

    public ContainerLizzyRepair(InventoryPlayer playerInventory, final World worldIn, final BlockPos blockPosIn, EntityPlayer player)
    {	
        this.outputSlot = new InventoryCraftResult();
        this.inputSlots = new InventoryBasic("Repair", true, 2)
        {
            /**
             * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it hasn't changed and skip it.
             */
            public void markDirty()
            {
                super.markDirty();
                ContainerLizzyRepair.this.onCraftMatrixChanged(this);
            }
        };

        this.heatRequired = Config.heatRequired;
        this.selfPosition = blockPosIn;
        this.theWorld = worldIn;
        this.thePlayer = player;
        this.addSlotToContainer(new Slot(this.inputSlots, 0, 27, 47));
        this.addSlotToContainer(new Slot(this.inputSlots, 1, 76, 47));
        this.addSlotToContainer(new Slot(this.outputSlot, 2, 134, 47)
        {
            /**
             * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
             */
            public boolean isItemValid(ItemStack stack)
            {
                return false;
            }
            /**
             * Return whether this slot's stack can be taken from this slot.
             */
            public boolean canTakeStack(EntityPlayer playerIn)
            {
                return (playerIn.capabilities.isCreativeMode || playerIn.experienceLevel >= ContainerLizzyRepair.this.maximumCost) && this.getHasStack() || ContainerLizzyRepair.this.renamingOnly; //removed -- && ContainerLizzyRepair.this.maximumCost > 0 -- to allow the cost to be 0
            }
            public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
            {
                if (!playerIn.capabilities.isCreativeMode)
                {
                    playerIn.addExperienceLevel(-ContainerLizzyRepair.this.maximumCost);
                    //causes a bug with Simple XP Storage when mana crystals are used after using the anvil.
                    //this seems to be happening because the above function doesn't actually set the xp total
                    //to the proper amount. It only sets the level.
                    //Solution: recalculate xp total in mana crystal code.
                }

                ContainerLizzyRepair.this.inputSlots.setInventorySlotContents(0, (ItemStack)null);

                if (ContainerLizzyRepair.this.materialCost > 0)
                {
                    ItemStack itemstack1 = ContainerLizzyRepair.this.inputSlots.getStackInSlot(1);
                    
                    if (itemstack1 != null && itemstack1.stackSize > ContainerLizzyRepair.this.materialCost)
                    {
                        itemstack1.stackSize -= ContainerLizzyRepair.this.materialCost;
                        ContainerLizzyRepair.this.inputSlots.setInventorySlotContents(1, itemstack1);
                    }
                    else
                    {
                    	ContainerLizzyRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
                    }
                }
                else
                {
                	ContainerLizzyRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
                }

                ContainerLizzyRepair.this.maximumCost = 0;
                
                IBlockState iblockstate = worldIn.getBlockState(blockPosIn);
                float breakChance = (float) Config.breakChancePercent / 100;
                
                if (!playerIn.capabilities.isCreativeMode && !worldIn.isRemote && iblockstate.getBlock() == LizzyAnvilBlocks.lizzy_anvil && playerIn.getRNG().nextFloat() < breakChance)
                {
                    int k = ((Integer)iblockstate.getValue(BlockLizzyAnvil.DAMAGE)).intValue();
                    ++k;

                    if (k > 2)
                    {
                        worldIn.setBlockToAir(blockPosIn);
                        worldIn.playAuxSFX(1020, blockPosIn, 0);
                    }
                    else
                    {
                        worldIn.setBlockState(blockPosIn, iblockstate.withProperty(BlockLizzyAnvil.DAMAGE, Integer.valueOf(k)), 2);
                        worldIn.playAuxSFX(1021, blockPosIn, 0);
                    }
                }
                else if (!worldIn.isRemote)
                {
                    worldIn.playAuxSFX(1021, blockPosIn, 0);
                }
            }
        });
        
        //Add regular inventory slots
        int i;
        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        super.onCraftMatrixChanged(inventoryIn);

        if (inventoryIn == this.inputSlots)
        {
    		//If heat source is required
    		if(this.heatRequired && !this.theWorld.isRemote)
    		{
    			//check for heat source whenever something is inserted into the left slot
    			ItemStack itemStackLeft = this.inputSlots.getStackInSlot(0);
    			ItemStack itemStackRight = this.inputSlots.getStackInSlot(1);
    			if(itemStackLeft != null)
    			{
    				if(itemStackLeft.isItemStackDamageable() && !ItemStack.areItemStacksEqual(itemStackLeft, prevItemStackLeft))
    				{
		        		this.checkForHeatSource();
		        		this.prevItemStackLeft = itemStackLeft;
		        		this.initialHeatCheckDone = true;
    				}
    			}
    			//check for heat source when something is put into the right slot if a heat check has not already been done (this clears up a minor display sync glitch that happens when the right slot is filled before the left one: flashes "No Heat Source!")
    			else if(itemStackRight != null && !this.initialHeatCheckDone)
    			{
					this.checkForHeatSource();
	    			this.initialHeatCheckDone = true;
    			}
    		}
    		
    		this.updateRepairOutput();
        }
    }

    /**
     * called when the Anvil Input Slot changes, calculates the new result and puts it in the output slot
     */
    public void updateRepairOutput()
    {
    	
        ItemStack itemstack = this.inputSlots.getStackInSlot(0);
        this.maximumCost = 0;
        int baseCost = 0;
        int baseValue = 0;
        
        if (itemstack == null)
        {
            this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
        }
        else
        {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = this.inputSlots.getStackInSlot(1);
            Map map1 = EnchantmentHelper.getEnchantments(itemstack1);
            boolean isBookInRight = false, isBookInLeft = false, nothingAdded = false;
            this.materialCost = 0;
            int j, k, l;
            
            if(itemstack2 == null) //make the output null unless changes to the name have been made
            {
            	if(repairedItemName.equals(itemstack1.getDisplayName()) || (StringUtils.isBlank(repairedItemName) && !itemstack1.hasDisplayName()))
            	{
            		this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
            		return;
            	}
            	this.renamingOnly = true;
            }
            else //itemstack2 != null
            {
            	this.renamingOnly = false;
            	
            	//Check if there is a heat source, if heat is required
            	if(!this.hasHeatSource && this.heatRequired && itemstack1.isItemStackDamageable()) //if heat is required and the item in the left slot is a tool (books can be worked with on the anvil without heat)
            	{
            		//System.out.println("-------------------EXITING (0)-----------------------"); //Debug Message
            		this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
            		this.maximumCost = 1; //this tells the gui to check if it should display the "No Heat Source!" message if no heat source is present
                    return;
            	}
            	
            	Map map2 = EnchantmentHelper.getEnchantments(itemstack2);
            	
                isBookInRight = itemstack2.getItem() == Items.enchanted_book && Items.enchanted_book.getEnchantments(itemstack2).tagCount() > 0;
                isBookInLeft = itemstack1.getItem() == Items.enchanted_book && Items.enchanted_book.getEnchantments(itemstack1).tagCount() > 0;
                
                boolean isUnenchanting = false;
                
                int repairBonusPercent = Config.repairBonusPercent;
                Item unenchantItem = Item.getItemById(Config.unenchantItemID) != null ? Item.getItemById(Config.unenchantItemID) : Items.redstone; //Items.redstone ID is 331
                
	            //REPAIRING DAMAGED ITEMS, UNENCHANTING and TRANSFERRING -----------------------------------------------------------
	            //If repairing an item with materials
	            if (itemstack1.isItemStackDamageable() 
	            		&& (itemstack1.getItem().getIsRepairable(itemstack, itemstack2) || AnvilMethods.getIsRepairableAdditions(itemstack, itemstack2)))
	            {
	            	int repairBonus;
	            	if(repairBonusPercent <= 0)
	            	{
	            		repairBonus = 0;
	            	}
	            	else
	            	{
	            		int percentPerMatierial = Math.max(repairBonusPercent / AnvilMethods.getMaterialCostFactor(itemstack1), 1); // a % per material used, based on the item's material factor cost; minimum 1
	            		repairBonus = Math.max(itemstack1.getMaxDamage() * percentPerMatierial / 100, 1); // a % of the max damage based on the repairBonusPercent; minimum 1
	            	}
	            	j = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / AnvilMethods.getMaterialCostFactor(itemstack1) + repairBonus); //j = the lower of these two values
	                if (j <= 0) //if there is nothing to repair, output = null and return
	                {
	                	this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
	                    return;
	                }
	                for (k = 0; j > 0 && k < itemstack2.stackSize; ++k) //if there is damage to repair 
	                {
	                    l = itemstack1.getItemDamage() - j;
	                    itemstack1.setItemDamage(l);
	                    j = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / AnvilMethods.getMaterialCostFactor(itemstack1) + repairBonus);
	                }
	                this.materialCost = k;
	                
	                if(!itemstack1.isItemEnchanted())
	                {
	                	baseCost = 0;
	                } 
	            }
	            else
	            {
	            	//if not an enchanted book in right slot and (not the same type of item in both slots or not a damageable item in left slot) and not an UNENCHANT_ITEM or a normal book in the right slot)
	            	if (!isBookInRight && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isItemStackDamageable())
	            			&& itemstack2.getItem() != unenchantItem && itemstack2.getItem() != Items.book)
	                {
	                    //System.out.println("-------------------EXITING (1)-----------------------"); //Debug Message
	                    this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
	                    return;
	                }
	            	if(itemstack2.getItem() == Items.book && (isBookInLeft || !itemstack1.isItemEnchanted())) //Only enchanted items are allowed with normal books (cannot transfer from enchanted book, only from an item)
	            	{
	                    //System.out.println("-------------------EXITING (2)-----------------------"); //Debug Message
	                    this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
	                    return;
	            	}
	            	if(itemstack2.getItem() == unenchantItem && (!itemstack1.isItemEnchanted() && !isBookInLeft)) //Only enchanted items and enchanted books are allowed with the unenchant item
	            	{
	                    //System.out.println("-------------------EXITING (3)-----------------------"); //Debug Message
	                    this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
	                    return;
	            	}
	            	if(!isBookInLeft && !itemstack1.isItemStackDamageable()) //A final check - only enchanted books and damageable items are allowed in the left slot
	            	{
	                    //System.out.println("-------------------EXITING (4)-----------------------"); //Debug Message
	                    this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
	                    return;
	            	}
	            	
	            	//if its not an enchanted book in right slot and it is a damageable item or enchanted book in left slot
	                if (!isBookInRight) // && (itemstack1.isItemStackDamageable() || isBookInLeft))
	                {
	                	//UNENCHANTING ------------------------------------------------------------------------------------------------------------------
	                    if(itemstack2.getItem() == unenchantItem ) //&& (itemstack1.isItemEnchanted() || isBookInLeft))
	                	{
	                		isUnenchanting = true;
	                		
	                		
	                		if (!itemstack2.hasDisplayName()) //if the unenchant item doesn't have a custom name (its an unnamed one)
	                        {
	                        	map1.clear(); //remove all enchantments
	                        	if(itemstack1.getItem() == Items.enchanted_book) itemstack1 = new ItemStack(Items.book); //set output to a regular book
	                        }
	                		else //the item has a custom name, check to see if it matches the name of one of the enchantments; if so, remove that enchantment, leaving the rest
	                		{
	                			String slot2DisplayName = itemstack2.getDisplayName();
	                    		int enchToRemoveID = AnvilMethods.getMatchingEnchantmentIDByName(map1, slot2DisplayName);
	
	                            if(map1.containsKey(Integer.valueOf(enchToRemoveID))) //check to make sure the ID is in map1
	                            {
	                            	map1.remove(Integer.valueOf(enchToRemoveID)); //remove the matching enchantment from the map
	                            	
	                            	if(itemstack1.getItem() == Items.enchanted_book && Items.enchanted_book.getEnchantments(itemstack1).tagCount() <= 1) //if only one enchantment exists
	                            	{
	                            		itemstack1 = new ItemStack(Items.book); //set output to a regular book
	                            	}
	                            	else if(itemstack1.getItem() == Items.enchanted_book && Items.enchanted_book.getEnchantments(itemstack1).tagCount() > 1)
	                            	{
	                            		itemstack1 = new ItemStack(Items.enchanted_book);
	                            	}
	                            }
	                            else //the custom name does not match any of the enchantments so do nothing
	                            {
	                            	this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
	        	                    return;
	                            }
	                		}
	                		this.materialCost = 1;	
	                    }
	                    
	                    //TRANSFERRING ------------------------------------------------------------------------------------------------------------------
	                    else if (itemstack2.getItem() == Items.book && itemstack1.isItemStackDamageable() && itemstack1.isItemEnchanted())
	                    {
	                		itemstack1 = new ItemStack(Items.enchanted_book); //set output to an enchanted book
	                		
	                		//if the normal book has a custom name, check to see if it matches the name of one of the enchantments; if so, transfer only that enchantment
	                		if (itemstack2.hasDisplayName())
	                        {
	                			String slot2DisplayName = itemstack2.getDisplayName();
	                			int enchantmentToTransferID = AnvilMethods.getMatchingEnchantmentIDByName(map1, slot2DisplayName);
	                			//if the custom name matches an enchantment on the item
	                			if(map1.containsKey(Integer.valueOf(enchantmentToTransferID)))
	                            {
	                				int enchantmentLevel = ((Integer)map1.get(Integer.valueOf(enchantmentToTransferID))).intValue();
	                				map1.clear(); //remove all enchantments	
	                				map1.put(Integer.valueOf(enchantmentToTransferID), Integer.valueOf(enchantmentLevel)); //then put the enchantment being transferred back on the book
	                            }
	                			else //the custom name doesn't match any of the enchantments so do nothing
	                			{
	                				this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
	        	                    return;
	                			}
	                        }
	                		//otherwise the normal book has no display name so transfer all enchantments (cost handled properly in the enchantments section below)
	                		
	                		this.materialCost = 1;	
	                    }
	                	
	                	//REPAIRING BY COMBINING ------------------------------------------------------------------------------------------------------------------
	                	else if (itemstack1.isItemStackDamageable() && !isBookInRight && itemstack2.getItem() != unenchantItem && itemstack2.getItem() != Items.book)
	                	{
	                		int itemDamage;
	    	                j = itemstack.getMaxDamage() - itemstack.getItemDamage(); //the amount of uses the item in left slot has left
	    	                k = itemstack2.getMaxDamage() - itemstack2.getItemDamage(); //the amount of uses the item in right slot has left
	    	                l = k + itemstack1.getMaxDamage() * repairBonusPercent / 100; //k + a % of the max damage for the item
	    	                int i1 = j + l;
	    	                itemDamage = itemstack1.getMaxDamage() - i1;
	    	
	    	                if (itemDamage < 0)
	    	                {
	    	                	itemDamage = 0;
	    	                }
	    	
	    	                if (itemDamage < itemstack1.getMetadata())
	    	                {
	    	                    itemstack1.setItemDamage(itemDamage);
	    	                    //If neither item is enchanted, cost is 0 (if one or both are enchanted, the cost is handled by the enchantment stuff)
	    	                    if (!itemstack1.isItemEnchanted() && !itemstack2.isItemEnchanted()) 
	    	                    {
	    	                    	baseCost = 0;
	    	                    }
	    	                }
	                    } 
	                }
	                
	                //ENCHANTMENT SORTING ------------------------------------------------------------------------------------------------------------------
	                Iterator iterator1 = map2.keySet().iterator();
	
	                //Go through all the enchantments on the item in the right slot
	                while (iterator1.hasNext())
	                {
	                    int enchantment2ID = ((Integer)iterator1.next()).intValue();
	                    Enchantment enchantment2 = Enchantment.getEnchantmentById(enchantment2ID);
	                    
	                    if (enchantment2 != null)
	                    {
	                    	//get the levels of the enchantments and compare them
	                        int enchantment1Level = map1.containsKey(Integer.valueOf(enchantment2ID)) ? ((Integer)map1.get(Integer.valueOf(enchantment2ID))).intValue() : 0;
	                        int enchantment2Level = ((Integer)map2.get(Integer.valueOf(enchantment2ID))).intValue();
	                        int enchantmentLevelResult;
	                        
	                        if (enchantment1Level == enchantment2Level) //if they are the same, increment the level
	                        {
	                            ++enchantment2Level;
	                            enchantmentLevelResult = enchantment2Level;
	                        }
	                        else //they are not the same, take the highest one
	                        {
	                        	enchantmentLevelResult = Math.max(enchantment2Level, enchantment1Level);
	                        }
	
	                        enchantment2Level = enchantmentLevelResult;
	                        
	                        boolean flag8 = enchantment2.canApply(itemstack);
	                        
	                        //if the item in left slot is an enchanted book
	                        if (itemstack.getItem() == Items.enchanted_book) //was: if (thePlayer.capabilities.isCreativeMode || itemstack.getItem() == Items.enchanted_book) This, oddly, made it so any enchantment on a book could be placed on any item while in creative mode.
	                        {
	                            flag8 = true;
	                        }
	
	                        //go through the enchantments on the item to be put in the output slot (the item in the left slot)
	                        Iterator iterator = map1.keySet().iterator();
	                        while (iterator.hasNext())
	                        {
	                            int enchantment1ID = ((Integer)iterator.next()).intValue();
	                            Enchantment enchantment1 = Enchantment.getEnchantmentById(enchantment1ID);
	                            
	                            //if not the same enchantment ID and the two cannot apply together
	                            if (enchantment1ID != enchantment2ID && !(enchantment2.canApplyTogether(enchantment1) && enchantment1.canApplyTogether(enchantment2))) //Forge BugFix: Let Both enchantments veto being together
	                            {
	                                flag8 = false;
	                            } 
	                        }
	                        
	                        if (flag8)
	                        {
	                        	if (enchantment2Level > enchantment2.getMaxLevel())
	                            {
	                            	enchantment2Level = enchantment2.getMaxLevel();
	                            }
	                            map1.put(Integer.valueOf(enchantment2ID), Integer.valueOf(enchantment2Level)); //put the new enchantment and its level into map1
	                        }
	                    }
	                }
	                //end of enchantment checking loop
	            }
	            //end of stuff other than repairing with materials
	                
                //COST CALCULATIONS ------------------------------------------------------------------------------------------------------------------
                baseValue = AnvilMethods.getTotalEnchantmentCost(map1);
                if(isUnenchanting)
                {
                	baseCost = 1;
                }
                else if(itemstack1.isItemStackDamageable() && isBookInRight) //use the value of the enchantment(s) on the book that end up on the item
                {
                	Map map0 = EnchantmentHelper.getEnchantments(itemstack1);
                	baseCost = AnvilMethods.getEnchantWithBookCost(map0, map1, map2);
                	if(baseCost == 0)
                	{
                		nothingAdded = true;
                	}
                }
                else if(isBookInLeft && isBookInRight) //use the lower of the two book values
                {
                	Map map0 = EnchantmentHelper.getEnchantments(itemstack1); //this is the enchantment map on the book in the left as the enchantments haven't been put on to itemstack1 yet.
                	int input1Cost = AnvilMethods.getTotalEnchantmentCost(map0);
                	int input2Cost = AnvilMethods.getTotalEnchantmentCost(map2);
                	baseCost = Math.min(input1Cost, input2Cost);
                }
                else
                {
                	baseCost += baseValue; //use the value of the enchantments on the output item
                }
                
            }
            //End of stuff dealing with 2 slots filled
            
            
    	    //Item Renaming - for renaming items while enchanting/repairing
    	    if (StringUtils.isBlank(repairedItemName))
    	    {
    	        if (itemstack.hasDisplayName())
    	        {
    	        	itemstack1.clearCustomName();
    	        }
    	    }
    	    else if (!repairedItemName.equals(itemstack.getDisplayName()))
    	    {
    	    	itemstack1.setStackDisplayName(repairedItemName);
    	    }
    	    
    	    if(itemstack2 != null)
    	    {
	    	    //Final Stuff
    	    	
    	    	//Check book compatibility (this check may be redundant at this point)
	            if (isBookInRight && !AnvilMethods.isBookCompatibleWithItem(itemstack1, itemstack2) && !isBookInLeft) //Original method didn't seem to work, made my own
	    	    {
	    	    	itemstack1 = null;
	    	    	baseCost = 0;
	    	    	//System.out.println("Incompatible item/book combination"); //Debug Message
	    	    }
	            
	            if(nothingAdded)
	            {
	            	itemstack1 = null;
	    	    	baseCost = 0;
	            	//System.out.println("Output null because nothing was added to the item"); //Debug Message
	            }
	            
	    	    this.maximumCost = baseCost;

	    	    int costLimit = Config.costLimit;
	    	    if (costLimit > 0 && (this.maximumCost >= costLimit || baseValue >= costLimit) && !this.thePlayer.capabilities.isCreativeMode)
	    	    {
	    	    	//System.out.println("Output null due to cost limit"); //Debug Message
	    	        itemstack1 = null;
	    	    }
	    	
	    	    if (itemstack1 != null)
	    	    {
	    	        EnchantmentHelper.setEnchantments(map1, itemstack1);
	    	    }
    	    }
    	    
            this.outputSlot.setInventorySlotContents(0, itemstack1);
            this.detectAndSendChanges();
        }
    }

    /**
     * Add the given Listener to the list of Listeners. Method name is for legacy.
     */
    public void addCraftingToCrafters(ICrafting listener)
    {
        super.addCraftingToCrafters(listener);
        listener.sendProgressBarUpdate(this, 0, this.maximumCost);
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        if (id == 0)
        {
            this.maximumCost = data;
        }
    }

    /**
     * Called when the container is closed.
     */
    
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (!this.theWorld.isRemote)
        {
            for (int i = 0; i < this.inputSlots.getSizeInventory(); ++i)
            {
                ItemStack itemstack = this.inputSlots.getStackInSlotOnClosing(i);

                if (itemstack != null)
                {
                    playerIn.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
        }
    }

    
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.theWorld.getBlockState(this.selfPosition).getBlock() != LizzyAnvilBlocks.lizzy_anvil ? false : playerIn.getDistanceSq((double)this.selfPosition.getX() + 0.5D, (double)this.selfPosition.getY() + 0.5D, (double)this.selfPosition.getZ() + 0.5D) <= 64.0D;
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 2)
            {
                if (!this.mergeItemStack(itemstack1, 3, 39, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index != 0 && index != 1)
            {
                if (index >= 3 && index < 39 && !this.mergeItemStack(itemstack1, 0, 2, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 3, 39, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }

    /**
     * used by the Anvil GUI to update the Item Name being typed by the player
     */
    
    public void updateItemName(String newName)
    {
        this.repairedItemName = newName;

        if (this.getSlot(2).getHasStack())
        {
            ItemStack itemstack = this.getSlot(2).getStack();

            if (StringUtils.isBlank(newName))
            {
                itemstack.clearCustomName();
            }
            else
            {
                itemstack.setStackDisplayName(this.repairedItemName);
            }
        }

        this.updateRepairOutput();
    }


	private void checkForHeatSource()
	{
		if(!this.theWorld.isRemote)
    	{
    		//Check for heat source; if none, repairs, etc. cannot be done
        	if(!AnvilMethods.isHeatPresent(theWorld, selfPosition))
        	{
        		this.hasHeatSource = false;
        		//System.out.println("No heat source");//Debug Message
        	}
        	else
        	{
        		this.hasHeatSource = true;
        		//System.out.println("Heat is present");//Debug Message
        	}
        	if (thePlayer instanceof EntityPlayerMP)
    		{
    			LizzyAnvil.network.sendTo(new HasHeatPacket(this.hasHeatSource), (EntityPlayerMP) thePlayer);
    		}
    	}
	}

	public void setHasHeat(boolean hasHeat) {
		
		if(this.hasHeatSource != hasHeat)
		{
			this.hasHeatSource = hasHeat;
		
			this.updateRepairOutput();
		}
		
	}

	
	
}