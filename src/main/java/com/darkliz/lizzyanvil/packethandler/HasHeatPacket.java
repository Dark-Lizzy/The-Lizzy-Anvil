package com.darkliz.lizzyanvil.packethandler;

import com.darkliz.lizzyanvil.container.ContainerLizzyRepair;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HasHeatPacket implements IMessage {
	private boolean hasHeat;
	
	public HasHeatPacket() { }

    public HasHeatPacket(boolean hasHeat) {
        this.hasHeat = hasHeat;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.hasHeat = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(hasHeat);
    }

    public static class Handler implements IMessageHandler<HasHeatPacket, IMessage> {
        @Override
        public IMessage onMessage(final HasHeatPacket message, final MessageContext ctx) {
            //IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // on the server (sending from client to server)
            IThreadListener mainThread = Minecraft.getMinecraft(); // on the client (sending from server to client)
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                	
                	if(ctx.side.isClient())
                	{
                		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
                		ContainerLizzyRepair containerrepair = (ContainerLizzyRepair) player.openContainer;
	                	if (containerrepair instanceof ContainerLizzyRepair)
	                    {
	                		containerrepair.setHasHeat(message.hasHeat);
	                    }
                	}
                }
            });
            return null;
        }
    }
}