package ssixprojet.server.packet.client;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import ssixprojet.server.packet.PacketClient;

public class PacketC03ReconnectPlayer extends PacketClient {
	private UUID uuid;
	private byte[] name;

	public PacketC03ReconnectPlayer(UUID uuid, String name) {
		super(0x03, 16);
		this.uuid = uuid;
		this.name = prepareUTF8String(name);
	}

	@Override
	public void write(ByteBuf buf) {
		writeUUID(buf, uuid);
		writeUTF8String(buf, name);
	}

}
