package gei.clavardage.controleurs;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import gei.clavardage.concurrent.ExecuteurSession;
import gei.clavardage.modeles.Message;
import gei.clavardage.modeles.ModeleSession;
import gei.clavardage.modeles.Utilisateur;
import gei.clavardage.reseau.services.ServiceReceptionTCP;
import gei.clavardage.reseau.taches.TacheEnvoiTCP;
import javafx.fxml.Initializable;

public class ControleurSession implements Initializable {

	private ModeleSession modele;
	private ServiceReceptionTCP reception;
	private ExecuteurSession executeur;
	private Socket sock;
	
	// TODO AccesBDD
	
	public ControleurSession(Utilisateur local, Utilisateur destinataire, Socket sock) throws IOException {
		this.modele = new ModeleSession(local, destinataire);
		this.executeur = ExecuteurSession.getInstance();
		this.reception = new ServiceReceptionTCP(this, sock);
		this.reception.start();
		this.sock = sock;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	private void fermeture() {
		this.reception.cancel();
	}
	
	public void fermetureLocale() {
		fermeture();
	}
	
	public void fermetureDistante() {
		fermeture();
	}
	
	private void envoiMessage(Message msg) {
		this.executeur.ajoutTache(new TacheEnvoiTCP(sock, msg));
	}
	
	public void receptionMessage(Message msg) {
		
	}

	public UUID getIdentifiant() {
		return modele.getIdentifiant();
	}
	
}
