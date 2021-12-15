package gei.clavardage.controleurs;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import gei.clavardage.concurrent.ExecuteurSession;
import gei.clavardage.modeles.Fin;
import gei.clavardage.modeles.FinOK;
import gei.clavardage.modeles.Message;
import gei.clavardage.modeles.ModeleSession;
import gei.clavardage.modeles.OK;
import gei.clavardage.modeles.Texte;
import gei.clavardage.modeles.Utilisateur;
import gei.clavardage.reseau.services.ServiceReceptionTCP;
import gei.clavardage.reseau.taches.TacheEnvoiTCP;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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
	@FXML
	private VBox messages;

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
				texte.clear();
			}
		});
		
		this.executeur.ajoutTache(new TacheEnvoiTCP(sock, new OK(getIdentifiant())));
	}

	private void fermeture() {
		this.reception.cancel();
		try {
			this.sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void fermetureLocale() {
		Fin msg = new Fin(getIdentifiant());
		this.executeur.ajoutTache(new TacheEnvoiTCP(sock, msg));
	}
	
	public void confirmerFermeture() {
		fermeture();
	}

	public void fermetureDistante() {
		Alert ferme = new Alert(AlertType.INFORMATION);
		ferme.setTitle("Fin de discussion");
		ferme.setContentText("L'utilisateur "+modele.getDestinataire().getPseudo()+" vient de fermer la discussion.");
		ferme.showAndWait();
		TacheEnvoiTCP envoi = new TacheEnvoiTCP(sock, new FinOK(getIdentifiant()));
		envoi.onSucceededProperty().addListener(e -> {
			fermeture();
		});
		this.executeur.ajoutTache(envoi);
	}

	private void envoiMessage(Message msg) {
		this.executeur.ajoutTache(new TacheEnvoiTCP(sock, msg));
	}

	public void receptionMessage(Message msg) {
		Node noeud = msg.affichage();
		if (noeud != null)
			this.messages.getChildren().add(noeud);
	}

	public UUID getIdentifiant() {
		return this.modele.getIdentifiant();
	}

}
