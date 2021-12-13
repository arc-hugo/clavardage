package gei.clavardage.reseau;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import gei.clavardage.controleurs.ControleurUtilisateurs;
import gei.clavardage.modeles.Utilisateur;
import gei.clavardage.reseau.services.ServiceEnvoiUDP;
import gei.clavardage.reseau.services.ServiceReceptionUDP;

public class AccesUDP {
	
	ControleurUtilisateurs ctrlUtilisateurs;
	ServiceReceptionUDP reception;
	
	public AccesUDP(ControleurUtilisateurs controleurUtilisateurs) {
		this.ctrlUtilisateurs = controleurUtilisateurs;
		this.reception = new ServiceReceptionUDP(this);
		this.reception.start();
	}
	
	public void pseudoLocalInvalide() {
		ctrlUtilisateurs.saisiePseudo();
	}
	
	private void envoi(String msg, InetAddress adresse, boolean broadcast) {
		ServiceEnvoiUDP envoi = new ServiceEnvoiUDP(msg, adresse, broadcast);
		envoi.start();
		
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
	
	public boolean validationUtilisateur(UUID uuid, InetAddress adresse, String pseudo) {
		System.out.println(pseudo);
		if (! ctrlUtilisateurs.validationDistante(uuid, pseudo)) {
			System.out.println("pseudo invalide");
			pseudoInvalide(adresse);
			return false;
		}
		ctrlUtilisateurs.receptionUtilisateur(uuid, adresse, pseudo);
		String msg = "UTILISATEUR " 
				+ ctrlUtilisateurs.getIdentifiantLocal()+" "
				+ ctrlUtilisateurs.getPseudoLocal();
		envoi(msg, adresse, false);
		return true;
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
