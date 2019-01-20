package com.devnautica.czeidcardservice;

import com.devnautica.czeidcardservice.utils.InMemoryData;
import com.devnautica.czeidcardservice.websocket.TestHWStatus;
import com.devnautica.czeidcardservice.websocket.server.EventServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.Map;

/*

Multiplatformni open-source identifikacni klient
Prihlasovani pomoci JS knihovny komunikujici s identifikacnim klientem
Priklad aplikace pouzivajici prihlasovani ( napr. peticni/volebni system )

umi podepisovat privatnim klicem od kratkyho certifikatu

1) podpis stringu, public klic
2) vytazeni privatu

 */
public class Main {
	public static void main(String... args){
		TestHWStatus.startChecking();

		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(6969);
		server.addConnector(connector);

		// Setup the basic application "context" for this application at "/"
		// This is also known as the handler tree (in jetty speak)
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		// Add a websocket to a specific path spec
		ServletHolder holderEvents = new ServletHolder("msg", EventServlet.class);
		context.addServlet(holderEvents, "/websocket/*");

		try{
			server.start();
			startUI();

			server.join();

		}catch (Throwable t){
			t.printStackTrace(System.err);
		}
	}
	public static void startUI() {
		//ECDSA

		/* Use an appropriate Look and Feel */
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | ClassNotFoundException e) {
		}

		/* Turn off metal's use of bold fonts */
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		//Schedule a job for the event-dispatching thread:
		//adding TrayIcon.
		SwingUtilities.invokeLater(() -> createAndShowGUI());
	}

	private static void createAndShowGUI() {
		//Check the SystemTray support
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		final PopupMenu popup = new PopupMenu();
		BufferedImage trayIconImage;
		try {
			trayIconImage = ImageIO.read(Main.class.getResource("/images/pp_white_bg.png"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//TODO: This tray icon width fits only my system
		int trayIconWidth = (int) (new TrayIcon(trayIconImage).getSize().width / 1.5);
		TrayIcon trayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH));
		InMemoryData.getInstance().setTrayIcon(trayIcon);

		final SystemTray tray = SystemTray.getSystemTray();

		// Create a popup menu components
		MenuItem viewPermissions = new MenuItem("View permissions");
		MenuItem exitItem = new MenuItem("Exit");

		//Add components to popup menu
		popup.add(viewPermissions);
		//popup.addSeparator();
		popup.addSeparator();
		popup.add(exitItem);

		trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
			return;
		}

		trayIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"This dialog box is run from System Tray");
			}
		});

		viewPermissions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				StringBuilder sb = new StringBuilder();
				for(Map.Entry<String,List<String>> ks : InMemoryData.getInstance().getAllowedMsgs().entrySet()){
					sb.append("\nWebsite: "+ks.getKey());
					for(String s : ks.getValue()){
						sb.append("\nMessage: "+s);
					}
				}

				JOptionPane.showMessageDialog(null,
						sb.toString()
				);
			}
		});

		exitItem.addActionListener(e -> {
			tray.remove(trayIcon);
			System.exit(0);
		});
	}

	//Obtain the image URL
	protected static Image createImage(String path, String description) {
		URL imageURL = Main.class.getResource(path);

		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}
}
