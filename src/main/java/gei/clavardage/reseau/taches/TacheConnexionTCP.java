package gei.clavardage.reseau.taches;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import gei.clavardage.concurrent.ExecuteurSession;
import gei.clavardage.modeles.utilisateurs.Utilisateur;
import gei.clavardage.reseau.AccesTCP;
import gei.clavardage.reseau.services.ServiceReceptionConnexionTCP;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class TacheConnexionTCP extends Task<Void> {

	private AccesTCP tcp;
	private Utilisateur destinataire;
	private ExecuteurSession executeur;
	
	public TacheConnexionTCP(AccesTCP tcp, Utilisateur destinataire) {
		this.tcp = tcp;
		this.executeur = ExecuteurSession.getInstance();
		this.destinataire = destinataire;
	}
	
	@Override
	protected Void call() throws Exception {
		try {
			Socket sock = new Socket(destinataire.getAdresse(), ServiceReceptionConnexionTCP.RECEPTION_PORT);
			BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("Read ok");

			String line = reader.readLine();
			System.out.println(line);
			if (line.equals("OK")) {
				this.executeur.ajoutTache(new Runnable() {
					@Override
					public void run() {
						try {
							tcp.connexionAccepte(sock);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			} else {
				Alert refus = new Alert(AlertType.INFORMATION);
				refus.setTitle("Refus");
				refus.setContentText("L'utilisateur "+destinataire.getPseudo()+" à refusé la demande de discussion");
				refus.show();
			}
		} catch (UnknownHostException e) {
			Alert refus = new Alert(AlertType.INFORMATION);
			refus.setTitle("Deconnecté");
			refus.setContentText("L'utilisateur "+destinataire.getPseudo()+" est deconnecté");
			refus.show();
			this.executeur.ajoutTache(new Runnable() {
				@Override
				public void run() {
					tcp.utilisateurDeconnecte(destinataire);
				}
			});
		}
		return null;
	}

}
