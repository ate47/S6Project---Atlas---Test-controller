package ssixproject.controller.window;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.function.Supplier;

import javax.swing.JPanel;

import ssixproject.client.PlayerData;
import ssixproject.controller.XAtlas;

public class GameWindowPanel extends JPanel {
	private static final long serialVersionUID = -7220370717863638605L;

	public static void drawCenteredString(Graphics g, String text, int x, int y) {
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		int cx = x - metrics.stringWidth(text) / 2;
		// Determine the Y coordinate for the text (note we add the ascent, as in java
		// 2d 0 is top of the screen)
		int cy = y + -metrics.getHeight() / 2 + metrics.getAscent();
		g.drawString(text, cx, cy);
	}

	private static int textWidth(Graphics g, String text) {
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		return metrics.stringWidth(text);
	}

	private final Supplier<PlayerData> data;
	private final XAtlas xatlas;

	public GameWindowPanel(Supplier<PlayerData> data, XAtlas xatlas) {
		super(null);
		this.data = data;
		this.xatlas = xatlas;
		setBackground(Color.WHITE);
	}

	public static final Color INFECTED_COLOR = new Color(0, 80, 0);
	public static final Color SURVIVOR_COLOR = new Color(0, 0, 80);

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		PlayerData data = this.data.get();
		int windowWidth = getWidth();
		int windowHeight = getHeight();
		switch (data.phase) {
		case WAITING:
			// display background
			g.setColor(SURVIVOR_COLOR);
			g.fillRect(0, 0, windowWidth, windowHeight);
			g.setFont(g.getFont().deriveFont(20F));
			g.setColor(Color.WHITE);
			drawCenteredString(g, "En attente de lancement...", windowWidth / 2, windowHeight / 2);
			break;
		case PLAYING:
			g.setFont(g.getFont().deriveFont(windowHeight / 10F));
			switch (data.type) {
			case INFECTED:
				// display background
				g.setColor(INFECTED_COLOR);
				g.fillRect(0, 0, windowWidth, windowHeight);

				g.translate(windowWidth / 2, windowHeight / 5);
				g.setColor(Color.GRAY);
				g.fillRect(-windowWidth / 4, -windowHeight / 20, windowWidth / 2, windowHeight / 10);

				if (data.health < 25)
					g.setColor(Color.RED);
				else if (data.health < 50)
					g.setColor(Color.YELLOW);
				else
					g.setColor(Color.GREEN);

				g.fillRect(-windowWidth / 4, -windowHeight / 20, data.health * windowWidth / 200, windowHeight / 10);

				g.setColor(Color.BLACK);
				drawCenteredString(g, "Vie: " + data.health, 0, 0);

				break;
			case SURVIVOR:
				// display background
				g.setColor(SURVIVOR_COLOR);
				g.fillRect(0, 0, windowWidth, windowHeight);

				g.translate(windowWidth / 2, windowHeight / 5);
				g.setColor(Color.WHITE);
				drawCenteredString(g, "Munitions: " + data.ammos, 0, 0);

				break;
			}

			g.translate(-windowWidth / 2, -windowHeight / 5);

			if (data.timeToWait > 0) {
				g.setFont(g.getFont().deriveFont(windowHeight / 10F));
				String txt = "Infection dans " + data.timeToWait + "s";
				int tw = (int) (textWidth(g, txt) * 1.25);
				g.setColor(Color.BLACK);
				g.fillRect(windowWidth / 2 - tw / 2, 0, tw, windowHeight / 10);
				g.setColor(Color.WHITE);
				drawCenteredString(g, txt, windowWidth / 2, windowHeight / 20);
			}

			g.setColor(Color.WHITE);
			g.setFont(g.getFont().deriveFont(windowWidth / 5F));
			drawCenteredString(g, String.valueOf(data.id), windowWidth / 2, windowHeight * 3 / 5);
			g.setFont(g.getFont().deriveFont(windowWidth / 30F));
			if (xatlas.isDancingModeEnabled())
				drawCenteredString(g, "Dancing mode enabled", windowWidth / 2, 8 * windowHeight / 9);
			break;
		case SCORE:
			switch (data.type) {
			case INFECTED:
				// display background
				g.setColor(INFECTED_COLOR);
				break;
			case SURVIVOR:
				// display background
				g.setColor(SURVIVOR_COLOR);
				break;
			}
			g.fillRect(0, 0, windowWidth, windowHeight);
			break;
		}

		g.setColor(Color.WHITE);
		g.setFont(g.getFont().deriveFont(windowWidth / 25F));
		drawCenteredString(g, data.username, windowWidth / 2, windowHeight * 9 / 10);
	}

}
