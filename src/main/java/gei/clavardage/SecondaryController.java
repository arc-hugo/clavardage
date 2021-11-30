package gei.clavardage;

import java.io.IOException;

import gei.clavardage.controleurs.ControleurUDP;
import gei.clavardage.controleurs.ControleurUtilisateurs;
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
    private void receiveMessage() throws IOException {
    	receive.setDisable(true);
    	ControleurUtilisateurs controleur = new ControleurUtilisateurs();
    	controleur.saisiePseudo();
    	ControleurUDP udp = new ControleurUDP(controleur);
    	ServiceReceptionUDP reception = new ServiceReceptionUDP(udp);
    	reception.start();
    }
}