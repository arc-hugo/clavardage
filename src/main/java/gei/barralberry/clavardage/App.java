package gei.barralberry.clavardage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;

import java.io.IOException;

import gei.barralberry.clavardage.controleurs.ControleurUtilisateurs;
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

		stage.setTitle("Logiciel de clavardage");
		stage.setScene(scene);

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
					int port = Integer.parseInt(args[i+1]);
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
					System.err.println("Erreur dans le nombre de paramètres");
					System.exit(1);
				}
			}
		}
		launch();
	}

}