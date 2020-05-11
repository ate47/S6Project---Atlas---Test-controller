package ssixprojet.server.packet.server;

import io.netty.buffer.ByteBuf;
import ssixproject.client.PlayerData;
import ssixprojet.server.packet.PacketServer;

public class PacketS0FScorePlayer extends PacketServer {
	public static PacketS0FScorePlayer create(ByteBuf buf) {
		if (!buf.isReadable(8 * 4))
			return null;
		return new PacketS0FScorePlayer(buf);
	}

	private int infectionSortId;
	private int survivorSortId;
	private int damageGiven = 0;
	private int damageTaken = 0;
	private int death = 0;
	private int kills = 0;
	private int infections = 0;
	private int timeAlive = 0;

	private PacketS0FScorePlayer(ByteBuf buf) {
		survivorSortId = buf.readInt();
		infectionSortId = buf.readInt();
		damageGiven = buf.readInt();
		damageTaken = buf.readInt();
		death = buf.readInt();
		infections = buf.readInt();
		kills = buf.readInt();
		timeAlive = buf.readInt();
	}

	@Override
	public void handle(PlayerData playerData) throws Exception {
		playerData.infectionSortId = infectionSortId;
		playerData.survivorSortId = survivorSortId;
		playerData.damageGiven = damageGiven;
		playerData.damageTaken = damageTaken;
		playerData.death = death;
		playerData.kills = kills;
		playerData.infections = infections;
		playerData.timeAlive = timeAlive;
	}
}
