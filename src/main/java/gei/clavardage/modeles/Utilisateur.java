package gei.clavardage.modeles;

import java.util.UUID;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Utilisateur {
	
	private UUID uuid;
	private String adresse;
	private StringProperty pseudo;
	private boolean actif;
	private boolean enSession;
	
	public Utilisateur(UUID uuid, String adresse, String pseudo, boolean actif) {
		this.uuid = uuid;
		this.adresse = adresse;
		this.pseudo = new SimpleStringProperty(pseudo);
		this.actif = actif;
		this.enSession = false;
	}
	
	public Utilisateur(String adresse, String pseudo, boolean actif) {
		this(UUID.randomUUID(), adresse, pseudo, actif);
	}
	
	public String getPseudo() {
		return this.pseudo.get();
	}
	
	public void setPseudo(String new_pseudo) {
		this.pseudo.set(new_pseudo);		
	}
	
	public UUID getIdentifiant () {
		return this.uuid;
	}
	
	public boolean isEnSession() {
		return enSession;
	}

	public void setEnSession(boolean enSession) {
		this.enSession = enSession;
	}

	public boolean isActif() {
		return actif;
	}
	
	public void setActif(boolean actif) {
		this.actif = actif;
	}
	
	public String getAdresse() {
		return this.adresse;
	}
	
	@Override
	public String toString() {
		return pseudo.get();
	}
}
