package gei.clavardage.controleurs;

import java.net.UnknownHostException;
import java.util.UUID;

import gei.clavardage.modeles.Paquet;
import gei.clavardage.modeles.PaquetBroadcast;
import gei.clavardage.modeles.PaquetUnicast;
import gei.clavardage.services.ServiceEnvoiUDP;

public class ControleurUDP {
	
	ControleurUtilisateurs ctrlUtilisateurs;
	
	public ControleurUDP(ControleurUtilisateurs controleurUtilisateurs) {
		this.ctrlUtilisateurs = controleurUtilisateurs;
	}
	
	public void pseudoLocalInvalide() {
		ctrlUtilisateurs.saisiePseudo();
	}
	
	private void envoi(Paquet paquet) {
		ServiceEnvoiUDP envoi = new ServiceEnvoiUDP(paquet);
		envoi.start();
		
	}
	
	private void pseudoInvalide(String adresse) {
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
	
	public boolean validationUtilisateur(UUID uuid, String adresse, String pseudo) {
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
	
	public void receptionUtilisateur(UUID uuid, String adresse, String pseudo) {
		ctrlUtilisateurs.receptionUtilisateur(uuid, adresse, pseudo);
	}
	
	protected void broadcastDeconnexion() {
		String msg = "DECONNEXION " 
				+ ctrlUtilisateurs.getIdentifiantLocal()+" "
				+ ctrlUtilisateurs.getPseudoLocal();
		try {
			envoi(new PaquetBroadcast(msg));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	protected void broadcastValidation(UUID id, String pseudo) {
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
