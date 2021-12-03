package gei.clavardage.controleurs;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import gei.clavardage.modeles.Message;
import gei.clavardage.modeles.ModeleSession;
import gei.clavardage.modeles.Utilisateur;
import gei.clavardage.reseau.services.ServiceCommunicationTCP;
import gei.clavardage.reseau.services.ServiceEnvoiTCP;
import javafx.fxml.Initializable;

public class ControleurSession implements Initializable {

	private ModeleSession modele;
	private ServiceCommunicationTCP reception;
	private ServiceEnvoiTCP envoi;
	
	// TODO bdd
	
	public ControleurSession(Utilisateur util, Socket sock) throws IOException {
		this.modele = new ModeleSession();
		this.reception = new ServiceCommunicationTCP(this, sock);
		this.reception.start();
		this.envoi = new ServiceEnvoiTCP(sock);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void fermeture() {
		
	}
	
	public void fermetureDistante() {
		
	}
	
	public void envoiMessage() {
		
	}
	
	public void receptionMessage(Message msg) {
		
	}

	public UUID getIdentifiant() {
		return modele.getIdentifiant();
	}
	
}
