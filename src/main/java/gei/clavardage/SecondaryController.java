package gei.clavardage;

import java.io.IOException;

import gei.clavardage.services.ServiceReceptionUDP;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SecondaryController {

	@FXML
	private Button receive;
	
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
    
    @FXML
    private void receiveMessage() {
    	receive.setDisable(true);
    	ServiceReceptionUDP reception = new ServiceReceptionUDP(null);
    	reception.start();
    }
}