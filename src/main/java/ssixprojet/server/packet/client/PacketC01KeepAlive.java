package ssixprojet.server.packet.client;

import ssixprojet.server.packet.PacketClient;

public class PacketC01KeepAlive extends PacketClient {

	public PacketC01KeepAlive() {
		super(0x01, 0);
	}

}
