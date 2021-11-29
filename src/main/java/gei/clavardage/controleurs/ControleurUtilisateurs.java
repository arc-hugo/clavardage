package gei.clavardage.controleurs;

import java.io.IOException;
import java.util.*;

import gei.clavardage.App;
import gei.clavardage.modeles.ModeleUtilisateurs;
import gei.clavardage.modeles.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ControleurUtilisateurs {
	
	private ModeleUtilisateurs modele;
	private ControleurUDP udp;
	private ControleurTCP tcp;
	
	
	public ControleurUtilisateurs() {
		this.modele = new ModeleUtilisateurs();
		this.udp = new ControleurUDP(this);
		this.tcp = new ControleurTCP();
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
			stage.showAndWait();
			
			ControleurPseudo pseudo = loader.getController();
			String login = pseudo.getTxt();
			udp.broadcastValidation(getIdentifiantLocal(), login);
			modele.changementPseudo(getIdentifiantLocal(), login);
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
	
	protected void receptionUtilisateur(UUID identifiant, String adresse, String pseudo) {
		modele.connexion(identifiant, adresse, pseudo);
	}
	
	protected void deconnexionDistante(UUID identifiant) {
		modele.deconnexion(identifiant);
	}

	protected boolean validationDistante(String pseudo) {
		return modele.getPseudoLocal().equals(pseudo);
	}

}
