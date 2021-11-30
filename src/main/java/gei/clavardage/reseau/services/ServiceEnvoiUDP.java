package gei.clavardage.reseau.services;

import java.net.DatagramSocket;

import gei.clavardage.modeles.Paquet;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceEnvoiUDP extends Service<Void> {
	
	private Paquet paquet;
	
	public ServiceEnvoiUDP(Paquet paquet) {
		this.paquet = paquet;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			
			@Override
			protected Void call() throws Exception {
				if (paquet != null) {
					DatagramSocket sock = new DatagramSocket();
					sock.send(paquet.getPaquet());
					sock.close();
				}
				return null;
			}
		};
	}

}
