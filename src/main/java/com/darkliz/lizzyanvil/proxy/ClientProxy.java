package com.darkliz.lizzyanvil.proxy;

import com.darkliz.lizzyanvil.init.LizzyAnvilBlocks;

public class ClientProxy extends CommonProxy{

	@Override
	public void registerRenders()
	{
		LizzyAnvilBlocks.registerRenders();
		
	}
	
	//For Testing
	@Override
    public boolean isClient() {
		//System.out.println("Running isClient - returning true");
        return true;
    }

    @Override
    public boolean isServer() {
    	//System.out.println("Running isServer - returning false");
        return false;
    }
    
	
}