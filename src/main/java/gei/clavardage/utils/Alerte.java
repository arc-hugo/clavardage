package gei.clavardage.utils;

import java.net.URL;

import gei.clavardage.App;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

public class Alerte extends Alert {

	private static String CSS = App.class.getResource("dialogues.css").toExternalForm();
	private static String CLASS = "dialogues";
	
	private Alerte(AlertType alertType) {
		super(alertType);
	}

	public static Alerte confirmationDeconnexion() {
		Alerte deco = new Alerte(AlertType.CONFIRMATION);
		DialogPane dialogPane = deco.getDialogPane();
		dialogPane.getStylesheets().add(CSS);
		dialogPane.getStyleClass().add(CLASS);
		deco.setTitle("Déconnexion");
		deco.setHeaderText("Vous vous apprêtez à vous déconnecter de l'application.");
		deco.setContentText("Êtes-vous sûr de vouloir continuer ?");
		return deco;
	}
	
	public static Alerte accepterConnexion(String pseudo) {
		Alerte confirm = new Alerte(AlertType.CONFIRMATION);
		DialogPane dialogPane = confirm.getDialogPane();
		dialogPane.getStylesheets().add(CSS);
		dialogPane.getStyleClass().add(CLASS);
		confirm.setTitle("Demande de lancement de session de " + pseudo);
		confirm.setHeaderText(pseudo + " souhaite lancer une session de discussion avec vous !");
		confirm.setContentText("Acceptez-vous cette demande ?");
		return confirm;
	}
	
	public static Alerte utilisateurDeconnecte(String pseudo) {
		Alerte refus = new Alerte(AlertType.INFORMATION);
		DialogPane dialogPane = refus.getDialogPane();
		dialogPane.getStylesheets().add(CSS);
		dialogPane.getStyleClass().add(CLASS);
		refus.setTitle("Deconnecté");
		refus.setContentText("L'utilisateur " + pseudo + " est deconnecté");
		return refus;
	}
	
}
