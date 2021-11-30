package gei.clavardage.services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import gei.clavardage.controleurs.ControleurUDP;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class ServiceReceptionUDP extends ScheduledService<Void> {
	
	public final static int RECEPTION_PORT = 22540;
	
	private ControleurUDP udp;
	
	public ServiceReceptionUDP(ControleurUDP udp) {
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
					byte[] buffer = new byte[256];
					DatagramPacket paquet = new DatagramPacket(buffer, buffer.length);
					
					while (true) {
						sock.receive(paquet);
						String message = new String(paquet.getData(), 0, paquet.getLength());
						ServiceParseUDP parse = new ServiceParseUDP(udp, message, paquet.getAddress().toString());
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
