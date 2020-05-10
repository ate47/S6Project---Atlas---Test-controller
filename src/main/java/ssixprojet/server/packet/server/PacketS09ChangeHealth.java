package ssixprojet.server.packet.server;

import io.netty.buffer.ByteBuf;
import ssixproject.controller.XAtlas;
import ssixprojet.server.packet.PacketServer;

public class PacketS09ChangeHealth extends PacketServer {
	public static PacketS09ChangeHealth create(ByteBuf buf) {
		if (!buf.isReadable(4))
			return null;

		return new PacketS09ChangeHealth(buf.readInt());
	}

	private int health;

	private PacketS09ChangeHealth(int health) {
		this.health = health;
	}

	@Override
	public void handle(XAtlas src) throws Exception {
		src.playerData.health = health;
	}
}
