package gei.barralberry.clavardage.modeles.session;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import gei.barralberry.clavardage.concurrent.ExecuteurDB;
import gei.barralberry.clavardage.donnees.AccesDB;
import gei.barralberry.clavardage.modeles.utilisateurs.Utilisateur;
import gei.barralberry.clavardage.reseau.messages.MessageAffiche;

public class ModeleSession {

	private Utilisateur utilisateurLocal;
	private Utilisateur destinataire;
	private Queue<MessageAffiche> queueEnvoi;
	private AccesDB accesDB;
	private ExecuteurDB ecriture;
	
	public ModeleSession(Utilisateur local, Utilisateur destinataire) throws ClassNotFoundException, SQLException, IOException {
		this.utilisateurLocal = local;
		this.destinataire = destinataire;
		this.queueEnvoi = new ArrayDeque<>();
		this.accesDB = new AccesDB(local.getIdentifiant(), destinataire.getIdentifiant());
		this.ecriture = ExecuteurDB.getInstance();
	}
	
	public UUID getIdentifiantLocal() {
		return this.utilisateurLocal.getIdentifiant();
	}
	
	public Utilisateur getDestinataire() {
		return this.destinataire;
	}
	
	public List<MessageAffiche> getDerniersMessages() throws SQLException {
		return this.accesDB.getDerniersMessages();
	}

	public synchronized void ajoutEnvoi(MessageAffiche msg) {
		this.queueEnvoi.add(msg);
	}
	
	public synchronized MessageAffiche envoiTermine() {
		MessageAffiche msg = this.queueEnvoi.remove();
		this.ecriture.ajoutTache(new Runnable() {
			@Override
			public void run() {
				accesDB.ajoutMessage(msg);
			}
		});
		return msg;
	}
	
	public void enregistrerReception(MessageAffiche msg) throws SQLException {
		this.ecriture.ajoutTache(new Runnable() {
			@Override
			public void run() {
				accesDB.ajoutMessage(msg);
			}
		});
	}
	
	public void fermeture() throws SQLException {
		this.accesDB.close();
	}
	
}
