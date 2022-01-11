package gei.barralberry.clavardage.controleurs;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import gei.barralberry.clavardage.concurrent.ExecuteurSession;
import gei.barralberry.clavardage.modeles.session.ModeleSession;
import gei.barralberry.clavardage.modeles.utilisateurs.Utilisateur;
import gei.barralberry.clavardage.reseau.messages.Fin;
import gei.barralberry.clavardage.reseau.messages.MessageAffiche;
import gei.barralberry.clavardage.reseau.messages.OK;
import gei.barralberry.clavardage.reseau.messages.Texte;
import gei.barralberry.clavardage.reseau.services.ServiceReceptionTCP;
import gei.barralberry.clavardage.reseau.taches.TacheEnvoiTCP;
import gei.barralberry.clavardage.util.Alerte;
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

	@FXML
	private Label name;
	@FXML
	private Button envoyer;
	@FXML
	private TextField texte;
	@FXML
	private VBox messages;

	private ModeleSession modele;
	private ServiceReceptionTCP reception;
	private ExecuteurSession executeur;

	public ControleurSession(Utilisateur local, Utilisateur destinataire) throws ClassNotFoundException, SQLException, IOException {
		this.modele = new ModeleSession(local, destinataire);
	}

	public ControleurSession(Utilisateur local, Utilisateur destinataire, Socket sock)
			throws IOException, SQLException, ClassNotFoundException {
		this.modele = new ModeleSession(local, destinataire, sock);
		this.executeur = ExecuteurSession.getInstance();
		this.reception = new ServiceReceptionTCP(this, sock);
		this.reception.start();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Utilisateur destinataire = this.modele.getDestinataire();
		this.name.textProperty().bind(destinataire.getPseudoPropery());

		this.envoyer.setOnAction(e -> {
			envoiTexte();
		});

		this.texte.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER && e.isControlDown()) {
				texte.setText(texte.getText() + "\n");
			} else if (e.getCode() == KeyCode.ENTER) {
				envoiTexte();
			}

		});

		this.envoyer.disableProperty().bind(this.modele.getConnecteProperty().not());
		this.texte.disableProperty().bind(this.modele.getConnecteProperty().not());

		if (this.modele.estConnecte()) {
			this.executeur.ajoutTache(new TacheEnvoiTCP(this.modele.getSocket(), new OK(getIdentifiantLocal())));
		}

		try {
			List<MessageAffiche> hist = this.modele.getDerniersMessages();
			for (MessageAffiche oldMsg : hist) {
				this.messages.getChildren().add(oldMsg.affichage());
			}
		} catch (SQLException e1) {
			Alerte ex = Alerte.exceptionLevee(e1);
			ex.showAndWait();
		}
		
		this.texte.requestFocus();
	}

	private void envoiTexte() {
		String txt = texte.getText();
		if (!txt.equals("")) {
			envoiMessage(new Texte(modele.getIdentifiantLocal(), txt));
			texte.clear();
		}
	}

	private void fermeture() {
		this.reception.cancel();
	}

	private void envoiMessage(MessageAffiche msg) {
		if (this.modele.estConnecte()) {
			this.modele.ajoutEnvoi(msg);
			this.executeur.ajoutTache(new TacheEnvoiTCP(this.modele.getSocket(), msg));
		}
	}

	public void fermetureLocale() {
		if (this.modele.estConnecte()) {
			Fin msg = new Fin(getIdentifiantLocal());
			TacheEnvoiTCP envoi = new TacheEnvoiTCP(this.modele.getSocket(), msg);
			envoi.setOnSucceeded(e -> {
				fermeture();
			});
			this.executeur.ajoutTache(envoi);
			try {
				this.modele.fermetureDB();
			} catch (SQLException e1) {
				Alerte ex = Alerte.exceptionLevee(e1);
				ex.show();
			}
		}
	}

	public void confirmerFermeture() {
		fermeture();
	}

	public void fermetureDistante() {
		// TODO passage en mode fin de session
		Platform.runLater(new Runnable() {
			public void run() {
				Alerte ferme = Alerte.fermetureSession(modele.getDestinataire().getPseudo());
				ferme.show();
			}
		});
		this.modele.fermetureDistante();
		try {
			this.modele.fermetureDB();
		} catch (SQLException e1) {
			Platform.runLater(new Runnable() {
				public void run() {
					Alerte ex = Alerte.exceptionLevee(e1);
					ex.show();
				}
			});
		}
		fermeture();
	}

	public void receptionMessage(MessageAffiche msg) {
		Node noeud = msg.affichage();
		if (noeud != null) {
			try {
				modele.enregistrerReception(msg);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						messages.getChildren().add(noeud);
					}
				});
			} catch (SQLException e) {
				Alerte ex = Alerte.exceptionLevee(e);
				ex.show();
			}
		}
	}

	public UUID getIdentifiantLocal() {
		return this.modele.getIdentifiantLocal();
	}

	public Utilisateur getDestinataire() {
		return this.modele.getDestinataire();
	}

	public void envoiRecu() {
		MessageAffiche msg = modele.envoiTermine();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				messages.getChildren().add(msg.affichage());
			}
		});
	}
}
