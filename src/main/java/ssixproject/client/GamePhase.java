package ssixproject.client;

public enum GamePhase {
	WAITING(0),
	PLAYING(1),
	SCORE(2);

	private int id;

	private GamePhase(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
