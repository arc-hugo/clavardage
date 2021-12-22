package gei.clavardage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;

import java.io.IOException;

import gei.clavardage.controleurs.ControleurUtilisateurs;
import gei.clavardage.utils.Decoration;

/**
 * JavaFX App
 */
public class App extends Application {

	private static Scene scene;

	public static int UDP_PORT_ENVOI = 22540;
	public static int UDP_PORT_RECEPTION = 22540;
	public static int TCP_PORT_ENVOI = 30861;
	public static int TCP_PORT_RECEPTION = 30861;

	@Override
	public void start(Stage stage) throws IOException {
		ControleurUtilisateurs controleur = new ControleurUtilisateurs();

		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("principal.fxml"));
		fxmlLoader.setController(controleur);
		scene = new Scene(fxmlLoader.load());

		stage.setTitle("Logiciel de clavardage");
		stage.setScene(scene);

		/*
		 * BorderPane borderPane = new BorderPane();
		 * borderPane.setStyle("-fx-background-color: #666666;");
		 */

		ToolBar toolBar = new ToolBar();
		toolBar.getItems().add(new Decoration());

		/*
		 * borderPane.setTop(toolBar);
		 * 
		 * stage.setScene(new Scene(borderPane, 300, 250));
		 */

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
					int port = Integer.parseInt(args[i+1]);
					switch (args[i]) {
					case "--tcp-envoi":
						App.TCP_PORT_ENVOI = port;
						break;
					case "--tcp-reception":
						App.TCP_PORT_RECEPTION = port;
						break;
					case "--udp-envoi":
						App.UDP_PORT_ENVOI = port;
						break;
					case "--udp-reception":
						App.UDP_PORT_RECEPTION = port;
						break;
					default:
						break;
					}
				} else {
					System.err.println("Erreur dans le nombre de paramètres");
					System.exit(1);
				}
			}
		}
		launch();
	}

}