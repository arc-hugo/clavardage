package gei.clavardage.reseau.taches;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import gei.clavardage.concurrent.ExecuteurSession;
import gei.clavardage.modeles.Utilisateur;
import gei.clavardage.reseau.AccesTCP;
import gei.clavardage.reseau.services.ServiceReceptionConnexionTCP;
import javafx.concurrent.Task;

public class TacheConnexionTCP extends Task<Void> {

	private AccesTCP tcp;
	private String adresse;
	private ExecuteurSession executeur;
	
	public TacheConnexionTCP(AccesTCP tcp, Utilisateur destinataire) {
		this.tcp = tcp;
		this.executeur = ExecuteurSession.getInstance();
		this.adresse = destinataire.getAdresse();
	}
	
	@Override
	protected Void call() throws Exception {
		Socket sock = new Socket(adresse, ServiceReceptionConnexionTCP.RECEPTION_PORT);
		BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		
		if (reader.readLine().equals("OK")) {
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
		}
		return null;
	}

}
