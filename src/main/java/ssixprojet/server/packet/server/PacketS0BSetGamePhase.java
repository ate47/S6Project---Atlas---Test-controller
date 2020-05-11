package ssixprojet.server.packet.server;

import io.netty.buffer.ByteBuf;
import ssixproject.client.GamePhase;
import ssixproject.client.PlayerData;
import ssixprojet.server.packet.PacketServer;

public class PacketS0BSetGamePhase extends PacketServer {
	public static PacketS0BSetGamePhase create(ByteBuf buf) {
		if (!buf.isReadable(4))
			return null;

		return new PacketS0BSetGamePhase(GamePhase.values()[buf.readInt()]);
	}

	private GamePhase phase;

	private PacketS0BSetGamePhase(GamePhase phase) {
		this.phase = phase;
	}

	@Override
	public void handle(PlayerData playerData) throws Exception {
		playerData.phase = phase;
	}

}
