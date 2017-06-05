package com.darkliz.lizzyanvil.crafting;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

/*
 * Recipe Remover by Neale Gaming - check out his minecraft modding tutorial series on YouTube!
 */


public class RecipeRemover {
	
/**Removes the recipe for an item from the game  */
	public static void removeCraftingRecipe(Item item)
	{
		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		
		Iterator<IRecipe> remover = recipes.iterator();
		
		while(remover.hasNext())
		{	
			ItemStack itemstack = remover.next().getRecipeOutput();

			if(itemstack != null && itemstack.getItem() == item)
			{
				remover.remove();
			}		
		}
	}
	
/**Removes the recipe for a block from the game */
	public static void removeCraftingRecipe(Block block)
	{
		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		
		Iterator<IRecipe> remover = recipes.iterator();
		
		while(remover.hasNext())
		{	
			ItemStack itemstack = remover.next().getRecipeOutput();

			if(itemstack != null && itemstack.getItem() == Item.getItemFromBlock(block))
			{
				remover.remove();
			}		
		}
	}
	
	
}
