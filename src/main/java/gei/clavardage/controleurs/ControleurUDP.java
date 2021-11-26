package gei.clavardage.controleurs;

import java.net.UnknownHostException;
import java.util.UUID;

import gei.clavardage.modeles.Paquet;
import gei.clavardage.modeles.PaquetBroadcast;
import gei.clavardage.modeles.PaquetUnicast;
import gei.clavardage.modeles.Utilisateur;
import gei.clavardage.services.ServiceEnvoiUDP;

public class ControleurUDP {
	
	ControleurUtilisateurs controleurUtilisateurs;
	
	public ControleurUDP(ControleurUtilisateurs controleurUtilisateurs) {
		this.controleurUtilisateurs = controleurUtilisateurs;
	}
	
	public void pseudoLocalInvalide() {
		//TODO Ajout notification -> ControleurUtilisateurs
	}
	
	private void envoi(Paquet paquet) {
		ServiceEnvoiUDP envoi = new ServiceEnvoiUDP(paquet);
		envoi.start();
		
	}
	
	public void deconnexion() {
		String msg = "DECONNEXION " + controleurUtilisateurs.getUtilisateurLocal().getIdentifiant();
		try {
			envoi(new PaquetBroadcast(msg));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void receptionUtilisateur(UUID uuid, String adresse, String pseudo) {
		// TODO Ajout utilisateur -> ControleurUtilisateurs
		
		Utilisateur local = controleurUtilisateurs.getUtilisateurLocal();
		String msg = "UTILISATEUR " + local.getIdentifiant() + "" + local.getPseudo();
		try {
			envoi(new PaquetUnicast(local.getAdresse(), msg));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	protected void validation(UUID uuid, String pseudo) {
		String msg = "VALIDATION " + uuid.toString() + " " + pseudo;
		try {
			envoi(new PaquetBroadcast(msg));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	protected void pseudoInvalide(String adresse) {
		try {
			envoi(new PaquetUnicast("INVALIDE",adresse));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
