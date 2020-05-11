package ssixproject.controller;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.SwingUtilities;

import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerButton;
import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerUnpluggedException;

import ssixproject.client.GamePhase;
import ssixproject.client.MasterData;
import ssixproject.client.PlayerData;
import ssixproject.client.PlayerType;
import ssixproject.controller.window.GameWindow;
import ssixprojet.server.packet.MasterPacketHandler;
import ssixprojet.server.packet.PacketManager;
import ssixprojet.server.packet.PlayerPacketHandler;
import ssixprojet.server.packet.client.PacketC04Move;
import ssixprojet.server.packet.client.PacketC05Shot;
import ssixprojet.server.packet.client.PacketC08LaunchPlayingPhase;
import ssixprojet.server.packet.client.PacketC09SendInfection;
import ssixprojet.server.packet.client.PacketC0ARestart;
import ssixprojet.server.packet.server.PacketS02PlayerRegister;
import ssixprojet.server.packet.server.PacketS06PlayerType;
import ssixprojet.server.packet.server.PacketS09ChangeHealth;
import ssixprojet.server.packet.server.PacketS0AChangeAmmos;
import ssixprojet.server.packet.server.PacketS0BSetGamePhase;
import ssixprojet.server.packet.server.PacketS0CBadPassword;
import ssixprojet.server.packet.server.PacketS0DMasterLogged;
import ssixprojet.server.packet.server.PacketS0ETimeToWaitPing;
import ssixprojet.server.packet.server.PacketS0FScorePlayer;

public class XAtlas {
	public static void main(String[] args) {
		new XAtlas(Config.query()).start();
	}

	private final BlockingQueue<Runnable> actions = new ArrayBlockingQueue<>(1024);
	public final PlayerPacketHandler[] handlers;
	public final MasterPacketHandler masterHandler;
	private int selectedHandler = 0;
	private final ControllerManager manager = new ControllerManager();
	private final ControllerIndex index;
	public final Config config;
	public final PacketManager<PlayerData> playerPacketManager;
	public final PacketManager<MasterData> masterPacketManager;
	private GameWindow window;

	private boolean started = true;

	public XAtlas(Config config) {
		this.config = config;

		this.playerPacketManager = new PacketManager<>();

		playerPacketManager.registerPacket(0x02, PacketS02PlayerRegister::create);
		playerPacketManager.registerPacket(0x06, PacketS06PlayerType::create);
		playerPacketManager.registerPacket(0x09, PacketS09ChangeHealth::create);
		playerPacketManager.registerPacket(0x0A, PacketS0AChangeAmmos::create);
		playerPacketManager.registerPacket(0x0B, PacketS0BSetGamePhase::create);
		playerPacketManager.registerPacket(0x0E, PacketS0ETimeToWaitPing::create);
		playerPacketManager.registerPacket(0x0F, PacketS0FScorePlayer::create);

		this.masterPacketManager = new PacketManager<>();

		masterPacketManager.registerPacket(0x0B, PacketS0BSetGamePhase::create);
		masterPacketManager.registerPacket(0x0C, PacketS0CBadPassword::create);
		masterPacketManager.registerPacket(0x0D, PacketS0DMasterLogged::create);
		masterPacketManager.registerPacket(0x0E, PacketS0ETimeToWaitPing::create);

		if (!config.masterPassword.isEmpty())
			masterHandler = new MasterPacketHandler(this, masterPacketManager, new MasterData(config.masterPassword));
		else
			masterHandler = null;

		manager.initSDLGamepad();
		index = manager.getControllerIndex(0);
		handlers = new PlayerPacketHandler[config.playerCount];
		if (config.playerCount == 1) {
			handlers[0] = new PlayerPacketHandler(this, playerPacketManager, new PlayerData(config.username));
		} else
			for (int i = 0; i < handlers.length; i++)
				handlers[i] = new PlayerPacketHandler(this, playerPacketManager,
						new PlayerData(config.username + " #" + (i + 1)));
		window = new GameWindow(() -> getSelectedHandler().getData(),
				masterHandler == null ? null : masterHandler.getData());
	}

	public boolean isStarted() {
		return started;
	}

	public synchronized PlayerPacketHandler getSelectedHandler() {
		return handlers[selectedHandler];
	}

	public synchronized PlayerPacketHandler selectHandler(int handlerID) {
		PlayerPacketHandler handler = handlers[selectedHandler = handlerID];
		handler.getData().log("Selected");
		return handler;
	}

	public synchronized PlayerPacketHandler selectNextHandler() {
		PlayerPacketHandler handler = handlers[selectedHandler = (selectedHandler + 1) % handlers.length];
		handler.getData().log("Selected");
		return handler;
	}

	public synchronized PlayerPacketHandler selectLastHandler() {
		PlayerPacketHandler handler = handlers[selectedHandler = (selectedHandler - 1 + handlers.length)
				% handlers.length];
		handler.getData().log("Selected");
		return handler;
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
		for (PlayerPacketHandler packetHandler : handlers)
			packetHandler.start();
		if (masterHandler != null)
			masterHandler.start();

		final long rate = 1000 / 20;
		while (started) {
			long start = System.currentTimeMillis();

			Runnable r;

			SwingUtilities.invokeLater(window::repaint);

			while ((r = actions.poll()) != null) {
				r.run();
			}

			try {
				if (index.isButtonJustPressed(ControllerButton.START)) {
					window.toggleMaster();
				}

				if (window.isMasterSelected()) {
					MasterData data = masterHandler.getData();
					if (index.isButtonJustPressed(ControllerButton.A)) { // button 1
						switch (data.phase) {
						case WAITING:
							masterHandler.sendPacket(new PacketC08LaunchPlayingPhase());
							break;
						case PLAYING:
							masterHandler.sendPacket(new PacketC09SendInfection(10));
							break;
						case SCORE:
							masterHandler.sendPacket(new PacketC0ARestart());
							break;
						}
					} else if (index.isButtonJustPressed(ControllerButton.X)) { // button 2

					} else if (index.isButtonJustPressed(ControllerButton.Y)) { // button 3

					} else if (index.isButtonJustPressed(ControllerButton.B)) {
						window.selectGame();
					}
				} else {
					PlayerPacketHandler packetHandler;

					if (index.isButtonJustPressed(ControllerButton.A)) {
						packetHandler = selectNextHandler();
					} else if (index.isButtonJustPressed(ControllerButton.B)) {
						packetHandler = selectLastHandler();
					} else
						packetHandler = getSelectedHandler();

					PlayerData playerData = packetHandler.getData();

					if (playerData.phase == GamePhase.PLAYING) {
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
