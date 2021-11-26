package gei.clavardage.controleurs;

import java.net.UnknownHostException;
import java.util.UUID;

import gei.clavardage.modeles.PaquetBroadcast;
import gei.clavardage.modeles.PaquetUnicast;
import gei.clavardage.services.ServiceEnvoiUDP;

public class ControleurUDP {
	
	ControleurUtilisateurs controleurUtilisateurs;
	
	public ControleurUDP(ControleurUtilisateurs controleurUtilisateurs) {
		this.controleurUtilisateurs = controleurUtilisateurs;
	}
	
	public void pseudoLocalInvalide() {
		//TODO
	}
	
	public void deconnexion() {
		UUID id = controleurUtilisateurs.getUtilisateurLocal().getIdentifiant();
		String msg = "DISCONNECTED" + id.toString();
		try {
			ServiceEnvoiUDP envoi = new ServiceEnvoiUDP(new PaquetBroadcast(msg));
			envoi.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void receptionUtilisateur(UUID uuid, String adresse, String pseudo) {
		
	}
	
	public void receptionPseudo(UUID uuid, String adresse, String pseudo) {
		
	}
	
	protected void validation(UUID uuid, String pseudo) {
		
	}
	
	protected void pseudoInvalide(String adresse) {
	}
}
