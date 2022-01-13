package gei.barralberry.clavardage.controleurs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import gei.barralberry.clavardage.concurrent.ExecuteurSession;
import gei.barralberry.clavardage.modeles.session.ModeleSession;
import gei.barralberry.clavardage.modeles.utilisateurs.EtatUtilisateur;
import gei.barralberry.clavardage.modeles.utilisateurs.Utilisateur;
import gei.barralberry.clavardage.reseau.messages.Fichier;
import gei.barralberry.clavardage.reseau.messages.Fin;
import gei.barralberry.clavardage.reseau.messages.MessageAffiche;
import gei.barralberry.clavardage.reseau.messages.OK;
import gei.barralberry.clavardage.reseau.messages.Texte;
import gei.barralberry.clavardage.reseau.services.ServiceReceptionTCP;
import gei.barralberry.clavardage.reseau.taches.TacheEnvoiTCP;
import gei.barralberry.clavardage.util.Alerte;
import gei.barralberry.clavardage.util.Configuration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;

public class ControleurSession implements Initializable {

	@FXML private Label name;
	@FXML private Button envoyer;
	@FXML private Button fichier;
	@FXML private TextField texte;
	@FXML private VBox messages;
  @FXML private ScrollPane scroll;

	private ModeleSession modele;
	private ServiceReceptionTCP reception;
	private ExecuteurSession executeur;
	private File dossierSession;

	public ControleurSession(Utilisateur local, Utilisateur destinataire) throws ClassNotFoundException, SQLException, IOException {
		this.modele = new ModeleSession(local, destinataire);
	}

	public ControleurSession(Utilisateur local, Utilisateur destinataire, Socket sock)
			throws IOException, SQLException, ClassNotFoundException {
		this.modele = new ModeleSession(local, destinataire, sock);
		this.executeur = ExecuteurSession.getInstance();
		this.reception = new ServiceReceptionTCP(this, sock);
		this.dossierSession = new File(Configuration.DOSSIER_CACHE+"/"+destinataire.getIdentifiant().toString());
		this.dossierSession.mkdirs();
		this.reception.start();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.name.textProperty().bind(this.modele.getDestinataire().getPseudoPropery());

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
		
		this.fichier.setOnAction(e -> {
			FileChooser choix = new FileChooser();
			choix.setTitle("Séléction d'un fichier à envoyer");
			File fichier = choix.showOpenDialog(this.texte.getScene().getWindow());
			if (fichier != null) {
				this.executeur.ajoutTache(new Runnable() {
					public void run() {
						envoiFichier(fichier);
					}
				});
			}
		});

		this.envoyer.disableProperty().bind(this.modele.getConnecteProperty().not());
		this.texte.disableProperty().bind(this.modele.getConnecteProperty().not());
		this.fichier.disableProperty().bind(this.modele.getConnecteProperty().not());

		this.texte.requestFocus();
		
		if (this.modele.estConnecte()) {
			this.executeur.ajoutTache(new TacheEnvoiTCP(this.modele.getSocket(), new OK(getIdentifiantLocal())));
		}

		try {
			List<MessageAffiche> hist = this.modele.getDerniersMessages();
			for (MessageAffiche oldMsg : hist) {
				VBox noeud = oldMsg.affichage();
				Label msg = (Label) noeud.getChildren().get(1);
				if (oldMsg.getAuteur().equals(this.modele.getIdentifiantLocal())) {
					String envoi = "-fx-background-color: red; -fx-text-fill: #f9f9f9; -fx-background-radius: 10; -fx-border-radius: 10; -fx-padding: 2 7; -fx-alignment: CENTER-RIGHT";
					msg.setTextAlignment(TextAlignment.RIGHT);
					msg.setStyle(envoi);
				} else {
					String recep = "-fx-background-color: #f9f9f9; -fx-border-color: #bbbbbb; -fx-text-fill: red; -fx-background-radius: 10; -fx-border-radius: 10; -fx-padding: 2 7;";
					msg.setStyle(recep);
				}
				String denvoi = "-fx-font-size: 10";
				Label date = (Label) noeud.getChildren().get(0);
				date.setStyle(denvoi);
				this.messages.getChildren().add(noeud);
				noeud.prefWidthProperty().bind(scroll.widthProperty());
				//noeud.prefHeightProperty().bind(scroll.heightProperty());
				msg.setAlignment(Pos.CENTER_RIGHT);
				date.setAlignment(Pos.CENTER);
			}
		} catch (SQLException e1) {
			Alerte ex = Alerte.exceptionLevee(e1);
			ex.showAndWait();
		}
	}

	private void envoiTexte() {
		String txt = texte.getText();
		if (!txt.equals("")) {
			envoiMessage(new Texte(modele.getIdentifiantLocal(), txt));
			texte.clear();
		}
	}
	
	private void envoiFichier(File fichier) {
		try {
			String nom = fichier.getName();
			
			// Récupération de l'extension du fichier
			int extPos = nom.lastIndexOf('.');
			String extension;
			if (extPos != -1) {
				extension = nom.substring(extPos);
				nom = nom.substring(0,extPos);
			} else {
				extension = "";
			}
			
			File cache = new File(this.dossierSession.getAbsolutePath() + "/" + nom + extension);
			int i = 1;
			while (cache.exists()) {
				cache = new File(this.dossierSession.getAbsolutePath() + "/" + String.format("%s(%d)", nom, i) + extension);
				i++;
			}
			Files.copy(new FileInputStream(fichier), cache.toPath());
			envoiMessage(new Fichier(getIdentifiantLocal(), cache));
		} catch (IOException e) {
			Alerte ex = Alerte.exceptionLevee(e);
			ex.show();
		}
	}

	private void fermeture() {
		Platform.runLater(new Runnable() {
			public void run() {
				reception.cancel();
			}
		});
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
				if (modele.getDestinataire().getEtat() != EtatUtilisateur.DECONNECTE) {
					modele.getDestinataire().setEtat(EtatUtilisateur.CONNECTE);
				}
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
	
	public File getDossierSession() {
		return this.dossierSession;
	}

	public void envoiRecu() {
		MessageAffiche msg = this.modele.envoiTermine();
		if (msg != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					Node aff = msg.affichage();
					messages.getChildren().add(aff);
				}
			});
		}
	}
}
