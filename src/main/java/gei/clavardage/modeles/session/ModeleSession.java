package gei.clavardage.modeles.session;

import java.util.UUID;

import gei.clavardage.modeles.utilisateurs.Utilisateur;

public class ModeleSession {

	private Utilisateur utilisateurLocal;
	private Utilisateur destinataire;
	
	public ModeleSession(Utilisateur local, Utilisateur destinataire) {
		this.utilisateurLocal = local;
		this.destinataire = destinataire;
	}
	
	public UUID getIdentifiant() {
		return this.utilisateurLocal.getIdentifiant();
	}
	
	public Utilisateur getDestinataire() {
		return this.destinataire;
	}

}
