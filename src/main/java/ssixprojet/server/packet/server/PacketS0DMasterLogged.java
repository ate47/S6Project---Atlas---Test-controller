package ssixprojet.server.packet.server;

import io.netty.buffer.ByteBuf;
import ssixproject.client.MasterData;
import ssixprojet.server.packet.PacketServer;

public class PacketS0DMasterLogged extends PacketServer<MasterData> {

	public static PacketS0DMasterLogged create(ByteBuf buf) {
		return new PacketS0DMasterLogged();
	}

	private PacketS0DMasterLogged() {}

	@Override
	public void handle(MasterData data) throws Exception {
		data.connected = true;
	}
}
