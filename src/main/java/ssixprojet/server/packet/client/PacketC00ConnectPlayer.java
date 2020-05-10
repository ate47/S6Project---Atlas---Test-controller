package ssixprojet.server.packet.client;

import io.netty.buffer.ByteBuf;
import ssixprojet.server.packet.PacketClient;

public class PacketC00ConnectPlayer extends PacketClient {
	private byte[] name;

	public PacketC00ConnectPlayer(String name) {
		super(0x00, 0);
		this.name = prepareUTF8String(name);
	}

	@Override
	public void write(ByteBuf buf) {
		writeUTF8String(buf, name);
	}

}
