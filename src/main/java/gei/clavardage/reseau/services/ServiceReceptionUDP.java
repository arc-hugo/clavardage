package gei.clavardage.reseau.services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import gei.clavardage.reseau.AccesUDP;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceReceptionUDP extends Service<Void> {
	
	public final static int RECEPTION_PORT = 22540;
	
	private AccesUDP udp;
	
	public ServiceReceptionUDP(AccesUDP udp) {
		this.udp = udp;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					@SuppressWarnings("resource")
					DatagramSocket sock = new DatagramSocket(RECEPTION_PORT);
					byte[] buffer = new byte[100];
					DatagramPacket paquet = new DatagramPacket(buffer, buffer.length);
					
					while (true) {
						sock.receive(paquet);
						String message = new String(paquet.getData(), 0, paquet.getLength());
						ServiceParseUDP parse = new ServiceParseUDP(udp, message, paquet.getAddress());
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
