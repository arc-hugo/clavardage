package gei.clavardage.reseau;

import java.io.IOException;
import java.net.Socket;

import gei.clavardage.controleurs.ControleurUtilisateurs;
import gei.clavardage.reseau.services.ServiceReceptionTCP;

public class AccesTCP {
	
	public static char ETX = 3;
	
	ControleurUtilisateurs ctrlUtilisateurs;
	ServiceReceptionTCP reception;
	
	public AccesTCP(ControleurUtilisateurs ctrlUtilisateurs) {
		this.ctrlUtilisateurs = ctrlUtilisateurs;
		this.reception = new ServiceReceptionTCP(this);
		reception.start();
	}
	
	public void receptionConnexion(Socket sock) throws IOException {
		this.ctrlUtilisateurs.demandeSession(sock);
	}
}
