package gei.barralberry.clavardage.modeles.session;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import gei.barralberry.clavardage.concurrent.ExecuteurDB;
import gei.barralberry.clavardage.donnees.AccesDB;
import gei.barralberry.clavardage.modeles.utilisateurs.Utilisateur;
import gei.barralberry.clavardage.reseau.messages.MessageAffiche;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ModeleSession {

	private BooleanProperty connecte;
	private Utilisateur utilisateurLocal;
	private Utilisateur destinataire;
	private Socket sock;
	private Queue<MessageAffiche> queueEnvoi;
	private AccesDB accesDB;
	private ExecuteurDB ecriture;

	public ModeleSession(Utilisateur local, Utilisateur destinataire)
			throws ClassNotFoundException, SQLException, IOException {
		this.utilisateurLocal = local;
		this.destinataire = destinataire;
		this.connecte = new SimpleBooleanProperty(false);
		this.accesDB = new AccesDB(local.getIdentifiant(), destinataire.getIdentifiant());
	}

	public ModeleSession(Utilisateur local, Utilisateur destinataire, Socket sock)
			throws ClassNotFoundException, SQLException, IOException {
		this(local, destinataire);
		if (sock != null) {
			this.sock = sock;
			this.connecte.set(true);
			this.queueEnvoi = new ArrayDeque<>();
			this.ecriture = ExecuteurDB.getInstance();
		}
	}

	public UUID getIdentifiantLocal() {
		return this.utilisateurLocal.getIdentifiant();
	}

	public Utilisateur getDestinataire() {
		return this.destinataire;
	}

	public Socket getSocket() {
		return this.sock;
	}

	public BooleanProperty getConnecteProperty() {
		return this.connecte;
	}

	public boolean estConnecte() {
		return this.connecte.get();
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

	public synchronized MessageAffiche erreurEnvoi() {
		return this.queueEnvoi.remove();
	}

	public void enregistrerReception(MessageAffiche msg) throws SQLException {
		this.ecriture.ajoutTache(new Runnable() {
			@Override
			public void run() {
				accesDB.ajoutMessage(msg);
			}
		});
	}

	public void fermetureDB() throws SQLException {
		this.accesDB.close();
	}

	public void fermetureDistante() {
		this.connecte.set(false);
	}

}
