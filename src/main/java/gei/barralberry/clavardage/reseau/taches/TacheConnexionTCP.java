package gei.barralberry.clavardage.reseau.taches;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import gei.barralberry.clavardage.concurrent.ExecuteurSession;
import gei.barralberry.clavardage.modeles.utilisateurs.Utilisateur;
import gei.barralberry.clavardage.reseau.AccesTCP;
import gei.barralberry.clavardage.util.Alerte;
import gei.barralberry.clavardage.util.Configuration;
import javafx.application.Platform;
import javafx.concurrent.Task;

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
			Socket sock = new Socket(destinataire.getAdresse(), Configuration.TCP_PORT_ENVOI);
			BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			String line = reader.readLine();
			this.executeur.ajoutTache(new Runnable() {
				@Override
				public void run() {
					if (line.equals("OK")) {
						tcp.connexionAccepte(sock);
					} else {
						tcp.connexionRefuse(sock);
					}
				}
			});
		} catch (UnknownHostException e) {
			Platform.runLater(new Runnable() {
				public void run() {
					Alerte refus = Alerte.utilisateurDeconnecte(destinataire.getPseudo());
					refus.show();
				}
			});
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
