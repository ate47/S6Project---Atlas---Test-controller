package ssixprojet.server.packet.server;

import io.netty.buffer.ByteBuf;
import ssixproject.client.Data;
import ssixproject.client.GamePhase;
import ssixprojet.server.packet.PacketServer;

public class PacketS0BSetGamePhase<D extends Data> extends PacketServer<D> {
	public static <D extends Data> PacketS0BSetGamePhase<D> create(ByteBuf buf) {
		if (!buf.isReadable(4))
			return null;

		return new PacketS0BSetGamePhase<>(GamePhase.values()[buf.readInt()]);
	}

	private GamePhase phase;

	private PacketS0BSetGamePhase(GamePhase phase) {
		this.phase = phase;
	}

	@Override
	public void handle(D playerData) throws Exception {
		playerData.phase = phase;
	}

}
