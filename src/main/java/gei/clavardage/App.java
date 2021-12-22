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

    @Override
    public void start(Stage stage) throws IOException {
    	ControleurUtilisateurs controleur = new ControleurUtilisateurs();    	
    	
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("principal.fxml"));
        fxmlLoader.setController(controleur);
        scene = new Scene(fxmlLoader.load());
        
        stage.setTitle("Logiciel de clavardage");
        stage.setScene(scene);

       /* BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #666666;");*/

        ToolBar toolBar = new ToolBar();
        toolBar.getItems().add(new Decoration());

        /*borderPane.setTop(toolBar);

        stage.setScene(new Scene(borderPane, 300, 250));*/
        
        stage.setOnCloseRequest(e -> {
        	controleur.deconnexion();
        	e.consume();
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }    

}