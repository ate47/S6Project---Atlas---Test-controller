package ssixproject.controller.window;

import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ssixproject.client.MasterData;
import ssixproject.client.PlayerData;

public class GameWindow extends JFrame {
	private static final long serialVersionUID = -7220370717863638605L;

	private JPanel game, master;
	private boolean masterSelected = false;

	public GameWindow(Supplier<PlayerData> data, MasterData master) {
		super("XAtlas Client");
		setResizable(false);
		setSize(800, 500);
		setLocationRelativeTo(null);
		this.game = new GameWindowPanel(data);
		this.master = master == null ? null : new MasterWindowPanel(master);

		this.game.setSize(getSize());
		if (this.master != null)
			this.master.setSize(getSize());

		setContentPane(this.game);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void selectGame() {
		setContentPane(game);
		masterSelected = false;
		game.repaint();
	}

	public void selectMaster() {
		if (master != null) {
			setContentPane(master);
			masterSelected = true;
			master.repaint();
		}
	}

	public boolean isMasterSelected() {
		return masterSelected;
	}

	public void toggleMaster() {
		if (masterSelected)
			selectGame();
		else
			selectMaster();
	}

}
