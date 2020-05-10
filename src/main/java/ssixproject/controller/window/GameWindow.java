package ssixproject.controller.window;

import javax.swing.JFrame;

import ssixproject.client.PlayerData;

public class GameWindow extends JFrame {
	private static final long serialVersionUID = -7220370717863638605L;

	public GameWindow(PlayerData data) {
		super("XAtlas Client");
		setResizable(false);
		setSize(800, 500);
		setLocationRelativeTo(null);
		setContentPane(new GameWindowPanel(data));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

}
