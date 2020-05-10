package ssixprojet.server.packet.server;

import io.netty.buffer.ByteBuf;
import ssixproject.controller.XAtlas;
import ssixprojet.server.packet.PacketServer;

public class PacketS0ETimeToWaitPing extends PacketServer {
	public static PacketS0ETimeToWaitPing create(ByteBuf buf) {
		if (!buf.isReadable(4))
			return null;
		return new PacketS0ETimeToWaitPing(buf.readInt());
	}

	private int time;

	private PacketS0ETimeToWaitPing(int time) {
		this.time = time;
	}

	@Override
	public void handle(XAtlas src) throws Exception {
		src.playerData.timeToWait = time;
	}
}
