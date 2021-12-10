package gei.clavardage.reseau;

import java.io.IOException;
import java.net.Socket;

import gei.clavardage.concurrent.ExecuteurReseau;
import gei.clavardage.controleurs.ControleurUtilisateurs;
import gei.clavardage.modeles.Utilisateur;
import gei.clavardage.reseau.services.ServiceReceptionConnexionTCP;
import gei.clavardage.reseau.taches.TacheConnexionTCP;

public class AccesTCP {

	private ControleurUtilisateurs ctrlUtilisateurs;
	private ServiceReceptionConnexionTCP reception;
	private ExecuteurReseau executeur;
	
	public AccesTCP(ControleurUtilisateurs ctrlUtilisateurs) {
		this.ctrlUtilisateurs = ctrlUtilisateurs;
		this.executeur = ExecuteurReseau.getInstance();
		this.reception = new ServiceReceptionConnexionTCP(this);
		reception.start();
	}
	
	public void receptionConnexion(Socket sock) throws IOException {
		this.ctrlUtilisateurs.demandeSession(sock);
	}

	public void demandeConnexion(Utilisateur destinataire) {
		this.executeur.ajoutTache(new TacheConnexionTCP(this, destinataire));
	}
	
	public void connexionAccepte(Socket sock) throws IOException {
		ctrlUtilisateurs.lancementAccepte(sock);
	}
}
