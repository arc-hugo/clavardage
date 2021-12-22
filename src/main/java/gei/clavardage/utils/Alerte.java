package gei.clavardage.utils;

import gei.clavardage.App;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

public class Alerte extends Alert {

	private static String CSS = App.class.getResource("dialogues.css").toExternalForm();
	private static String CLASS = "dialogues";
	
	private Alerte(AlertType alertType) {
		super(alertType);
		DialogPane dialogPane = this.getDialogPane();
		dialogPane.getStylesheets().add(CSS);
		dialogPane.getStyleClass().add(CLASS);
	}

	public static Alerte confirmationDeconnexion() {
		Alerte deco = new Alerte(AlertType.CONFIRMATION);
		deco.setTitle("Déconnexion");
		deco.setHeaderText("Vous vous apprêtez à vous déconnecter de l'application.");
		deco.setContentText("Êtes-vous sûr de vouloir continuer ?");
		return deco;
	}
	
	public static Alerte accepterConnexion(String pseudo) {
		Alerte confirm = new Alerte(AlertType.CONFIRMATION);
		confirm.setTitle("Demande de lancement de session de " + pseudo);
		confirm.setHeaderText(pseudo + " souhaite lancer une session de discussion avec vous !");
		confirm.setContentText("Acceptez-vous cette demande ?");
		return confirm;
	}
	
	public static Alerte utilisateurDeconnecte(String pseudo) {
		Alerte refus = new Alerte(AlertType.INFORMATION);
		refus.setTitle("Deconnecté");
		refus.setContentText("L'utilisateur " + pseudo + " est deconnecté");
		return refus;
	}
	
	public static Alerte refusConnexion(String pseudo) {
		Alerte refus = new Alerte(AlertType.INFORMATION);
		refus.setTitle("Refus");
		refus.setContentText("L'utilisateur "+pseudo+" à refusé la demande de discussion");
		return refus;
	}
	
	public static Alerte fermetureSession(String pseudo) {
		Alerte ferme = new Alerte(AlertType.INFORMATION);
		ferme.setTitle("Fin de discussion");
		ferme.setContentText("L'utilisateur "+ pseudo +" vient de fermer la discussion.");
		return ferme;
	}
	
}
