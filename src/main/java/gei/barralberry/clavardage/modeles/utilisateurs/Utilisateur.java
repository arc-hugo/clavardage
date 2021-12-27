package gei.barralberry.clavardage.modeles.utilisateurs;

import java.net.InetAddress;
import java.util.UUID;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class Utilisateur {
	
	private UUID uuid;
	private InetAddress adresse;
	private StringProperty pseudo;
	private ObjectProperty<EtatUtilisateur> etat;
	
	public Utilisateur(UUID uuid, InetAddress adresse, String pseudo, EtatUtilisateur etat) {
		this.uuid = uuid;
		this.adresse = adresse;
		this.pseudo = new SimpleStringProperty(pseudo);
		this.etat = new SimpleObjectProperty<EtatUtilisateur>(etat);
	}
	
	public Utilisateur(InetAddress adresse, String pseudo, EtatUtilisateur etat) {
		this(UUID.randomUUID(), adresse, pseudo, etat);
	}
	
	public String getPseudo() {
		return this.pseudo.get();
	}
	
	public StringProperty getPseudoPropery() {
		return this.pseudo;
	}
	
	public void setPseudo(String new_pseudo) {
		this.pseudo.set(new_pseudo);		
	}
	
	public UUID getIdentifiant () {
		return this.uuid;
	}
	
	public void setEtat(EtatUtilisateur etat) {
		this.etat.set(etat);
	}
	
	public EtatUtilisateur getEtat() {
		return this.etat.get();
	}
	
	public InetAddress getAdresse() {
		return this.adresse;
	}
	
	public static Callback<Utilisateur, Observable[]> extractor() {
        return new Callback<Utilisateur, Observable[]>() {
            @Override
            public Observable[] call(Utilisateur param) {
                return new Observable[]{param.pseudo, param.etat};
            }
        };
    }

	@Override
	public String toString() {
		return ""+pseudo.get()+":"+uuid;
	}
}
