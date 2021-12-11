package gei.clavardage.controleurs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.*;

import gei.clavardage.App;
import gei.clavardage.modeles.ModeleUtilisateurs;
import gei.clavardage.modeles.Utilisateur;
import gei.clavardage.reseau.AccesTCP;
import gei.clavardage.reseau.AccesUDP;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ControleurUtilisateurs implements Initializable {

	@FXML
	private TabPane tabs;
	@FXML
	private ListView<Utilisateur> list;
	@FXML
	private MenuItem deconnexion;
	@FXML
	private MenuItem changerPseudo;

	private ModeleUtilisateurs modele;
	private AccesUDP udp;
	private AccesTCP tcp;

	public ControleurUtilisateurs() {
		this.modele = new ModeleUtilisateurs();
		this.udp = new AccesUDP(this);
		this.tcp = new AccesTCP(this);
	}

	public UUID getIdentifiantLocal() {
		return modele.getUtilisateurLocal().getIdentifiant();
	}

	public String getPseudoLocal() {
		return modele.getUtilisateurLocal().getPseudo();
	}

	@FXML
	public void saisiePseudo() {
		FXMLLoader loader = new FXMLLoader(App.class.getResource("saisiePseudo.fxml"));
		loader.setController(new ControleurPseudo());
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Saisie de pseudo");
		try {
			stage.setScene((Scene) loader.load());
			String login = "";
			while (login.equals("")) {
				stage.showAndWait();
				ControleurPseudo pseudo = loader.getController();
				login = pseudo.getTxt();
			}

			udp.broadcastValidation(getIdentifiantLocal(), login);
			modele.setPseudoLocal(login);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void creationSession(Utilisateur util, Socket sock) throws IOException {
		util.setEnSession(true);
		ControleurSession session = new ControleurSession(modele.getUtilisateurLocal(), util, sock);
		FXMLLoader loader = new FXMLLoader(App.class.getResource("session.fxml"));
		loader.setController(session);

		Tab tab = (Tab) loader.load();
		tab.setOnClosed(e -> {
			session.fermetureLocale();
		});
		this.tabs.getTabs().add(tab);
	}

	public void lancementSession(Utilisateur destinataire) {
		tcp.demandeConnexion(destinataire);
	}

	public void lancementAccepte(Socket sock) throws IOException {
		Utilisateur util = this.modele.getUtilisateurWithAdresse(sock.getInetAddress().getHostAddress());
		if (util != null) {
			creationSession(util, sock);
		} else {
			sock.close();
		}
	}

	public void deconnexion() {
		Alert deco = new Alert(AlertType.CONFIRMATION);
		deco.setTitle("Déconnexion");
		deco.setHeaderText("Vous vous apprêtez à vous déconnecter de l'application.");
		deco.setContentText("Êtes-vous sûr de vouloir continuer ?");

		Optional<ButtonType> opt = deco.showAndWait();
		if (opt.get() == ButtonType.OK) {
			udp.broadcastDeconnexion(modele.getUtilisateurLocal());
			Platform.exit();
			System.exit(0);
		}
	}

	public void demandeSession(Socket sock) throws IOException {
		Utilisateur util = this.modele.getUtilisateurWithAdresse(sock.getInetAddress().getHostAddress());

		if (util != null) {
			Alert confirm = new Alert(AlertType.CONFIRMATION);
			confirm.setTitle("Demande de lancement de session de " + util.getPseudo());
			confirm.setHeaderText(util.getPseudo() + " souhaite lancer une session de discussion avec vous !");
			confirm.setContentText("Acceptez-vous cette demande ?");

			PrintWriter conn = new PrintWriter(sock.getOutputStream());
			Optional<ButtonType> result = confirm.showAndWait();
			if (result.get() == ButtonType.OK) {
				conn.println("OK");
				creationSession(util, sock);
			} else {
				conn.println();
				conn.close();
				sock.close();
			}
		} else {
			// TODO if util == null -> demande UDP de renvoi utilisateur
			sock.close();
		}
	}

	public void receptionUtilisateur(UUID identifiant, InetAddress adresse, String pseudo) {
		modele.connexion(identifiant, adresse, pseudo);
	}

	public void deconnexionDistante(UUID identifiant) {
		modele.deconnexion(identifiant);
		modele.setEnSession(identifiant, false);
	}

	public boolean validationDistante(String pseudo) {
		return !(modele.getPseudoLocal().trim().toLowerCase().equals(pseudo.trim().toLowerCase()));
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.list.setItems(this.modele.getUtilisateurs());

		PseudoClass inactive = PseudoClass.getPseudoClass("inactive");
		this.list.setCellFactory(cell -> new ListCell<Utilisateur>() {
			protected void updateItem(Utilisateur item, boolean empty) {
				if (empty) {
					setText(null);
					pseudoClassStateChanged(inactive, true);
				} else {
					setText(item.getPseudo());
					if (item.isActif()) {
						pseudoClassStateChanged(inactive, false);
					} else {
						pseudoClassStateChanged(inactive, true);
					}
				}
			}
		});

		this.list.setOnMouseClicked(e -> {
			Utilisateur util = list.getSelectionModel().getSelectedItem();
			if (!util.isEnSession()) {
				lancementSession(util);
			}
		});

		this.changerPseudo.setOnAction(e -> {
			saisiePseudo();
		});

		this.deconnexion.setOnAction(e -> {
			deconnexion();
		});

		saisiePseudo();
	}

}
