package com.darkliz.lizzyanvil.proxy;

public class CommonProxy {
	
	
	public void registerRenders()
	{
		
	}
	
	//For Testing
	public boolean isClient() {
		//System.out.println("Running isClient - returning false");
        return false;
    }
	
    public boolean isServer() {
    	//System.out.println("Running isServer - returning true");
        return true;
    }
    
    
}
