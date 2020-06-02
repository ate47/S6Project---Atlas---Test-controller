package ssixprojet.server.packet;

import ssixproject.client.Data;
import ssixproject.controller.XAtlas;
import ssixprojet.server.packet.client.PacketC02ConnectScreen;

public class FakeScreenPacketHandler extends PacketHandler<Data> {

	public FakeScreenPacketHandler(XAtlas xAtlas) {
		super(xAtlas, new PacketManager<>(), new Data());
	}

	@Override
	public void onOpen() {
		sendPacket(new PacketC02ConnectScreen());
	}

}
