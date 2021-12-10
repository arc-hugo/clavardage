package gei.clavardage.reseau.services;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceEnvoiUDP extends Service<Void> {

	private String message;
	private InetAddress hote;
	private boolean broadcast;

	public ServiceEnvoiUDP(String message, InetAddress hote, boolean broadcast) {
		this.message = message;
		this.hote = hote;
		this.broadcast = broadcast;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				DatagramSocket sock = new DatagramSocket();
				if (broadcast) {
					sock.setBroadcast(true);

					Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
					while (interfaces.hasMoreElements()) {
						NetworkInterface inter = interfaces.nextElement();
						if (!inter.isLoopback()) {
							for (InterfaceAddress interAdd : inter.getInterfaceAddresses()) {
								InetAddress broadcast = interAdd.getBroadcast();
								if (broadcast != null) {
									DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), broadcast,
											ServiceReceptionUDP.RECEPTION_PORT);
									sock.send(packet);
								}
							}
						}
					}

				} else {
					DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), hote,
							ServiceReceptionUDP.RECEPTION_PORT);
					sock.send(packet);
				}
				sock.close();
				return null;
			}
		};
	}

}
