package ssixproject.controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Config {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static final File SAVE_FILE = new File("controller.json");

	/**
	 * @return the loaded (or new) config from the config file
	 */
	public static Config loadFromFile() {
		try (Reader r = new FileReader(SAVE_FILE)) {
			return GSON.fromJson(r, Config.class);
		} catch (Exception e) {
			return new Config();
		}
	}

	/**
	 * @return show a frame to select config and return it
	 */
	public static Config query() {
		Config cfg = loadFromFile();

		JFrame frame = new JFrame("XAtlas");
		frame.setLocationRelativeTo(null);
		frame.setSize(400, 420);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel(null);
		panel.setBackground(Color.WHITE);
		JLabel usernameLabel = new JLabel("Pseudo");
		usernameLabel.setBounds(20, 0, 360, 60);
		JTextField username = new JTextField(cfg.username);
		username.setBounds(20, 60, 360, 40);
		JLabel addressLabel = new JLabel("Serveur");
		addressLabel.setBounds(20, 100, 360, 60);
		JTextField address = new JTextField(cfg.serverAddress);
		address.setBounds(20, 160, 360, 40);

		Integer[] count = new Integer[20];

		for (int i = 0; i < count.length; i++)
			count[i] = i + 1;

		JComboBox<Integer> playerCount = new JComboBox<>(count);
		playerCount.setBounds(20, 240, 360, 40);
		playerCount.setSelectedIndex(cfg.playerCount > 0 && cfg.playerCount <= count.length ? cfg.playerCount - 1 : 0);

		JButton launch = new JButton("Lancer");
		launch.setBounds(20, 320, 360, 40);
		launch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] add = address.getText().split("[:]");
				if (add.length > 2)
					return;
				cfg.serverAddress = address.getText();
				cfg.serverHost = add[0];
				try {
					cfg.serverPort = Integer.valueOf(add[1]);
				} catch (Exception e2) {
					return;
				}

				cfg.username = username.getText();
				Integer item = (Integer) playerCount.getSelectedItem();
				cfg.playerCount = item == null ? 1 : item.intValue();
				frame.setVisible(false);
				synchronized (cfg) {
					cfg.notify();
				}
			}
		});
		launch.setBackground(Color.LIGHT_GRAY);
		launch.setForeground(Color.BLACK);
		launch.setBorderPainted(false);

		panel.add(usernameLabel);
		panel.add(username);
		panel.add(addressLabel);
		panel.add(address);
		panel.add(playerCount);
		panel.add(launch);

		frame.setContentPane(panel);
		frame.setVisible(true);

		synchronized (cfg) {
			try {
				cfg.wait();
			} catch (InterruptedException e) {
				throw new Error(e);
			}
			cfg.saveToFile();
			return cfg;
		}
	}

	public String serverHost = "127.0.0.1";
	public int serverPort = 2080;
	public String serverAddress = serverHost + ":" + serverPort;
	public String username = "XPlayer";
	public int playerCount = 1;

	/**
	 * save the config to the config file
	 */
	public void saveToFile() {
		try (FileWriter w = new FileWriter(SAVE_FILE)) {
			GSON.toJson(this, w);
		} catch (Exception e) {}
	}
}
