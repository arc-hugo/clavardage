package gei.clavardage.reseau;

import java.io.IOException;
import java.net.Socket;

import gei.clavardage.controleurs.ControleurUtilisateurs;
import gei.clavardage.reseau.services.ServiceReceptionConnexionTCP;

public class AccesTCP {
	
	ControleurUtilisateurs ctrlUtilisateurs;
	ServiceReceptionConnexionTCP reception;
	
	public AccesTCP(ControleurUtilisateurs ctrlUtilisateurs) {
		this.ctrlUtilisateurs = ctrlUtilisateurs;
		this.reception = new ServiceReceptionConnexionTCP(this);
		reception.start();
	}
	
	public void receptionConnexion(Socket sock) throws IOException {
		this.ctrlUtilisateurs.demandeSession(sock);
	}
}
