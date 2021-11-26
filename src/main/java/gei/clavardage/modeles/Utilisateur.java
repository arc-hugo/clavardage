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
	
	public String getPseudo() {
		return pseudo;
	}
	
	public void setPseudo(String new_pseudo) {
		this.pseudo = new_pseudo;		
	}
	
	public UUID getIdentifiant () {
		return this.uuid;
	}
	
	public boolean isActif() {
		return actif;
	}
	
	public void setActif(boolean new_actif) {
		this.actif = new_actif;
	}
	
	public String getAdresse() {
		return this.adresse;
	}
}
