package gei.clavardage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import gei.clavardage.controleurs.ControleurUtilisateurs;

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
        
        /*stage.initStyle(StageStyle.UNDECORATED);

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #666666;");

        ToolBar toolBar = new ToolBar();

        int height = 25;
        toolBar.setPrefHeight(height);
        toolBar.setMinHeight(height);
        toolBar.setMaxHeight(height);
        toolBar.getItems().add(new WindowButtons());

        borderPane.setTop(toolBar);

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
    
    class WindowButtons extends HBox {

        public WindowButtons() {
            Button closeBtn = new Button("X");

            closeBtn.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent actionEvent) {
                    Platform.exit();
                }
            });

            this.getChildren().add(closeBtn);
        }
    }
    
    

}