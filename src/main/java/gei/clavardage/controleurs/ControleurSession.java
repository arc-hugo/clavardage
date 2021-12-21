package gei.clavardage.controleurs;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import gei.clavardage.concurrent.ExecuteurSession;
import gei.clavardage.modeles.messages.Fin;
import gei.clavardage.modeles.messages.Message;
import gei.clavardage.modeles.messages.OK;
import gei.clavardage.modeles.messages.Texte;
import gei.clavardage.modeles.session.ModeleSession;
import gei.clavardage.modeles.utilisateurs.Utilisateur;
import gei.clavardage.reseau.services.ServiceReceptionTCP;
import gei.clavardage.reseau.taches.TacheEnvoiTCP;
import javafx.application.Platform;
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

	@FXML private Label name;
	@FXML private Button envoyer;
	@FXML private TextField texte;
	@FXML private VBox messages;
	
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
		Utilisateur destinataire = this.modele.getDestinataire();
		this.name.setText(destinataire.getPseudo());
		
		this.envoyer.setOnAction(e ->  {
				String txt = texte.getText();
				envoiMessage(new Texte(modele.getIdentifiantLocal(), txt));
				texte.clear();
		});
		
		this.executeur.ajoutTache(new TacheEnvoiTCP(sock, new OK(getIdentifiantLocal())));
	}

	private void fermeture() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				reception.cancel();
				try {
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void envoiMessage(Message msg) {
		this.executeur.ajoutTache(new TacheEnvoiTCP(sock, msg));
	}

	public void fermetureLocale() {
		Fin msg = new Fin(getIdentifiantLocal());
		TacheEnvoiTCP envoi = new TacheEnvoiTCP(sock, msg);
		envoi.setOnSucceeded(e -> {
			fermeture();
		});
		this.executeur.ajoutTache(envoi);
	}
	
	public void confirmerFermeture() {
		fermeture();
	}

	public void fermetureDistante() {
		Alert ferme = new Alert(AlertType.INFORMATION);
		ferme.setTitle("Fin de discussion");
		ferme.setContentText("L'utilisateur "+modele.getDestinataire().getPseudo()+" vient de fermer la discussion.");
		ferme.showAndWait();
		fermeture();
	}

	public void receptionMessage(Message msg) {
		Node noeud = msg.affichage();
		if (noeud != null)
			this.messages.getChildren().add(noeud);
	}

	public UUID getIdentifiantLocal() {
		return this.modele.getIdentifiantLocal();
	}
	
	public Utilisateur getDestinataire() {
		return this.modele.getDestinataire();
	}
}
