package gei.clavardage.reseau;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import gei.clavardage.controleurs.ControleurUtilisateurs;
import gei.clavardage.modeles.Paquet;
import gei.clavardage.modeles.PaquetBroadcast;
import gei.clavardage.modeles.PaquetUnicast;
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
	
	private void envoi(Paquet paquet) {
		ServiceEnvoiUDP envoi = new ServiceEnvoiUDP(paquet);
		envoi.start();
		
	}
	
	private void pseudoInvalide(InetAddress adresse) {
		try {
			String msg = "INVALIDE "
					+ ctrlUtilisateurs.getIdentifiantLocal()+" "
					+ctrlUtilisateurs.getPseudoLocal();
			envoi(new PaquetUnicast(msg, adresse));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void deconnexionDistante(UUID identifiant) {
		ctrlUtilisateurs.deconnexionDistante(identifiant);
	}
	
	public boolean validationUtilisateur(UUID uuid, InetAddress adresse, String pseudo) {
		if (! ctrlUtilisateurs.validationDistante(pseudo)) {
			pseudoInvalide(adresse);
			return false;
		}
		ctrlUtilisateurs.receptionUtilisateur(uuid, adresse, pseudo);
		String msg = "UTILISATEUR " 
				+ ctrlUtilisateurs.getIdentifiantLocal()+" "
				+ ctrlUtilisateurs.getPseudoLocal();
		try {
			envoi(new PaquetBroadcast(msg));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public void receptionUtilisateur(UUID uuid, InetAddress adresse, String pseudo) {
		ctrlUtilisateurs.receptionUtilisateur(uuid, adresse, pseudo);
	}
	
	public void broadcastDeconnexion() {
		String msg = "DECONNEXION " 
				+ ctrlUtilisateurs.getIdentifiantLocal()+" "
				+ ctrlUtilisateurs.getPseudoLocal();
		try {
			envoi(new PaquetBroadcast(msg));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void broadcastValidation(UUID id, String pseudo) {
		String msg = "VALIDATION "
				+ id.toString()+ " " 
				+ pseudo;
		try {
			envoi(new PaquetBroadcast(msg));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
