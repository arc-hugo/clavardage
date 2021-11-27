package gei.clavardage;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.UUID;

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
    	UUID uuid = UUID.randomUUID();
    	String msg = "UTILISATEUR " + uuid.toString() + " TEST";
    	ServiceEnvoiUDP envoiUDP = new ServiceEnvoiUDP(new PaquetUnicast(msg, "localhost"));
    	envoiUDP.start();
    }
}
