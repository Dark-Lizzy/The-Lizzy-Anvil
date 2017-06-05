package com.darkliz.lizzyanvil.packethandler;

import com.darkliz.lizzyanvil.container.ContainerLizzyRepair;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AnvilRenamePacket implements IMessage {
	private String text;
	
	public AnvilRenamePacket() { }

    public AnvilRenamePacket(String text) {
        this.text = text;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        text = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, text);
    }

    public static class Handler implements IMessageHandler<AnvilRenamePacket, IMessage> {
        @Override
        public IMessage onMessage(final AnvilRenamePacket message, final MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // on the server (sending from client to server)
            //IThreadListener mainThread = Minecraft.getMinecraft(); // on the client (sending from server to client)
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                	ContainerLizzyRepair containerrepair = (ContainerLizzyRepair) ctx.getServerHandler().playerEntity.openContainer;
                	
                	if (containerrepair instanceof ContainerLizzyRepair)
                    {
                		
                        if (message.text != null && message.text.length() >= 1)
                        {
                            String s = ChatAllowedCharacters.filterAllowedCharacters(message.text);

                            if (s.length() <= 40)
                            {
                                containerrepair.updateItemName(s);
                            }
                        }
                        else
                        {
                            containerrepair.updateItemName("");
                        }
                    }
                }
            });
            return null;
        }
    }
}