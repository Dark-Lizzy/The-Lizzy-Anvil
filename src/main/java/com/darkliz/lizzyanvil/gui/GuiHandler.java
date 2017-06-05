package com.darkliz.lizzyanvil.gui;

import com.darkliz.lizzyanvil.LizzyAnvil;
import com.darkliz.lizzyanvil.container.ContainerLizzyRepair;
import com.darkliz.lizzyanvil.init.LizzyAnvilBlocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler{

	
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		//System.out.println("running container");
		if(ID == LizzyAnvil.guiIDLizzyAnvil) {
			return ID == LizzyAnvil.guiIDLizzyAnvil 
					&& world.getBlockState(pos).getBlock() == LizzyAnvilBlocks.lizzy_anvil ? new ContainerLizzyRepair(player.inventory, world, pos, player): null;
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		//System.out.println("running gui");
		if(ID == LizzyAnvil.guiIDLizzyAnvil) {
			return ID == LizzyAnvil.guiIDLizzyAnvil 
					&& world.getBlockState(pos).getBlock() == LizzyAnvilBlocks.lizzy_anvil ? new GuiLizzyRepair(player.inventory, world): null;
		}
		
		return null;
	}
}
