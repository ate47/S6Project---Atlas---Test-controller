package ssixproject.controller.window;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import ssixproject.client.MasterData;

public class MasterWindowPanel extends JPanel {
	private static final long serialVersionUID = -7220370717863638605L;
	private MasterData data;

	public MasterWindowPanel(MasterData data) {
		super(null);
		this.data = data;
		setBackground(Color.WHITE);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int windowWidth = getWidth();
		int windowHeight = getHeight();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, windowWidth, windowHeight);
		if (!data.connected) {
			g.setFont(g.getFont().deriveFont(20F));
			g.setColor(Color.BLACK);
			GameWindowPanel.drawCenteredString(g, "Connexion...", windowWidth / 2, windowHeight / 2);
			return;
		}

		g.setFont(g.getFont().deriveFont(80F));
		g.setColor(Color.BLACK);
		GameWindowPanel.drawCenteredString(g, "Master", windowWidth / 2, windowHeight / 2 - 100);
		
		g.setFont(g.getFont().deriveFont(20F));
		switch (data.phase) {
		case WAITING:
			GameWindowPanel.drawCenteredString(g, "A : lancer infection", windowWidth / 2, windowHeight / 2);
			GameWindowPanel.drawCenteredString(g, "B : retour", windowWidth / 2, windowHeight / 2 + 30);
			break;
		case PLAYING:
			GameWindowPanel.drawCenteredString(g, "A : infecter 10%", windowWidth / 2, windowHeight / 2);
			GameWindowPanel.drawCenteredString(g, "B : retour", windowWidth / 2, windowHeight / 2 + 30);
			break;
		case SCORE:
			GameWindowPanel.drawCenteredString(g, "A : relancer", windowWidth / 2, windowHeight / 2);
			GameWindowPanel.drawCenteredString(g, "B : retour", windowWidth / 2, windowHeight / 2 + 30);
			break;
		}
	}
}
