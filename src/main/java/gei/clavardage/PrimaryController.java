package gei.clavardage;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.UUID;

import gei.clavardage.controleurs.ControleurUDP;
import gei.clavardage.controleurs.ControleurUtilisateurs;
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
    	ControleurUtilisateurs controleurUtilisateurs = new ControleurUtilisateurs();
    	ControleurUDP controleurUDP = new ControleurUDP(controleurUtilisateurs);
    	
    	controleurUDP.deconnexion();
    }
}
