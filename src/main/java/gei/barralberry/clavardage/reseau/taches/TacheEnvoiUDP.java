package gei.barralberry.clavardage.reseau.taches;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import gei.barralberry.clavardage.util.Configuration;
import javafx.concurrent.Task;

public class TacheEnvoiUDP extends Task<Void> {

	private String message;
	private InetAddress hote;
	private boolean broadcast;

	public TacheEnvoiUDP(String message, InetAddress hote, boolean broadcast) {
		this.message = message;
		this.hote = hote;
		this.broadcast = broadcast;
	}
	
	@Override
	protected Void call() throws Exception {
		DatagramSocket sock = new DatagramSocket();
		if (this.broadcast) {
			sock.setBroadcast(true);

			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface inter = interfaces.nextElement();
				if (!inter.isLoopback()) {
					for (InterfaceAddress interAdd : inter.getInterfaceAddresses()) {
						InetAddress broadcast = interAdd.getBroadcast();
						if (broadcast != null) {
							DatagramPacket packet = new DatagramPacket(this.message.getBytes(), this.message.length(), broadcast, Configuration.UDP_PORT_ENVOI);
							sock.send(packet);
						}
					}
				}
			}

		} else {
			DatagramPacket packet = new DatagramPacket(this.message.getBytes(), this.message.length(), this.hote, Configuration.UDP_PORT_ENVOI);
			sock.send(packet);
		}
		sock.close();
		return null;
	}

}
