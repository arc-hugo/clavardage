package gei.clavardage.controleurs;

import java.util.UUID;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

	
	public class ControleurSession extends Application {
		    
		public static void main(String [] args) {
		        launch(args);
		    }
	
		 @Override
		 public void start(Stage stage) throws Exception {
			 Pane root = new Pane();
			 root.getChildren().addAll();
			 Scene scene = new Scene(root);
			 stage.setScene(scene);
			 stage.show();
		    }
		
		
	
	}
