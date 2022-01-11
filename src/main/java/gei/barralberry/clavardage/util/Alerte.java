package gei.barralberry.clavardage.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import gei.barralberry.clavardage.App;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.stage.StageStyle;

public class Alerte extends Alert {

	private static String CSS = App.class.getResource("dialogues.css").toExternalForm();
	private static String LABEL = App.class.getResource("bouton.css").toExternalForm();
	private static String CLASS = "dialogues";
	
	private Alerte(AlertType alertType) {
		super(alertType);
		
		DialogPane dialogPane = this.getDialogPane();
		dialogPane.getStylesheets().add(CSS);
		dialogPane.getStyleClass().add(CLASS);
		
	/*}
	private void customisation(DialogPane dialogPane) {
		ButtonBar buttonbar = new ButtonBar();
		buttonbar.getStyleClass().add("bouton");
		Button r = new Button();
		r.getStyleClass().add("boutonR");
		Button o = new Button();
		o.getStyleClass().add("boutonO");
		Button v = new Button();
		v.getStyleClass().add("boutonV");
		r.lookup("boutonR");
		
		HBox buttons = new HBox();
		buttons.setAlignment( Pos.CENTER );
		buttons.setSpacing( 10 );
		buttons.getChildren().addAll(r,o,v);*/
		
		this.initStyle(StageStyle.UNDECORATED);
		
	}
	public static Alerte confirmationDeconnexion() {
		Alerte alert = new Alerte(AlertType.CONFIRMATION);
		//DialogPane dialogPane = alert.getDialogPane();
		//alert.customisation(dialogPane);
		alert.setTitle("Déconnexion");
		alert.setHeaderText("Vous vous apprêtez à vous déconnecter de l'application.");
		alert.setContentText("Êtes-vous sûr de vouloir continuer ?");
		return alert;
	}
	
	public static Alerte accepterConnexion(String pseudo) {
		Alerte alert = new Alerte(AlertType.CONFIRMATION);
		alert.setTitle("Demande de lancement de session de " + pseudo);
		alert.setHeaderText(pseudo + " souhaite lancer une session de discussion avec vous !");
		alert.setContentText("Acceptez-vous cette demande ?");
		return alert;
	}
	
	public static Alerte utilisateurDeconnecte(String pseudo) {
		Alerte alert = new Alerte(AlertType.INFORMATION);
		alert.setTitle("Deconnecté");
		alert.setHeaderText(null);
		alert.setContentText("L'utilisateur " + pseudo + " est deconnecté");
		return alert;
	}
	
	public static Alerte refusConnexion(String pseudo) {
		Alerte alert = new Alerte(AlertType.INFORMATION);
		alert.setTitle("Refus");
		alert.setHeaderText(null);
		alert.setContentText("L'utilisateur "+pseudo+" à refusé la demande de discussion");
		return alert;
	}
	
	public static Alerte fermetureSession(String pseudo) {
		Alerte alert = new Alerte(AlertType.INFORMATION);
		alert.setTitle("Fin de discussion");
		alert.setHeaderText(null);
		alert.setContentText("L'utilisateur "+ pseudo +" vient de fermer la discussion.");
		return alert;
	}
	
	public static Alerte exceptionLevee(Exception e) {
		Alerte alert = new Alerte(AlertType.ERROR);
		alert.setTitle("Une exception a été levée !");
		alert.setHeaderText(e.getMessage());
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String text = sw.toString();
		
		TextArea ta = new TextArea(text);
		ta.setEditable(false);
		ta.setWrapText(true);
		
		alert.getDialogPane().setExpandableContent(ta);
		
		return alert;
	}
	
	// TODO alerte erreur au démarrage
	
}
