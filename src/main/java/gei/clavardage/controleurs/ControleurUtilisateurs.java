package gei.clavardage.controleurs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;

import gei.clavardage.App;
import gei.clavardage.modeles.ModeleUtilisateurs;
import gei.clavardage.modeles.Utilisateur;
import gei.clavardage.reseau.AccesTCP;
import gei.clavardage.reseau.AccesUDP;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ControleurUtilisateurs implements Initializable {
	
	@FXML
	private TabPane tabs;
	@FXML
	private ListView<Utilisateur> list;
	
	private ModeleUtilisateurs modele;
	private AccesUDP udp;
	@SuppressWarnings("unused")
	private AccesTCP tcp;
	
	public ControleurUtilisateurs() {
		this.modele = new ModeleUtilisateurs();
		this.udp = new AccesUDP(this);
		this.tcp = new AccesTCP();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.list.setItems(this.modele.getUtilisateurs());
		saisiePseudo();
	}
	
	public UUID getIdentifiantLocal() {
		return modele.getUtilisateurLocal().getIdentifiant();
	}
	
	public String getPseudoLocal() {
		return modele.getUtilisateurLocal().getPseudo();
	}
	ControleurSession session;
	
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
			System.out.println(modele.getPseudoLocal());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void lancementSession(UUID identifiant) {
		
	}
	
	public void deconnexion () {
		udp.broadcastDeconnexion();
	}
	
	protected void demandeSession(Utilisateur utilisateur) {
	}
	
	public void receptionUtilisateur(UUID identifiant, InetAddress adresse, String pseudo) {
		modele.connexion(identifiant, adresse, pseudo);
	}
	
	public void deconnexionDistante(UUID identifiant) {
		modele.deconnexion(identifiant);
	}

	public boolean validationDistante(String pseudo) {
		return !(modele.getPseudoLocal().trim().equals(pseudo.trim()));
	}

}
