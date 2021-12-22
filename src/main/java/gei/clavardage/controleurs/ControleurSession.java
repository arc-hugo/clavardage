package gei.clavardage.controleurs;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import gei.clavardage.concurrent.ExecuteurSession;
import gei.clavardage.modeles.messages.Fin;
import gei.clavardage.modeles.messages.FinOK;
import gei.clavardage.modeles.messages.MessageAffiche;
import gei.clavardage.modeles.messages.OK;
import gei.clavardage.modeles.messages.Texte;
import gei.clavardage.modeles.session.ModeleSession;
import gei.clavardage.modeles.utilisateurs.Utilisateur;
import gei.clavardage.reseau.services.ServiceReceptionTCP;
import gei.clavardage.reseau.taches.TacheEnvoiTCP;
import gei.clavardage.utils.Alerte;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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
		this.name.textProperty().bind(destinataire.getPseudoPropery());
		
		this.envoyer.setOnAction(e ->  {
			envoiTexte();
		});
		
		this.texte.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER && e.isControlDown()) {
				texte.setText(texte.getText()+"\n");
			} else if (e.getCode() == KeyCode.ENTER) {
				envoiTexte();
			}
			
		});
		
		this.executeur.ajoutTache(new TacheEnvoiTCP(sock, new OK(getIdentifiantLocal())));
	}

	private void envoiTexte() {
		String txt = texte.getText();
		if (!txt.equals("")) {
			envoiMessage(new Texte(modele.getIdentifiantLocal(), txt));
			texte.clear();
		}
	}
	
	private void fermeture() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				reception.cancel();
			}
		});
	}

	private void envoiMessage(MessageAffiche msg) {
		this.modele.ajoutEnvoi(msg);
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
		// TODO passage en mode lecture d'historique
		Alerte ferme = Alerte.fermetureSession(modele.getDestinataire().getPseudo());
		ferme.showAndWait();
		TacheEnvoiTCP envoi = new TacheEnvoiTCP(sock, new FinOK(getIdentifiantLocal()));
		envoi.setOnSucceeded(e -> {
			fermeture();
		});
		this.executeur.ajoutTache(envoi);
	}

	public void receptionMessage(MessageAffiche msg) {
		Node noeud = msg.affichage();
		if (noeud != null) {	
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					messages.getChildren().add(noeud);
				}
			});
		}
	}

	public UUID getIdentifiantLocal() {
		return this.modele.getIdentifiantLocal();
	}
	
	public Utilisateur getDestinataire() {
		return this.modele.getDestinataire();
	}

	public void envoiRecu() {
		MessageAffiche msg = this.modele.envoiTermine();
		if (msg != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					messages.getChildren().add(msg.affichage());
				}
			});
		}
	}
}
