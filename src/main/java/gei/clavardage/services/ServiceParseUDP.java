package gei.clavardage.services;

import java.util.UUID;

import gei.clavardage.controleurs.ControleurUDP;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceParseUDP extends Service<Void> {

	private ControleurUDP udp;
	private String type;
	private UUID uuid;
	private String pseudo;
	private String adresse;

	public ServiceParseUDP(ControleurUDP udp, String message, String adresse) {
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
					System.out.println("Déconnexion de " + uuid);
					deconnexion();
					break;
				case "UTILISATEUR":
					System.out.println("Nouvel utilisateur " + uuid + " - " + pseudo);
					utilisateur();
					break;
				case "VALIDATION":
					System.out.println("Demande de validation de pseudo de " + uuid + " - " + pseudo);
					validation();
					break;
				case "INVALIDE":
					System.out.println("Pseudo choisi invalide !");
					invalide();
				default:
					break;
				}
				return null;
			}
		};
	}

}
