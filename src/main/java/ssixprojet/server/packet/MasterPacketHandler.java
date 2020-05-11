package ssixprojet.server.packet;

import ssixproject.client.MasterData;
import ssixproject.controller.XAtlas;
import ssixprojet.server.packet.client.PacketC07ConnectMaster;

public class MasterPacketHandler extends PacketHandler<MasterData> {

	public MasterPacketHandler(XAtlas xAtlas, PacketManager<MasterData> packetManager, MasterData data) {
		super(xAtlas, packetManager, data);
	}

	@Override
	public void onOpen() {
		data.connected = false;
		sendPacket(new PacketC07ConnectMaster(data.password));
	}

}
