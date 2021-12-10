package gei.clavardage.modeles;

import java.util.UUID;

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
