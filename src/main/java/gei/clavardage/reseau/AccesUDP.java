package gei.clavardage.reseau;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import gei.clavardage.concurrent.ExecuteurReseau;
import gei.clavardage.controleurs.ControleurUtilisateurs;
import gei.clavardage.modeles.utilisateurs.Utilisateur;
import gei.clavardage.reseau.services.ServiceReceptionUDP;
import gei.clavardage.reseau.taches.TacheEnvoiUDP;
import javafx.application.Platform;

public class AccesUDP {
	
	private ControleurUtilisateurs ctrlUtilisateurs;
	private ServiceReceptionUDP reception;
	private ExecuteurReseau executeur;
	
	public AccesUDP(ControleurUtilisateurs controleurUtilisateurs) {
		this.ctrlUtilisateurs = controleurUtilisateurs;
		this.executeur = ExecuteurReseau.getInstance();
		this.reception = new ServiceReceptionUDP(this);
		this.reception.start();
	}
	
	public void pseudoLocalInvalide() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				ctrlUtilisateurs.saisiePseudo();
			}
		});
	}
	
	private void envoi(String msg, InetAddress adresse, boolean broadcast) {
		this.executeur.ajoutTache(new TacheEnvoiUDP(msg, adresse, broadcast));
	}
	
	private void pseudoInvalide(InetAddress adresse) {
			String msg = "INVALIDE "
					+ ctrlUtilisateurs.getIdentifiantLocal()+" "
					+ ctrlUtilisateurs.getPseudoLocal();
			envoi(msg, adresse, false);
	}
	
	public void deconnexionDistante(UUID identifiant) {
		ctrlUtilisateurs.deconnexionDistante(identifiant);
	}
	
	public void validationUtilisateur(UUID uuid, InetAddress adresse, String pseudo) {
		System.out.println(pseudo);
		if (! ctrlUtilisateurs.validationDistante(uuid, pseudo)) {
			pseudoInvalide(adresse);
		}
		ctrlUtilisateurs.receptionUtilisateur(uuid, adresse, pseudo);
		String msg = "UTILISATEUR " 
				+ ctrlUtilisateurs.getIdentifiantLocal()+" "
				+ ctrlUtilisateurs.getPseudoLocal();
		envoi(msg, adresse, false);
	}
	
	public void receptionUtilisateur(UUID uuid, InetAddress adresse, String pseudo) {
		ctrlUtilisateurs.receptionUtilisateur(uuid, adresse, pseudo);
	}
	
	public void broadcastDeconnexion(Utilisateur utilisateur) {
		String msg = "DECONNEXION " 
				+ utilisateur.getIdentifiant() +" "
				+ utilisateur.getPseudo();
		try {
			envoi(msg, InetAddress.getByName("255.255.255.255"), true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void broadcastValidation(UUID id, String pseudo) {
		String msg = "VALIDATION "
				+ id.toString()+ " " 
				+ pseudo;
		try {
			envoi(msg, InetAddress.getByName("255.255.255.255"), true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
