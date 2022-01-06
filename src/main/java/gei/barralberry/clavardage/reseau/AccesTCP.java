package gei.barralberry.clavardage.reseau;

import java.io.IOException;
import java.net.Socket;

import gei.barralberry.clavardage.concurrent.ExecuteurReseau;
import gei.barralberry.clavardage.controleurs.ControleurUtilisateurs;
import gei.barralberry.clavardage.modeles.utilisateurs.Utilisateur;
import gei.barralberry.clavardage.reseau.services.ServiceReceptionConnexionTCP;
import gei.barralberry.clavardage.reseau.taches.TacheConnexionTCP;

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
		this.ctrlUtilisateurs.lancementAccepte(sock);
	}
	
	public void connexionRefuse(Socket sock) throws IOException {
		this.ctrlUtilisateurs.lancementRefuse(sock);
	}
	
	public void utilisateurDeconnecte(Utilisateur destinatiare) {
		this.ctrlUtilisateurs.deconnexionDistante(destinatiare.getIdentifiant());
	}
}
