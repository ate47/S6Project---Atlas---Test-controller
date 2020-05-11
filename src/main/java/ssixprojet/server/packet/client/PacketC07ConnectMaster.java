package ssixprojet.server.packet.client;

import io.netty.buffer.ByteBuf;
import ssixprojet.server.packet.PacketClient;

public class PacketC07ConnectMaster extends PacketClient {
	private byte[] password;

	public PacketC07ConnectMaster(String password) {
		super(0x07, 0);
		this.password = prepareUTF8String(password);
	}

	@Override
	public void write(ByteBuf buf) {
		writeUTF8String(buf, password);
	}

}
