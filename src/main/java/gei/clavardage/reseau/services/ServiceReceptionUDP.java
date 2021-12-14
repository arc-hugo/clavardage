package gei.clavardage.reseau.services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import gei.clavardage.concurrent.ExecuteurReseau;
import gei.clavardage.reseau.AccesUDP;
import gei.clavardage.reseau.taches.TacheParseUDP;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceReceptionUDP extends Service<Void> {

	public final static int RECEPTION_PORT = 22540;

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
					DatagramSocket sock = new DatagramSocket(RECEPTION_PORT);
					byte[] buffer = new byte[200];
					DatagramPacket paquet = new DatagramPacket(buffer, buffer.length);

					while (true) {
						System.out.println("wait udp");
						sock.receive(paquet);
						String message = new String(paquet.getData(), 0, paquet.getLength());
						System.out.println(message);
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
