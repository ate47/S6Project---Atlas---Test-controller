package ssixprojet.server.packet;

public abstract class PacketServer<D> implements Packet {

	/**
	 * handle the packet
	 * 
	 * @throws Exception
	 *             if the packet throw an exception
	 */
	public abstract void handle(D data) throws Exception;
}
