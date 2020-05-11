package ssixprojet.server.packet;

import ssixproject.client.PlayerData;

public abstract class PacketServer implements Packet {

	/**
	 * handle the packet
	 * 
	 * @throws Exception
	 *             if the packet throw an exception
	 */
	public abstract void handle(PlayerData data) throws Exception;
}
