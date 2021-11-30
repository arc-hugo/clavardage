package gei.clavardage.reseau.services;

import java.util.UUID;

import gei.clavardage.reseau.AccesUDP;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceParseUDP extends Service<Void> {

	private AccesUDP udp;
	private String type;
	private UUID uuid;
	private String pseudo;
	private String adresse;

	public ServiceParseUDP(AccesUDP udp, String message, String adresse) {
		this.udp = udp;
		
		String[] split = message.split(" ");
		this.type = split[0];
		this.uuid = UUID.fromString(split[1]);
		this.pseudo = split[2];
		for (int i = 3; i < split.length; i++) {
			this.pseudo = this.pseudo+" "+split[i];
		}		
		this.adresse = adresse;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			private void deconnexion() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						udp.deconnexionDistante(uuid);
					}
				});
			}

			private void utilisateur() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						udp.receptionUtilisateur(uuid, adresse, pseudo);
					}
				});
			}

			private void validation() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						udp.validationUtilisateur(uuid, adresse, pseudo);
					}
				});
			}

			private void invalide() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						udp.pseudoLocalInvalide();
					}
				});
			}

			@Override
			protected Void call() throws Exception {
				switch (type) {
				case "DECONNEXION":
					deconnexion();
					break;
				case "UTILISATEUR":
					utilisateur();
					break;
				case "VALIDATION":
					validation();
					break;
				case "INVALIDE":
					invalide();
				default:
					break;
				}
				return null;
			}
		};
	}

}
