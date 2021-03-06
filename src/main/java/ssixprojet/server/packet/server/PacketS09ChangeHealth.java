package ssixprojet.server.packet.server;

import io.netty.buffer.ByteBuf;
import ssixproject.client.PlayerData;
import ssixprojet.server.packet.PacketServer;

public class PacketS09ChangeHealth extends PacketServer<PlayerData> {
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
	public void handle(PlayerData playerData) throws Exception {
		playerData.health = health;
	}
}
