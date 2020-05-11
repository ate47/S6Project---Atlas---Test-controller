package ssixprojet.server.packet.server;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import ssixproject.client.PlayerData;
import ssixprojet.server.packet.PacketManager;
import ssixprojet.server.packet.PacketServer;

public class PacketS02PlayerRegister extends PacketServer {
	public static PacketS02PlayerRegister create(ByteBuf buf) {
		if (!buf.isReadable(20))
			return null;
		UUID uuid = PacketManager.readUUID(buf);
		int id = buf.readInt();

		return new PacketS02PlayerRegister(uuid, id);
	}

	private UUID playerUUID;
	private int id;

	public PacketS02PlayerRegister(UUID playerUUID, int id) {
		this.playerUUID = playerUUID;
		this.id = id;
	}

	@Override
	public void handle(PlayerData playerData) throws Exception {
		playerData.playerUUID = playerUUID;
		playerData.id = id;
		playerData.log("id: " + playerUUID + ", " + id);
	}

}
