package com.darkliz.lizzyanvil.init;

import com.darkliz.lizzyanvil.crafting.RecipeRemover;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class LizzyAnvilCraftingRecipes {
//////////////////////////////////////////////////////////////////
//Crafting Recipes ///////////////////////////////////////////////
//////////////////////////////////////////////////////////////////

public static void doCraftingRecipes()
	{

	//Remove vanilla anvil recipe
	RecipeRemover.removeCraftingRecipe(Blocks.anvil);
	
	//Replace it with less expensive recipe
	GameRegistry.addRecipe(new ItemStack(Blocks.anvil, 1), 
			new Object[] {	"iii", 
							" i ", 
							"iii", 'I', Blocks.iron_block, 'i', Items.iron_ingot});
	
	//Add a recipe for the workshop anvil (lizzy_anvil) - requires an undamaged plain anvil (damaged plain anvils cannot be made into workshop anvils)
	GameRegistry.addRecipe(new ItemStack(LizzyAnvilBlocks.lizzy_anvil, 1),
			new Object[] {	"iii", 
							"IAI", 
							"iii", 'A', new ItemStack(Blocks.anvil, 1, 0), 'I', Blocks.iron_block, 'i', Items.iron_ingot});
	
	}
}
