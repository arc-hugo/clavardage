package gei.clavardage.modeles;

import java.util.UUID;

public class ModeleSession {

	private Utilisateur utilisateurLocal;
	private Utilisateur destinataire;
	
	
	public UUID getIdentifiant() {
		return this.utilisateurLocal.getIdentifiant();
	}

}
