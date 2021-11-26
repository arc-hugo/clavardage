package gei.clavardage.modeles;

import java.util.UUID;

public class Utilisateur {
	
	private UUID uuid;
	private String adresse;
	private String pseudo;
	private boolean actif;
	
	public Utilisateur(UUID uuid, String adresse, String pseudo, boolean actif) {
		this.uuid = uuid;
		this.adresse = adresse;
		this.pseudo = pseudo;
		this.actif = actif;
	}
	
	public Utilisateur(String adresse, String pseudo, boolean actif) {
		this.uuid = UUID.randomUUID();
		this.adresse = adresse;
		this.pseudo = pseudo;
		this.actif = actif;
	}
	
	String getPseudo() {
		return pseudo;
	}
	
	void setPseudo(String new_pseudo) {
		this.pseudo = new_pseudo;		
	}
	
	UUID getIdentifiant () {
		return this.uuid;
	}
	
	boolean isActif() {
		return actif;
	}
	
	void setActif(boolean new_actif) {
		this.actif = new_actif;
	}
	
	String getAdresse() {
		return this.adresse;
	}
}
