package ssixprojet.server.packet.server;

import io.netty.buffer.ByteBuf;
import ssixproject.controller.XAtlas;
import ssixprojet.server.packet.PacketServer;

public class PacketS0AChangeAmmos extends PacketServer {
	public static PacketS0AChangeAmmos create(ByteBuf buf) {
		if (!buf.isReadable(4))
			return null;

		return new PacketS0AChangeAmmos(buf.readInt());
	}

	private int ammos;

	private PacketS0AChangeAmmos(int ammos) {
		this.ammos = ammos;
	}

	@Override
	public void handle(XAtlas src) throws Exception {
		src.playerData.ammos = ammos;
	}
}
