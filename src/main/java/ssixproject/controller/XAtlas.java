package ssixproject.controller;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.SwingUtilities;

import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerUnpluggedException;

import ssixproject.client.GamePhase;
import ssixproject.client.PlayerData;
import ssixproject.client.PlayerType;
import ssixproject.controller.window.GameWindow;
import ssixprojet.server.packet.PacketHandler;
import ssixprojet.server.packet.PacketManager;
import ssixprojet.server.packet.client.PacketC04Move;
import ssixprojet.server.packet.client.PacketC05Shot;

public class XAtlas {
	public static void main(String[] args) {
		new XAtlas(Config.query()).start();
	}

	private final BlockingQueue<Runnable> actions = new ArrayBlockingQueue<>(1024);
	public final PlayerData playerData = new PlayerData();
	private final ControllerManager manager = new ControllerManager();
	private final ControllerIndex index;
	public final Config config;
	public final PacketHandler packetHandler;
	public final PacketManager packetManager = new PacketManager();
	private GameWindow window;

	private boolean started = true;

	public XAtlas(Config config) {
		this.config = config;
		manager.initSDLGamepad();
		index = manager.getControllerIndex(0);
		packetHandler = new PacketHandler(this);
		window = new GameWindow(playerData);
	}

	public boolean isStarted() {
		return started;
	}

	private void sleepNE(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {}
	}

	public void doAction(Runnable action) {
		try {
			actions.put(action);
		} catch (InterruptedException e) {}
	}

	public void start() {
		window.setVisible(true);
		packetHandler.start();
		final long rate = 1000 / 20;
		while (started) {
			long start = System.currentTimeMillis();

			Runnable r;

			SwingUtilities.invokeLater(window::repaint);

			while ((r = actions.poll()) != null) {
				r.run();
			}

			if (playerData.phase == GamePhase.PLAYING)
				try {
					float lx = index.getAxisState(ControllerAxis.LEFTX);
					float ly = -index.getAxisState(ControllerAxis.LEFTY);
					float rx = index.getAxisState(ControllerAxis.RIGHTX);
					float ry = -index.getAxisState(ControllerAxis.RIGHTY);

					float ld = (float) Math.sqrt(lx * lx + ly * ly);
					float rd = (float) Math.sqrt(rx * rx + ry * ry);

					if (ld > 1) {
						lx /= ld;
						ly /= ld;
					}
					if (rd > 1) {
						rx /= rd;
						ry /= rd;
					}
					boolean r00 = rd < 0.1;
					boolean l00 = ld < 0.1;
					if (!l00 || !r00) {
						if (!r00) {
							packetHandler.sendPacket(new PacketC04Move(lx, ly, rx, ry));
						} else
							packetHandler.sendPacket(new PacketC04Move(lx, ly, lx, ly));

						if (playerData.type == PlayerType.SURVIVOR && rd > 0.75F) {
							packetHandler.sendPacket(new PacketC05Shot());
							index.doVibration(0.25F, 0.25F, (int) rate);
						}
					}
				} catch (ControllerUnpluggedException e) {
					System.out.println("Il faut brancher une manette!");
				}

			long deltaTime = start + rate - System.currentTimeMillis();
			if (deltaTime > 0)
				sleepNE(deltaTime);
		}
	}

	public void stop() {
		started = false;
	}
}
