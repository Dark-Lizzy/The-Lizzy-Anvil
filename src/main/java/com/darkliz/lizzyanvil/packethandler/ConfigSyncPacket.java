package com.darkliz.lizzyanvil.packethandler;

import com.darkliz.lizzyanvil.config.Config;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ConfigSyncPacket implements IMessage {
	private String key;
	private int value;
	
	public ConfigSyncPacket() { }

    public ConfigSyncPacket(String key, int value) {
    	this.key = key;
        this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	key = ByteBufUtils.readUTF8String(buf);
    	value = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, key);
        buf.writeInt(value);
    }

    public static class Handler implements IMessageHandler<ConfigSyncPacket, IMessage> {
        @Override
        public IMessage onMessage(final ConfigSyncPacket message, final MessageContext ctx) {
            //IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // on the server (sending from client to server)
            IThreadListener mainThread = Minecraft.getMinecraft(); // on the client (sending from server to client)
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                	//EntityPlayer thePlayer = (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : ctx.getServerHandler().playerEntity);
                	if(ctx.side.isClient())
                	{
                		//System.out.println("processing packet on client..."); //Debug Message
                		
                    	//Set the config field corresponding to key on the client to the value passed in from the server
                    	Config.syncConfigToServer(message.key, message.value);
                	}
                }
            });
            return null;
        }
    }
}