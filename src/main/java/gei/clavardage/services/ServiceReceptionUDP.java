package gei.clavardage.services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import gei.clavardage.controleurs.ControleurUDP;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class ServiceReceptionUDP extends ScheduledService<Void> {
	
	public final static int RECEPTION_PORT = 22540;
	
	private ControleurUDP controleur;
	
	public ServiceReceptionUDP(ControleurUDP controleur) {
		this.controleur = controleur;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					System.out.println("launch");
					DatagramSocket sock = new DatagramSocket(RECEPTION_PORT);
					byte[] buffer = new byte[256];
					DatagramPacket paquet = new DatagramPacket(buffer, buffer.length);
					
					while (true) {
						sock.receive(paquet);
						String message = new String(paquet.getData(), 0, paquet.getLength());
						ServiceParseUDP parse = new ServiceParseUDP(controleur, message);
						parse.start();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			
		};
	}

}
