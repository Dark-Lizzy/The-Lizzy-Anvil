package com.darkliz.lizzyanvil.packethandler;

import com.darkliz.lizzyanvil.config.Config;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RequestConfigSyncPacket implements IMessage {
	
	
	public RequestConfigSyncPacket() { }
	
    

    @Override
    public void fromBytes(ByteBuf buf) {
    	
    }

    @Override
    public void toBytes(ByteBuf buf) {
        
    }

    public static class Handler implements IMessageHandler<RequestConfigSyncPacket, IMessage> {
        @Override
        public IMessage onMessage(final RequestConfigSyncPacket message, final MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // on the server (sending from client to server)
            //IThreadListener mainThread = Minecraft.getMinecraft(); // on the client (sending from server to client)
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                	
                	if(ctx.side.isServer())
                	{
                		//System.out.println("config sync request recieved... "); //Debug Message
	                	
	                	if(MinecraftServer.getServer().isDedicatedServer())
	                	{
	                		//System.out.println("processing packet on server..."); //Debug Message
	                		
	                		//Get the server config values and send them to the client
		                	Config.sendConfigToClient(ctx.getServerHandler().playerEntity);
	                	}
                	}
                }
            });
            return null;
        }
    }
}