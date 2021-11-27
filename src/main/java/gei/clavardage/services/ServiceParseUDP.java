package gei.clavardage.services;

import gei.clavardage.controleurs.ControleurUDP;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceParseUDP extends Service<Void> {

	private ControleurUDP udp;
	private String[] message;
	
	public ServiceParseUDP(ControleurUDP udp, String message) {
		this.udp = udp;
		this.message = message.split(" ");
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				if (message.length > 0) {
					switch (message[0]) {
					case "DECONNEXION":
						System.out.println("Deconnexion de "+message[1]);
						break;
					case "UTILISATEUR":
						System.out.println("Nouvel utilisateur "+message[1]+" - "+message[2]);
						break;
					case "VALIDATION":
						System.out.println("Demande de validation de pseudo de "+message[1]+" - "+message[2]);
						break;
					default:
						break;
					}
				}
				return null;
			}
		};
	}

}
