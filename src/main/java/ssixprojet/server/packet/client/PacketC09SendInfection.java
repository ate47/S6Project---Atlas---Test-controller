package ssixprojet.server.packet.client;

import io.netty.buffer.ByteBuf;
import ssixprojet.server.packet.PacketClient;

public class PacketC09SendInfection extends PacketClient {

	private int percentage;

	public PacketC09SendInfection(int percentage) {
		super(0x09, 4);
		this.percentage = Math.min(Math.max(percentage, 0), 100);
	}

	@Override
	public void write(ByteBuf buf) {
		buf.writeInt(percentage);
	}

}
