package gei.barralberry.clavardage.reseau.services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import gei.barralberry.clavardage.App;
import gei.barralberry.clavardage.concurrent.ExecuteurReseau;
import gei.barralberry.clavardage.reseau.AccesUDP;
import gei.barralberry.clavardage.reseau.taches.TacheParseUDP;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceReceptionUDP extends Service<Void> {
	
	private AccesUDP udp;
	private ExecuteurReseau executeur;

	public ServiceReceptionUDP(AccesUDP udp) {
		this.udp = udp;
		this.executeur = ExecuteurReseau.getInstance();
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					@SuppressWarnings("resource")
					DatagramSocket sock = new DatagramSocket(App.UDP_PORT_RECEPTION);
					byte[] buffer = new byte[200];
					DatagramPacket paquet = new DatagramPacket(buffer, buffer.length);

					while (true) {
						sock.receive(paquet);
						String message = new String(paquet.getData(), 0, paquet.getLength());
						executeur.ajoutTache(new TacheParseUDP(udp, message, paquet.getAddress()));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

		};
	}

}
