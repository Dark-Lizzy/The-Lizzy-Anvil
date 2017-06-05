package com.darkliz.lizzyanvil.init;

import com.darkliz.lizzyanvil.Reference;
import com.darkliz.lizzyanvil.blocks.BlockLizzyAnvil;
import com.darkliz.lizzyanvil.blocks.ItemLizzyAnvilBlock;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class LizzyAnvilBlocks {
	
	//Block Declaration
		public static Block lizzy_anvil;
		


	//Block Registers
		public static void init()
		{
			lizzy_anvil = new BlockLizzyAnvil().setUnlocalizedName("lizzy_anvil");
			GameRegistry.registerBlock(lizzy_anvil, ItemLizzyAnvilBlock.class, lizzy_anvil.getUnlocalizedName().substring(5));
			
			
		}
		
	//Render Registers
		public static void registerRenders()
		{
			
			Item item = Item.getItemFromBlock(lizzy_anvil);

			String variantName = "lizzy_anvil_intact";
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":" + variantName, "inventory"));
			ModelBakery.addVariantName(item, Reference.MOD_ID + ":" + variantName);
			
			variantName = "lizzy_anvil_slightly_damaged";
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 1, new ModelResourceLocation(Reference.MOD_ID + ":" + variantName, "inventory"));
			ModelBakery.addVariantName(item, Reference.MOD_ID + ":" + variantName);
			
			variantName = "lizzy_anvil_very_damaged";
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 2, new ModelResourceLocation(Reference.MOD_ID + ":" + variantName, "inventory"));
			ModelBakery.addVariantName(item, Reference.MOD_ID + ":" + variantName);
		}
		
		
		

	//Registry Functions
		public static void register(Block block)
		{
			GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));
		}
		
		public static void registerRender(Block block)
		{
			Item item = Item.getItemFromBlock(block);
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().
			register(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
		}
		
	}