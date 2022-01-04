package gei.barralberry.clavardage.controleurs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import gei.barralberry.clavardage.App;
import gei.barralberry.clavardage.modeles.utilisateurs.EtatUtilisateur;
import gei.barralberry.clavardage.modeles.utilisateurs.ModeleUtilisateurs;
import gei.barralberry.clavardage.modeles.utilisateurs.Utilisateur;
import gei.barralberry.clavardage.reseau.AccesTCP;
import gei.barralberry.clavardage.reseau.AccesUDP;
import gei.barralberry.clavardage.reseau.messages.Fin;
import gei.barralberry.clavardage.reseau.messages.OK;
import gei.barralberry.clavardage.utils.Alerte;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ControleurUtilisateurs implements Initializable {

	@FXML private TabPane tabs;
	@FXML private ListView<Utilisateur> list;
	@FXML private MenuItem deconnexion;
	@FXML private MenuItem changerPseudo;

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

	public void saisiePseudo() {
		if (!ControleurPseudo.isActif()) {
			FXMLLoader loader = new FXMLLoader(App.class.getResource("saisiePseudo.fxml"));
			loader.setController(new ControleurPseudo());
			Stage stage = new Stage();
			stage.initStyle(StageStyle.DECORATED);
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
	}

	private void creationSession(Utilisateur util, Socket sock) throws IOException, SQLException {
		this.modele.setEtat(util.getIdentifiant(), EtatUtilisateur.EN_SESSION);
		ControleurSession session = new ControleurSession(modele.getUtilisateurLocal(), util, sock);
		FXMLLoader loader = new FXMLLoader(App.class.getResource("session.fxml"));
		loader.setController(session);

		Tab tab = new Tab(util.getPseudo(), loader.load());
		tab.textProperty().bind(util.getPseudoPropery());
		tab.setOnClosed(e -> {
			session.fermetureLocale();
			if (this.modele.getEtat(util.getIdentifiant()) != EtatUtilisateur.DECONNECTE) {
				this.modele.setEtat(util.getIdentifiant(), EtatUtilisateur.CONNECTE);
			}
		});
		tab.setUserData(session);
		this.tabs.getTabs().add(tab);
	}

	public void lancementSession(Utilisateur destinataire) {
		if (destinataire.getEtat() == EtatUtilisateur.CONNECTE) {
				destinataire.setEtat(EtatUtilisateur.EN_ATTENTE);
				tcp.demandeConnexion(destinataire);
		} else if (destinataire.getEtat() == EtatUtilisateur.DECONNECTE) {
			Alerte refus = Alerte.utilisateurDeconnecte(destinataire.getPseudo());
			refus.show();
		}
	}

	public void lancementAccepte(Socket sock) throws IOException {
		Utilisateur util = this.modele.getUtilisateurWithAdresse(sock.getInetAddress());
		if (util != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					try {
						creationSession(util, sock);
					} catch (IOException | SQLException e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			sock.close();
		}
	}
	
	public void deconnexion() {
		Alerte deco = Alerte.confirmationDeconnexion();

		Optional<ButtonType> opt = deco.showAndWait();
		if (opt.get() == ButtonType.OK) {
			udp.broadcastDeconnexion(modele.getUtilisateurLocal());
			this.tabs.getTabs().clear();
			Platform.exit();
			System.exit(0);
		}
	}
	
	private void accepterConnexion(Utilisateur util, Socket sock) throws IOException, SQLException {
		Alerte confirm = Alerte.accepterConnexion(util.getPseudo());
		
		Optional<ButtonType> result = confirm.showAndWait();
		if (result.get() == ButtonType.OK) {
			OK ok = new OK(getIdentifiantLocal());
			ok.envoie(sock);
			creationSession(util, sock);
		} else {
			Fin fin = new Fin(getIdentifiantLocal());
			fin.envoie(sock);
			sock.close();
		}
	}

	public void demandeSession(Socket sock) throws IOException {
		Utilisateur util = this.modele.getUtilisateurWithAdresse(sock.getInetAddress());
		if (util != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					try {
						accepterConnexion(util, sock);
					} catch (IOException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} else {
			// TODO if util == null -> demande TCP de renvoi utilisateur
			sock.close();
		}
	}

	public void receptionUtilisateur(UUID identifiant, InetAddress adresse, String pseudo) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				modele.connexion(identifiant, adresse, pseudo);
			}
		});
	}

	public void deconnexionDistante(UUID identifiant) {
		if (this.modele.getEtat(identifiant) == EtatUtilisateur.EN_SESSION) {
			String pseudo = this.modele.getPseudo(identifiant);
			for (Tab tab : this.tabs.getTabs()) {
				if (tab.getText().equals(pseudo)) {
					ControleurSession session = (ControleurSession) tab.getUserData();
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							session.fermetureDistante();
						}
					});
				}
			}
		}
		this.modele.setEtat(identifiant, EtatUtilisateur.DECONNECTE);
	}

	public boolean validationDistante(UUID uuid, String pseudo) {
		if (!(this.modele.getPseudoLocal().trim().equals(pseudo.trim()))) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					modele.setPseudo(uuid, pseudo);
				}
			});
			return true;
		}
		return false;
	}
	
	@FXML 
	private void ferme() {
		deconnexion();
	}
	
	@FXML 
	private void diminue() {
		Stage st;
		st = (Stage)this.tabs.getScene().getWindow();
		st.setFullScreen(false);
	}
	
	
	@FXML 
	private void augmente() {
		Stage st;
		st = (Stage)this.tabs.getScene().getWindow();
		st.setFullScreen(true);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Lie la vue de la liste à la liste d'utilisateurs du modèle
		this.list.setItems(this.modele.getUtilisateurs());

		// Change l'apparence des pseudos dans la liste des utilisateurs
		// Lance une demande de session lorsque l'on click sur un pseudo
		PseudoClass inactive = PseudoClass.getPseudoClass("inactive");
		PseudoClass session = PseudoClass.getPseudoClass("session");
		this.list.setCellFactory(lv -> {
			ListCell<Utilisateur> cell = new ListCell<Utilisateur>() {
				protected void updateItem(Utilisateur item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						setText(item.getPseudo());
						switch (item.getEtat()) {
						case CONNECTE:
							pseudoClassStateChanged(session, false);
							pseudoClassStateChanged(inactive, false);
							break;
						case DECONNECTE:
							pseudoClassStateChanged(session, false);
							pseudoClassStateChanged(inactive, true);
							break;
						case EN_SESSION:
							pseudoClassStateChanged(session, true);
							pseudoClassStateChanged(inactive, false);
							break;
						default:
							break;
						}
					}
				}
			};
			cell.setOnMouseClicked(e -> {
				if (!cell.isEmpty()) {
					lancementSession(cell.getItem());
				}
			});
			return cell;
		});
		
		// Fermeture possible de l'onglet selectionné
		this.tabs.setTabClosingPolicy(TabClosingPolicy.SELECTED_TAB);

		// Associe le changement de pseudo à une option du menu
		this.changerPseudo.setOnAction(e -> {
			saisiePseudo();
		});

		// Associe la déconnexion à une option du menu
		this.deconnexion.setOnAction(e -> {
			deconnexion();
		});

		// Lance le choix de pseudo avant la fin de l'initialisation
		saisiePseudo();
	}

}
