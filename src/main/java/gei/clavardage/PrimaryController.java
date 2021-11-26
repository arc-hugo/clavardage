package gei.clavardage;

import java.io.IOException;
import java.net.UnknownHostException;

import gei.clavardage.modeles.PaquetUnicast;
import gei.clavardage.services.ServiceEnvoiUDP;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    
    @FXML
    private void sendMessage() throws UnknownHostException {
    	ServiceEnvoiUDP envoi = new ServiceEnvoiUDP(new PaquetUnicast("HELLO\n", "localhost"));
    	envoi.start();
    }
}
