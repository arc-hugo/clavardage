package gei.barralberry.clavardage.utils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class Decoration extends HBox {

    public Decoration() {
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
