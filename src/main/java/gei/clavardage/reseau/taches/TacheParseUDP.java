package gei.clavardage.reseau.taches;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.UUID;

import gei.clavardage.concurrent.ExecuteurFond;
import gei.clavardage.reseau.AccesUDP;
import javafx.concurrent.Task;

public class TacheParseUDP extends Task<Void> {

	private AccesUDP udp;
	private String type;
	private UUID uuid;
	private String pseudo;
	private InetAddress adresse;
	private ExecuteurFond executeur;
	
	public TacheParseUDP(AccesUDP udp, String message, InetAddress adresse) {
		this.udp = udp;
		this.executeur = ExecuteurFond.getInstance();
		
		String[] split = message.split(" ");
		this.type = split[0];
		this.uuid = UUID.fromString(split[1]);
		this.pseudo = split[2];
		for (int i = 3; i < split.length; i++) {
			this.pseudo = this.pseudo+" "+split[i];
		}
		this.adresse = adresse;
		
		System.out.println(this.type + " " 
				+ this.uuid + " "
				+ this.pseudo + " "
				+ this.adresse);
	}
	
	private void deconnexion() {
		this.executeur.ajoutTache(new Runnable() {
			@Override
			public void run() {
				udp.deconnexionDistante(uuid);
			}
		});
	}

	private void utilisateur() {
		this.executeur.ajoutTache(new Runnable() {
			@Override
			public void run() {
				udp.receptionUtilisateur(uuid, adresse, pseudo);
			}
		});
	}

	private void validation() {
		this.executeur.ajoutTache(new Runnable() {
			@Override
			public void run() {
				udp.validationUtilisateur(uuid, adresse, pseudo);
			}
		});
	}

	private void invalide() {
		this.executeur.ajoutTache(new Runnable() {
			@Override
			public void run() {
				udp.pseudoLocalInvalide();
			}
		});
	}
	
	@Override
	protected Void call() throws Exception {
		if (NetworkInterface.getByInetAddress(adresse) == null) {
			switch (type) {
			case "DECONNEXION":
				deconnexion();
				break;
			case "UTILISATEUR":
				utilisateur();
				break;
			case "VALIDATION":
				validation();
				break;
			case "INVALIDE":
				invalide();
			default:
				break;
			}
		}
		return null;
	}

}
