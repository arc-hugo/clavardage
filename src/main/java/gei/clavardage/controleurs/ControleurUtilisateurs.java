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
import javafx.css.PseudoClass;
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
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setTitle("Saisie de pseudo");
		try {
			stage.setScene(new Scene(loader.load()));
			String login = "";
			while (login .equals("")) {
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
		this.tabs.getTabs().add((Tab) loader.load());
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
	
	public void deconnexion () {
		udp.broadcastDeconnexion(modele.getUtilisateurLocal());
	}
	
	public void demandeSession(Socket sock) throws IOException {
		Utilisateur util = this.modele.getUtilisateurWithAdresse(sock.getInetAddress().getHostAddress());
		
		if (util != null) {
			Alert confirm = new Alert(AlertType.CONFIRMATION);
			confirm.setTitle("Demande de lancement de session de "+util.getPseudo());
			confirm.setHeaderText(util.getPseudo()+" souhaite lancer une session de discussion avec vous !");
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
		return !(modele.getPseudoLocal().trim().equals(pseudo.trim()));
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
		
		this.list.setOnMouseClicked(e -> new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Utilisateur util = list.getSelectionModel().getSelectedItem();
				if (!util.isEnSession()) {
					lancementSession(util);
				}
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
