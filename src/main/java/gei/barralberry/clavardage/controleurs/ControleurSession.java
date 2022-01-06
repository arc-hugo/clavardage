package gei.barralberry.clavardage.controleurs;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import gei.barralberry.clavardage.concurrent.ExecuteurSession;
import gei.barralberry.clavardage.donnees.AccesDB;
import gei.barralberry.clavardage.modeles.session.ModeleSession;
import gei.barralberry.clavardage.modeles.session.SessionMode;
import gei.barralberry.clavardage.modeles.utilisateurs.Utilisateur;
import gei.barralberry.clavardage.reseau.messages.Fin;
import gei.barralberry.clavardage.reseau.messages.FinOK;
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
	
	private final static int HIST_SIZE = 30;

	@FXML private Label name;
	@FXML private Button envoyer;
	@FXML private TextField texte;
	@FXML private VBox messages;
	
	private SessionMode mode;
	private ModeleSession modele;
	private AccesDB db;
	private ServiceReceptionTCP reception;
	private ExecuteurSession executeur;
	private Socket sock;

	public ControleurSession(Utilisateur local, Utilisateur destinataire) throws IOException, SQLException, ClassNotFoundException {
		this.mode = SessionMode.HISTORIQUE;
		this.modele = new ModeleSession(local, destinataire);
		this.db = new AccesDB(destinataire.getIdentifiant(), local.getIdentifiant());
	} 
	
	public ControleurSession(Utilisateur local, Utilisateur destinataire, Socket sock) throws IOException, SQLException, ClassNotFoundException {
		this.mode = SessionMode.CONNECTE;
		this.modele = new ModeleSession(local, destinataire);
		this.db = new AccesDB(destinataire.getIdentifiant(), local.getIdentifiant());
		this.executeur = ExecuteurSession.getInstance();
		this.reception = new ServiceReceptionTCP(this, sock);
		this.reception.start();
		this.sock = sock;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Utilisateur destinataire = this.modele.getDestinataire();
		this.name.textProperty().bind(destinataire.getPseudoPropery());
		
		if (this.mode == SessionMode.CONNECTE) {
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
		} else {
			this.envoyer.setDisable(true);
			this.texte.setDisable(true);
		}
		
		
		try {
			List<MessageAffiche> hist = this.db.getDerniersMessages(HIST_SIZE);
			for (MessageAffiche oldMsg : hist) {
				this.messages.getChildren().add(oldMsg.affichage());
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		if (this.mode == SessionMode.CONNECTE) {
			this.modele.ajoutEnvoi(msg);
			this.executeur.ajoutTache(new TacheEnvoiTCP(sock, msg));
		}
	}

	public void fermetureLocale() {
		Fin msg = new Fin(getIdentifiantLocal());
		TacheEnvoiTCP envoi = new TacheEnvoiTCP(sock, msg);
		envoi.setOnSucceeded(e -> {
			try {
				db.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			fermeture();
		});
		this.executeur.ajoutTache(envoi);
	}
	
	public void confirmerFermeture() {
		fermeture();
	}

	public void fermetureDistante() {
		// TODO passage en mode lecture d'historique
		this.mode = SessionMode.FIN;
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
					try {
						db.ajoutMessage(msg);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
					try {
						db.ajoutMessage(msg);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					messages.getChildren().add(msg.affichage());
				}
			});
		}
	}
}
