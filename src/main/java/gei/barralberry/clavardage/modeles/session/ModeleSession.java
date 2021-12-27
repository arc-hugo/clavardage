package gei.barralberry.clavardage.modeles.session;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;

import gei.barralberry.clavardage.modeles.messages.MessageAffiche;
import gei.barralberry.clavardage.modeles.utilisateurs.Utilisateur;

public class ModeleSession {

	private Utilisateur utilisateurLocal;
	private Utilisateur destinataire;
	private Queue<MessageAffiche> queueEnvoi;
	
	public ModeleSession(Utilisateur local, Utilisateur destinataire) {
		this.utilisateurLocal = local;
		this.destinataire = destinataire;
		this.queueEnvoi = new ArrayDeque<>();
	}
	
	public UUID getIdentifiantLocal() {
		return this.utilisateurLocal.getIdentifiant();
	}
	
	public Utilisateur getDestinataire() {
		return this.destinataire;
	}

	public synchronized void ajoutEnvoi(MessageAffiche msg) {
		this.queueEnvoi.add(msg);
	}
	
	public synchronized MessageAffiche envoiTermine() {
		return this.queueEnvoi.remove();
	}
	
}
