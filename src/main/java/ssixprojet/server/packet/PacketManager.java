package ssixprojet.server.packet;

import java.util.UUID;

import org.apache.commons.io.Charsets;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import ssixprojet.server.packet.Packet.PacketBuilder;
import ssixprojet.server.packet.server.PacketS02PlayerRegister;
import ssixprojet.server.packet.server.PacketS06PlayerType;
import ssixprojet.server.packet.server.PacketS09ChangeHealth;
import ssixprojet.server.packet.server.PacketS0AChangeAmmos;
import ssixprojet.server.packet.server.PacketS0BSetGamePhase;
import ssixprojet.server.packet.server.PacketS0ETimeToWaitPing;
import ssixprojet.server.packet.server.PacketS0FScorePlayer;

public class PacketManager {
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
	private PacketBuilder<? extends PacketServer>[] packets = new PacketBuilder[256];

	public PacketManager() {
		registerPacket(0x02, PacketS02PlayerRegister::create);
		registerPacket(0x06, PacketS06PlayerType::create);
		registerPacket(0x09, PacketS09ChangeHealth::create);
		registerPacket(0x0A, PacketS0AChangeAmmos::create);
		registerPacket(0x0B, PacketS0BSetGamePhase::create);
		registerPacket(0x0E, PacketS0ETimeToWaitPing::create);
		registerPacket(0x0F, PacketS0FScorePlayer::create);
	}

	/**
	 * build a packet from a {@link TextWebSocketFrame}
	 * 
	 * @param frame
	 *            the frame
	 * @return the packet or null if an error occurred
	 */
	public PacketServer buildPacket(BinaryWebSocketFrame frame) {
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

	public PacketServer buildPacket(int type, ByteBuf buffer) {
		if (type < 0 || type >= packets.length) {
			return null;
		}
		// get the packet builder for this type
		PacketBuilder<?> bld = packets[type];
		if (bld == null) {
			return null;
		}

		// build the packet and release the buffer data
		return (PacketServer) bld.build(buffer);
	}

	/**
	 * register a {@link PacketBuilder} for client packets
	 * 
	 * @param packetId
	 *            the packet id
	 * @param builder
	 *            the builder
	 */
	public void registerPacket(int packetId, PacketBuilder<? extends PacketServer> builder) {
		if (packets.length <= packetId || packetId < 0)
			throw new IllegalArgumentException("Bad packet id");
		if (packets[packetId] != null)
			throw new IllegalArgumentException("Already registered packet");

		packets[packetId] = builder;
	}

}
