package ssixprojet.server.packet.client;

import io.netty.buffer.ByteBuf;
import ssixprojet.server.packet.PacketClient;

public class PacketC04Move extends PacketClient {
	private double deltaX, deltaY, lookX, lookY;

	public PacketC04Move(double deltaX, double deltaY, double lookX, double lookY) {
		super(0x04, 8 * 4);
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		this.lookX = lookX;
		this.lookY = lookY;
	}

	@Override
	public void write(ByteBuf buf) {
		buf.writeDouble(deltaX);
		buf.writeDouble(deltaY);
		buf.writeDouble(lookX);
		buf.writeDouble(lookY);
	}

}
