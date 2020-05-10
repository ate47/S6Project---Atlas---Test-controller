package ssixproject.client;

import java.util.UUID;

public class PlayerData {
	public UUID playerUUID = null;
	public int health = 100;
	public int ammos = 0;
	public PlayerType type = PlayerType.SURVIVOR;
	public int survivorSortId = 0;
	public int infectionSortId = 0;
	public int damageGiven = 0;
	public int damageTaken = 0;
	public int death = 0;
	public int infections = 0;
	public int kills = 0;
	public int timeAlive = 0;
	public int id = 0;
	public int timeToWait = 0;
	public GamePhase phase = GamePhase.WAITING;
}
