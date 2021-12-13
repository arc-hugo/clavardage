package gei.clavardage.controleurs;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import gei.clavardage.concurrent.ExecuteurSession;
import gei.clavardage.modeles.Message;
import gei.clavardage.modeles.ModeleSession;
import gei.clavardage.modeles.Texte;
import gei.clavardage.modeles.Utilisateur;
import gei.clavardage.reseau.services.ServiceReceptionTCP;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import gei.clavardage.reseau.taches.TacheEnvoiTCP;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ControleurSession implements Initializable {

	private ModeleSession modele;
	private ServiceReceptionTCP reception;
	private ExecuteurSession executeur;
	private Socket sock;

	@FXML
	private Label name;
	@FXML
	private Button envoyer;
	@FXML
	private TextField texte;

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
		Utilisateur destinataire = this.modele.getDestinataire();
		this.name.setText(destinataire.getPseudo());
		
		this.envoyer.setOnAction(e -> new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				String txt = texte.getText();
				envoiMessage(new Texte(modele.getIdentifiant(), txt));
			}
		});
		
	}

	private void fermeture() {
		this.reception.cancel();
	}

	public void fermetureLocale() {
		// TODO
		fermeture();
	}

	public void fermetureDistante() {
		// TODO
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
