package com.darkliz.lizzyanvil.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemMultiTexture;

public class ItemLizzyAnvilBlock extends ItemMultiTexture
{

    public ItemLizzyAnvilBlock(Block block)
    {
        super(block, block, new String[] {"intact", "slightlyDamaged", "veryDamaged"});
    }

    public int getMetadata(int damage)
    {
        return damage << 2;
    }

}