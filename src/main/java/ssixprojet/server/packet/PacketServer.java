package ssixprojet.server.packet;

import ssixproject.controller.XAtlas;

public abstract class PacketServer implements Packet {

	/**
	 * handle the packet
	 * 
	 * @throws Exception
	 *             if the packet throw an exception
	 */
	public abstract void handle(XAtlas src) throws Exception;
}
