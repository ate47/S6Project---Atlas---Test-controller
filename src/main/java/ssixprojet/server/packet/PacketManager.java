package ssixprojet.server.packet;

import java.util.UUID;

import org.apache.commons.io.Charsets;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import ssixprojet.server.packet.Packet.PacketBuilder;

public class PacketManager<D> {
	/**
	 * read an UTF8 string from a buffer
	 * 
	 * @param buf
	 *            the buffer
	 * @return the string or null if can't read enough bytes
	 */
	public static String readUTF8String(ByteBuf buf) {
		if (!buf.isReadable(4))
			return null;

		int size = buf.readInt();
		byte[] bytes = new byte[size];

		if (!buf.isReadable(size))
			return null;
		buf.readBytes(bytes);
		return new String(bytes, Charsets.UTF_8);
	}

	/**
	 * read a UUID from a buffer
	 * 
	 * @param buf
	 *            the buffer
	 * @return the uuid or null if can't read enough bytes
	 */
	public static UUID readUUID(ByteBuf buf) {
		if (!buf.isReadable(16))
			return null;
		long most = buf.readLong();
		long least = buf.readLong();
		return new UUID(most, least);
	}

	@SuppressWarnings("unchecked")
	private PacketBuilder<? extends PacketServer<D>>[] packets = new PacketBuilder[256];

	public PacketManager() {}

	/**
	 * build a packet from a {@link TextWebSocketFrame}
	 * 
	 * @param frame
	 *            the frame
	 * @return the packet or null if an error occurred
	 */
	public PacketServer<D> buildPacket(BinaryWebSocketFrame frame) {
		ByteBuf buffer = frame.content();
		try {
			if (!buffer.isReadable(4))
				return null;
			int type = (int) buffer.readUnsignedInt(); // read u32

			return buildPacket(type, buffer);
		} catch (Exception e) {
			return null;
		}
	}

	public PacketServer<D> buildPacket(int type, ByteBuf buffer) {
		if (type < 0 || type >= packets.length) {
			return null;
		}
		// get the packet builder for this type
		PacketBuilder<? extends PacketServer<D>> bld = packets[type];
		if (bld == null) {
			return null;
		}

		// build the packet and release the buffer data
		return bld.build(buffer);
	}

	/**
	 * register a {@link PacketBuilder} for client packets
	 * 
	 * @param packetId
	 *            the packet id
	 * @param builder
	 *            the builder
	 */
	public void registerPacket(int packetId, PacketBuilder<? extends PacketServer<D>> builder) {
		if (packets.length <= packetId || packetId < 0)
			throw new IllegalArgumentException("Bad packet id");
		if (packets[packetId] != null)
			throw new IllegalArgumentException("Already registered packet");

		packets[packetId] = builder;
	}

}
