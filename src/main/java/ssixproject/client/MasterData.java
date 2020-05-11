package ssixproject.client;

public class MasterData extends Data {
	public final String password;
	public boolean connected = false;

	public MasterData(String password) {
		this.password = password;
	}
}
