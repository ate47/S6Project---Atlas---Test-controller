package ssixprojet.server.packet.server;

import io.netty.buffer.ByteBuf;
import ssixproject.client.Data;
import ssixprojet.server.packet.PacketServer;

public class PacketS0ETimeToWaitPing<D extends Data> extends PacketServer<D> {
	public static <D extends Data> PacketS0ETimeToWaitPing<D> create(ByteBuf buf) {
		if (!buf.isReadable(4))
			return null;
		return new PacketS0ETimeToWaitPing<>(buf.readInt());
	}

	private int time;

	private PacketS0ETimeToWaitPing(int time) {
		this.time = time;
	}

	@Override
	public void handle(D playerData) throws Exception {
		playerData.timeToWait = time;
	}
}
