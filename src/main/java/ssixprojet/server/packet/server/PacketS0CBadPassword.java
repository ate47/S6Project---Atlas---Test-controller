package ssixprojet.server.packet.server;

import javax.swing.JOptionPane;

import io.netty.buffer.ByteBuf;
import ssixproject.client.MasterData;
import ssixprojet.server.packet.PacketServer;

public class PacketS0CBadPassword extends PacketServer<MasterData> {

	public static PacketS0CBadPassword create(ByteBuf buf) {
		return new PacketS0CBadPassword();
	}

	private PacketS0CBadPassword() {}

	@Override
	public void handle(MasterData data) throws Exception {
		JOptionPane.showMessageDialog(null, "Mot de passe Master faux!");
		System.exit(0);
	}
}
