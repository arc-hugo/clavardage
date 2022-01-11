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
import gei.barralberry.clavardage.util.Alerte;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ControleurUtilisateurs implements Initializable {

	@FXML
	private TabPane tabs;
	@FXML
	private ListView<Utilisateur> list;
	@FXML
	private MenuItem deconnexion;
	@FXML
	private MenuItem changerPseudo;
	@FXML
	private ButtonBar buttonbar;
	@FXML
	private MenuButton name;
	@FXML
	private VBox vb;
	@FXML
	private BorderPane pane;

	private ModeleUtilisateurs modele;
	private AccesUDP udp;
	private AccesTCP tcp;

	private int x = 0;
	private int y = 0;
	private Boolean resizebottom = false;
	private double dx;
	private double dy;

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
		if (!ControleurPseudo.estActif()) {
			FXMLLoader loader = new FXMLLoader(App.class.getResource("saisiePseudo.fxml"));
			loader.setController(new ControleurPseudo());
			Stage stage = new Stage();
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Saisie de pseudo");
			try {
				Scene scene = (Scene) loader.load();
				scene.setFill(Color.TRANSPARENT);
				stage.setScene(scene);
				String login = "";
				while (login.equals("")) {
					stage.showAndWait();
					ControleurPseudo pseudo = loader.getController();
					login = pseudo.getTxt();
				}
				udp.broadcastValidation(getIdentifiantLocal(), login);
				modele.setPseudoLocal(login);
			} catch (IOException e) {
				Alerte alert = Alerte.exceptionLevee(e);
				alert.show();
			}
		}
	}

	private void creationSession(Utilisateur util, Socket sock)
			throws IOException, SQLException, ClassNotFoundException {
		Tab tab = chercherSession(util.getIdentifiant());
		if (tab != null) {
			this.tabs.getTabs().remove(tab);
		}
		this.modele.setEtat(util.getIdentifiant(), EtatUtilisateur.EN_SESSION);

		ControleurSession session = new ControleurSession(modele.getUtilisateurLocal(), util, sock);
		FXMLLoader loader = new FXMLLoader(App.class.getResource("session.fxml"));
		loader.setController(session);

		Tab ntab = new Tab(util.getPseudo(), loader.load());
		ntab.textProperty().bind(util.getPseudoPropery());
		ntab.setOnClosed(e -> {
			session.fermetureLocale();
			if (this.modele.getEtat(util.getIdentifiant()) != EtatUtilisateur.DECONNECTE) {
				this.modele.setEtat(util.getIdentifiant(), EtatUtilisateur.CONNECTE);
			}
		});
		ntab.setUserData(session);
		Platform.runLater(new Runnable() {
			public void run() {
				tabs.getTabs().add(ntab);
			}
		});
	}

	private void afficherHistorique(Utilisateur util) throws IOException, ClassNotFoundException, SQLException {
		if (util.getEtat() != EtatUtilisateur.EN_SESSION) {
			ControleurSession historique = new ControleurSession(this.modele.getUtilisateurLocal(), util);
			FXMLLoader loader = new FXMLLoader(App.class.getResource("session.fxml"));
			loader.setController(historique);

			Tab tab = new Tab(util.getPseudo(), loader.load());
			tab.textProperty().bind(util.getPseudoPropery());
			tab.setUserData(historique);
			this.tabs.getTabs().add(tab);
		} else {
			String pseudo = util.getPseudo();
			for (Tab tab : this.tabs.getTabs()) {
				if (tab.getText().equals(pseudo)) {
					this.tabs.getSelectionModel().select(tab);
				}
			}
		}
	}

	public void lancementSession(Utilisateur destinataire) {
		if (destinataire.getEtat() == EtatUtilisateur.CONNECTE) {
			destinataire.setEtat(EtatUtilisateur.EN_ATTENTE);
			tcp.demandeConnexion(destinataire);
		} else if (destinataire.getEtat() == EtatUtilisateur.DECONNECTE) {
			Alerte deco = Alerte.utilisateurDeconnecte(destinataire.getPseudo());
			deco.show();
		}
	}

	public void lancementAccepte(Socket sock) {
		Utilisateur util = this.modele.getUtilisateurWithAdresse(sock.getInetAddress());
		try {
			if (util != null) {
				creationSession(util, sock);
			} else {
				sock.close();
			}
		} catch (IOException | SQLException | ClassNotFoundException e) {
			Alerte ex = Alerte.exceptionLevee(e);
			ex.show();
		}
	}

	public void lancementRefuse(InetAddress adresse) {
		Utilisateur util = this.modele.getUtilisateurWithAdresse(adresse);
		if (util != null) {
			Alerte refus = Alerte.refusConnexion(util.getPseudo());
			refus.show();
			modele.setEtat(util.getIdentifiant(), EtatUtilisateur.CONNECTE);
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

	public boolean demandeSession(Socket sock) {
		Utilisateur util = this.modele.getUtilisateurWithAdresse(sock.getInetAddress());
		if (util != null) {
			Alerte confirm = Alerte.accepterConnexion(util.getPseudo());
			Optional<ButtonType> result = confirm.showAndWait();
			if (result.get() == ButtonType.OK) {
				try {
					creationSession(util, sock);
				} catch (ClassNotFoundException | IOException | SQLException e) {
					Alerte alert = Alerte.exceptionLevee(e);
					alert.show();
				}
				return true;
			} else {
				return false;
				
			}
		} else {
			// TODO if util == null -> demande TCP de renvoi utilisateur
		}
		return false;
	}

	public void receptionUtilisateur(UUID identifiant, InetAddress adresse, String pseudo) {
		modele.connexion(identifiant, adresse, pseudo);
	}
	
	public void deconnexionDistante(UUID identifiant) {
		if (this.modele.getEtat(identifiant) == EtatUtilisateur.EN_SESSION) {
			Tab tab = chercherSession(identifiant);
			if (tab != null) {
				ControleurSession session = (ControleurSession) tab.getUserData();
				session.fermetureDistante();
			}
		}
		this.modele.setEtat(identifiant, EtatUtilisateur.DECONNECTE);
	}

	public boolean validationDistante(UUID uuid, String pseudo) {
		if (!(this.modele.getPseudoLocal().trim().equals(pseudo.trim()))) {
			Platform.runLater(new Runnable() {
				public void run() {
					modele.setPseudo(uuid, pseudo);
				}
			});
			return true;
		}
		return false;
	}

	private Tab chercherSession(UUID identifiant) {
		String pseudo = this.modele.getPseudo(identifiant);
		for (Tab tab : this.tabs.getTabs()) {
			if (tab.getText().equals(pseudo)) {
				return tab;
			}
		}
		return null;
	}

	@FXML
	private void ferme() {
		deconnexion();
	}

	@FXML
	private void diminue() {
		Stage st;
		st = (Stage) this.tabs.getScene().getWindow();
		st.setIconified(true);
	}

	@FXML
	private void change() {
		Stage st;
		st = (Stage) this.tabs.getScene().getWindow();
		if (st.isFullScreen()) {
			st.setFullScreen(false);
		} else {
			st.setFullScreen(true);
		}
	}

	@FXML
	private void enter(MouseEvent event) {
		Scene scene = pane.getScene();
		Stage stage = (Stage) pane.getScene().getWindow();
		if (event.getX() > stage.getWidth() - 50 && event.getX() < stage.getWidth() + 5) {
			scene.setCursor(Cursor.E_RESIZE);
		} else {
			scene.setCursor(Cursor.DEFAULT);
		}
	}
	/*
	 * @FXML private void exit(MouseEvent event) { Scene scene = pane.getScene();
	 * Stage stage = (Stage) pane.getScene().getWindow(); if (!(event.getX() >
	 * stage.getWidth() - 5 && event.getX() < stage.getWidth() + 5 )) {
	 * scene.setCursor(Cursor.DEFAULT); } }
	 */

	@FXML
	private void dragged1(MouseEvent event) {
		Stage stage = (Stage) buttonbar.getScene().getWindow();
		stage.setX(event.getScreenX() - x);
		stage.setY(event.getScreenY() - y);
	}

	@FXML
	private void dragged2(MouseEvent event) {
		Stage stage = (Stage) pane.getScene().getWindow();
		if (resizebottom == true) {
			stage.setWidth(event.getX() + dx);
			stage.setHeight(event.getY() + dy);
		}
	}

	@FXML
	private void pressed1(MouseEvent event) {
		x = (int) event.getSceneX();
		y = (int) event.getSceneY();
	}

	@FXML
	private void pressed2(MouseEvent event) {
		Stage stage = (Stage) pane.getScene().getWindow();
		Scene scene = pane.getScene();
		if (event.getX() > stage.getWidth() - 50 && event.getX() < stage.getWidth() + 50) {
			resizebottom = true;
			dx = stage.getWidth() - event.getX();
			scene.setCursor(Cursor.E_RESIZE);

		} else if (event.getY() > stage.getHeight() - 50 && event.getY() < stage.getHeight() + 50) {
			resizebottom = true;
			dy = stage.getHeight() - event.getY();
			scene.setCursor(Cursor.N_RESIZE);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Lie la vue de la liste à la liste d'utilisateurs du modèle
		this.list.setItems(this.modele.getUtilisateurs());

		this.name.textProperty().bind(this.modele.getUtilisateurLocal().getPseudoPropery());

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
					ContextMenu menu = new ContextMenu();
					MenuItem lance = new MenuItem("Discussion");
					lance.setOnAction(f -> {
						lancementSession(cell.getItem());
					});
					MenuItem hist = new MenuItem("Historique");
					hist.setOnAction(f -> {
						Platform.runLater(new Runnable() {
							public void run() {
								try {
									afficherHistorique(cell.getItem());
								} catch (IOException | ClassNotFoundException | SQLException e1) {
									Alerte exe = Alerte.exceptionLevee(e1);
									exe.show();
								}
							}
						});
					});
					menu.getItems().addAll(lance, hist);
					menu.show(cell, e.getSceneX(), e.getSceneY());
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
