package gei.clavardage.modeles;

import java.net.InetAddress;
import java.util.UUID;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class Utilisateur {
	
	private UUID uuid;
	private InetAddress adresse;
	private StringProperty pseudo;
	private BooleanProperty actif;
	private BooleanProperty enSession;
	
	public Utilisateur(UUID uuid, InetAddress adresse, String pseudo, boolean actif) {
		this.uuid = uuid;
		this.adresse = adresse;
		System.out.println(adresse);
		this.pseudo = new SimpleStringProperty(pseudo);
		this.actif = new SimpleBooleanProperty(actif);
		this.enSession = new SimpleBooleanProperty(false);
	}
	
	public Utilisateur(InetAddress adresse, String pseudo, boolean actif) {
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
		return enSession.get();
	}

	public void setEnSession(boolean enSession) {
		this.enSession.set(enSession);
	}

	public boolean isActif() {
		return actif.get();
	}
	
	public void setActif(boolean actif) {
		this.actif.set(actif);
	}
	
	public InetAddress getAdresse() {
		return this.adresse;
	}
	
	public static Callback<Utilisateur, Observable[]> extractor() {
        return new Callback<Utilisateur, Observable[]>() {
            @Override
            public Observable[] call(Utilisateur param) {
                return new Observable[]{param.pseudo, param.actif, param.enSession};
            }
        };
    }

	@Override
	public String toString() {
		return ""+pseudo.get()+":"+uuid;
	}
}
