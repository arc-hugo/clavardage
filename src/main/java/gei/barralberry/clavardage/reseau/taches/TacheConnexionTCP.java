package gei.barralberry.clavardage.reseau.taches;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import gei.barralberry.clavardage.concurrent.ExecuteurSession;
import gei.barralberry.clavardage.modeles.utilisateurs.Utilisateur;
import gei.barralberry.clavardage.reseau.AccesTCP;
import gei.barralberry.clavardage.util.Alerte;
import gei.barralberry.clavardage.util.Configuration;
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
			if (reader.readLine().equals("OK")) {
				this.executeur.ajoutTache(new Runnable() {
					@Override
					public void run() {
						try {
							tcp.connexionAccepte(sock);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			} else {
				this.executeur.ajoutTache(new Runnable() {
					@Override
					public void run() {
						tcp.connexionRefuse(sock);
					}
				});
			}
		} catch (UnknownHostException e) {
			Alerte refus = Alerte.utilisateurDeconnecte(destinataire.getPseudo());
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
