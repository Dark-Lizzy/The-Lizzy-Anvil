package com.darkliz.lizzyanvil.eventhandler;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerInteractEventHandler {

	
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event){
		
		EntityPlayer player = event.entityPlayer;
		Action action = event.action;
		World world = event.world;
		BlockPos pos = event.pos;
		//EnumFacing face = event.face; // Can be null if unknown
		
		if(action == action.RIGHT_CLICK_BLOCK)
		{
			Block blockClicked = world.getBlockState(pos).getBlock();

			if(blockClicked != null && blockClicked == Blocks.anvil)
			{
				if(!player.isSneaking())
				{
					event.setCanceled(true);
				}
				else if(player.getHeldItem() == null)
				{
					event.setCanceled(true);
				}
			}
		}
	}
}
