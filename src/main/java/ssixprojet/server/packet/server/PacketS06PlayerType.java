package ssixprojet.server.packet.server;

import io.netty.buffer.ByteBuf;
import ssixproject.client.PlayerType;
import ssixproject.controller.XAtlas;
import ssixprojet.server.packet.PacketServer;

public class PacketS06PlayerType extends PacketServer {
	public static PacketS06PlayerType create(ByteBuf buf) {
		if (!buf.isReadable(4))
			return null;
		int type = buf.readInt();
		return new PacketS06PlayerType(type);
	}

	private int type;

	private PacketS06PlayerType(int type) {
		this.type = type;
	}

	@Override
	public void handle(XAtlas src) throws Exception {
		src.playerData.type = PlayerType.values()[type];
	}

}
