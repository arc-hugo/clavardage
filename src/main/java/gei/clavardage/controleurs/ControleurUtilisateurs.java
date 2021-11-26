package gei.clavardage.controleurs;

import java.io.IOException;
import java.util.*;

import gei.clavardage.modeles.ModeleUtilisateurs;
import gei.clavardage.modeles.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ControleurUtilisateurs {
	
	ModeleUtilisateurs modele;
	ControleurUDP udp;
	ControleurTCP tcp;
	
	@FXML
	
	public void saisiePseudo() throws IOException {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("saisiePseudo.fxml"));		
		Stage stage = new Stage();
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setTitle("Saisie de pseudo");
		stage.setScene(new Scene(loader.load()));
		stage.showAndWait();
		
		ControleurPseudo pseudo = loader.getController();
		String login = pseudo.getTxt();
	}
	
	public void lancementSession(UUID identifiant) {
	}
	
	public void demandeSession(Utilisateur utilisateur) {}
	
	public void deconnexion () {}
	
	protected void receptionPseudo(UUID identifiant, String adresse, String pseudo) {
	}
	
	protected void deconnexionDistante(UUID identifiant) {
	}
	
	
	
	
	
	
	
	
	

}
