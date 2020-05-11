package ssixprojet.server.packet;

import ssixproject.client.PlayerData;
import ssixproject.controller.XAtlas;
import ssixprojet.server.packet.client.PacketC00ConnectPlayer;
import ssixprojet.server.packet.client.PacketC03ReconnectPlayer;

public class PlayerPacketHandler extends PacketHandler<PlayerData> {

	public PlayerPacketHandler(XAtlas xAtlas, PacketManager<PlayerData> packetManager, PlayerData data) {
		super(xAtlas, packetManager, data);
	}

	@Override
	public void onOpen() {
		if (data.playerUUID != null) {
			sendPacket(new PacketC03ReconnectPlayer(data.playerUUID, data.username));
		} else {
			sendPacket(new PacketC00ConnectPlayer(data.username));
		}
	}

}
