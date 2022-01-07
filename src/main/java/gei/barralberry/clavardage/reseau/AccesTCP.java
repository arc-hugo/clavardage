package gei.barralberry.clavardage.reseau;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import gei.barralberry.clavardage.concurrent.ExecuteurReseau;
import gei.barralberry.clavardage.controleurs.ControleurUtilisateurs;
import gei.barralberry.clavardage.modeles.utilisateurs.Utilisateur;
import gei.barralberry.clavardage.reseau.services.ServiceReceptionConnexionTCP;
import gei.barralberry.clavardage.reseau.taches.TacheConnexionTCP;
import gei.barralberry.clavardage.util.Configuration;

public class AccesTCP {

	private ControleurUtilisateurs ctrlUtilisateurs;
	private ServiceReceptionConnexionTCP reception;
	private ExecuteurReseau executeur;
	
	public static boolean estTCPUtilise() {
		try {
			ServerSocket testTCP = new ServerSocket(Configuration.TCP_PORT_RECEPTION);
			testTCP.close();
			
		} catch (IOException e) {
			return true;
		}
		
		return false;
	}
	
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
	
	public void connexionAccepte(Socket sock) {
		this.ctrlUtilisateurs.lancementAccepte(sock);
	}
	
	public void connexionRefuse(Socket sock) {
		this.ctrlUtilisateurs.lancementRefuse(sock);
	}
	
	public void utilisateurDeconnecte(Utilisateur destinatiare) {
		this.ctrlUtilisateurs.deconnexionDistante(destinatiare.getIdentifiant());
	}
}
