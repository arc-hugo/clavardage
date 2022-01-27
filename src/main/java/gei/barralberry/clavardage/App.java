package gei.barralberry.clavardage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import gei.barralberry.clavardage.controleurs.ControleurUtilisateurs;
import gei.barralberry.clavardage.donnees.AccesDB;
import gei.barralberry.clavardage.reseau.AccesTCP;
import gei.barralberry.clavardage.reseau.AccesUDP;
import gei.barralberry.clavardage.util.Configuration;
import gei.barralberry.clavardage.util.Decoration;

/**
 * JavaFX App
 */
public class App extends Application {

	private static Scene scene;

	@Override
	public void start(Stage stage) throws IOException {
		ControleurUtilisateurs controleur = new ControleurUtilisateurs();

		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("principal.fxml"));
		fxmlLoader.setController(controleur);
		scene = (Scene) (fxmlLoader.load());
		scene.setFill(Color.TRANSPARENT);
		stage.setTitle("Logiciel de clavardage");
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setResizable(true);
		stage.setScene(scene);

		/*
		 * scene.rootProperty().addListener(new ChangeListener<Parent>(){
		 * 
		 * @Override public void changed(ObservableValue<? extends Parent> arg0, Parent
		 * oldValue, Parent newValue){ scene.rootProperty().removeListener(this);
		 * scene.setRoot(root); ((Region)newValue).setPrefWidth(initWidth); //make sure
		 * is a Region! ((Region)newValue).setPrefHeight(initHeight); //make sure is a
		 * Region! root.getChildren().clear(); root.getChildren().add(newValue);
		 * scene.rootProperty().addListener(this); } });
		 */
		/*
		 * scene.setOnMouseExited(e -> { if (e.getX() <= (stage.getWidth() - 10) &&
		 * e.getX() >= (stage.getWidth() + 10) && e.getY() >= (stage.getHeight() - 10)
		 * && e.getY() <= stage.getHeight() + 10 ) { scene.setCursor(Cursor.DEFAULT); }
		 * }); scene.setOnMouseEntered(e -> { if (e.getX() <= (stage.getWidth() - 10) &&
		 * e.getX() >= (stage.getWidth() + 10) && e.getY() >= (stage.getHeight() - 10)
		 * && e.getY() <= stage.getHeight() + 10 ) { scene.setCursor(Cursor.N_RESIZE); }
		 * });
		 * 
		 * scene.setOnMousePressed(e -> { if (e.getX() > stage.getWidth() - 50 &&
		 * e.getX() < stage.getWidth() + 50 && e.getY() > stage.getHeight() - 50 &&
		 * e.getY() < stage.getHeight() + 50) { resizebottom = true; dx =
		 * stage.getWidth() - e.getX(); dy = stage.getHeight() - e.getY(); } else {
		 * resizebottom = false; xOffset = e.getSceneX(); yOffset = e.getSceneY(); } });
		 * 
		 * scene.setOnMouseDragged(e -> { if (resizebottom == false) {
		 * stage.setX(e.getScreenX() - xOffset); stage.setY(e.getScreenY() - yOffset); }
		 * else { stage.setWidth(e.getX() + dx); stage.setHeight(e.getY() + dy); } });
		 */

		ToolBar toolBar = new ToolBar();
		toolBar.getItems().add(new Decoration());

		stage.setOnCloseRequest(e -> {
			controleur.deconnexion();
			e.consume();
		});

		stage.show();
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			for (int i = 0; i < args.length; i += 2) {
				if (i + 1 < args.length) {
					int port = Integer.parseInt(args[i + 1]);
					if (port >= 1024 || port <= 65535) {
						switch (args[i]) {
						case "--tcp-envoi":
							Configuration.TCP_PORT_ENVOI = port;
							break;
						case "--tcp-reception":
							Configuration.TCP_PORT_RECEPTION = port;
							break;
						case "--udp-envoi":
							Configuration.UDP_PORT_ENVOI = port;
							break;
						case "--udp-reception":
							Configuration.UDP_PORT_RECEPTION = port;
							break;
						default:
							break;
						}
					} else {
						System.err.println("Erreur dans le numéro de port : " + port);
						System.exit(1);
					}
				} else {
					System.err.println("Erreur dans le nombre de paramètres");
					System.exit(2);
				}
			}
		}

		if (!AccesUDP.estUDPUtilise()) {
			if (!AccesTCP.estTCPUtilise()) {
				if (AccesDB.bloquerDB()) {
					launch();
				} else {
					System.err.println("Base de donnée SQLite est déjà bloquée par un autre porgramme");
					System.exit(5);
				}
			} else {
				System.err.println("Port TCP déjà utilisé par une instance ou une autre application");
				System.exit(3);
			}
		} else {
			System.err.println("Port UDP déjà utilisé par une instance ou une autre application");
			System.exit(3);
		}
	}
}