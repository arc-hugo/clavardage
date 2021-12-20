package gei.clavardage.modeles.utilisateurs;

import java.net.InetAddress;
import java.util.UUID;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class Utilisateur {
	
	private UUID uuid;
	private InetAddress adresse;
	private StringProperty pseudo;
	private Etat etat;
	
	public Utilisateur(UUID uuid, InetAddress adresse, String pseudo, Etat etat) {
		this.uuid = uuid;
		this.adresse = adresse;
		System.out.println(adresse);
		this.pseudo = new SimpleStringProperty(pseudo);
		this.etat = etat;
	}
	
	public Utilisateur(InetAddress adresse, String pseudo, Etat etat) {
		this(UUID.randomUUID(), adresse, pseudo, etat);
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
	
	public void changementEtat(Etat etat) {
		this.etat = etat;
	}
	
	public boolean isActif() {
		return this.etat.isActif();
	}
	
	public boolean isEnSession() {
		return this.etat.isEnSession();
	}
	
	public boolean isEnAttente() {
		return this.etat.isEnAttente();
	}
	
	public InetAddress getAdresse() {
		return this.adresse;
	}
	
	public static Callback<Utilisateur, Observable[]> extractor() {
        return new Callback<Utilisateur, Observable[]>() {
            @Override
            public Observable[] call(Utilisateur param) {
                return new Observable[]{param.pseudo};
            }
        };
    }

	@Override
	public String toString() {
		return ""+pseudo.get()+":"+uuid;
	}
}
