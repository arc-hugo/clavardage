package gei.barralberry.clavardage;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import gei.barralberry.clavardage.controleurs.ControleurUtilisateurs;
import gei.barralberry.clavardage.utils.Decoration;

/**
 * JavaFX App
 */
public class App extends Application {

	private static Scene scene;

	public static int UDP_PORT_ENVOI = 22540;
	public static int UDP_PORT_RECEPTION = 22540;
	public static int TCP_PORT_ENVOI = 30861;
	public static int TCP_PORT_RECEPTION = 30861;
	private Boolean resizebottom = false;
    private double dx;
    private double dy;
    private double xOffset;
    private double yOffset;
	
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
		/*scene.setOnMouseExited(e -> {
			if (e.getX() <= (stage.getWidth() - 10) 
					&& e.getX() >= (stage.getWidth() + 10) 
					&& e.getY() >= (stage.getHeight() - 10) 
					&& e.getY() <= stage.getHeight() + 10 ) {
				scene.setCursor(Cursor.DEFAULT);
			}
		});
		scene.setOnMouseEntered(e -> {
			if (e.getX() <= (stage.getWidth() - 10) 
					&& e.getX() >= (stage.getWidth() + 10) 
					&& e.getY() >= (stage.getHeight() - 10) 
					&& e.getY() <= stage.getHeight() + 10 ) {
				scene.setCursor(Cursor.N_RESIZE);
			}
		});
		
		scene.setOnMousePressed(e -> {
		  	if (e.getX() > stage.getWidth() - 50
	                && e.getX() < stage.getWidth() + 50
	                && e.getY() > stage.getHeight() - 50
	                && e.getY() < stage.getHeight() + 50) {
	            resizebottom = true;
	            dx = stage.getWidth() - e.getX();
	            dy = stage.getHeight() - e.getY();
	        } else {
	            resizebottom = false;
	            xOffset = e.getSceneX();
	            yOffset = e.getSceneY();
	        } 
	    });

	  scene.setOnMouseDragged(e -> {
	        if (resizebottom == false) {
	            stage.setX(e.getScreenX() - xOffset);
	            stage.setY(e.getScreenY() - yOffset);
	        } else {
	            stage.setWidth(e.getX() + dx);
	            stage.setHeight(e.getY() + dy);
	        }
	    });*/

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