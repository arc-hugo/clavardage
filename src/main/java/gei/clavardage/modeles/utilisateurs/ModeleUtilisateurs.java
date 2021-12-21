package gei.clavardage.modeles.utilisateurs;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.IntStream;

import gei.clavardage.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ModeleUtilisateurs {
	
	private Utilisateur utilisateurLocal;
	private ObservableList<Utilisateur> utilisateurs;
	
	public ModeleUtilisateurs() {
		String range = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder builder = new StringBuilder();
		Random rdn = new Random();
		while (builder.length() < 20) {
			int i = (int) (rdn.nextFloat() * range.length());
			builder.append(range.charAt(i));
		}
		
		try {
			Preferences pref = Preferences.userNodeForPackage(App.class);
			String uuid = pref.get("uuid", null);
			if (uuid == null) {
				this.utilisateurLocal = new Utilisateur(InetAddress.getAllByName("localhost")[0], builder.toString(), EtatUtilisateur.CONNECTE);
				pref.put("uuid", this.utilisateurLocal.getIdentifiant().toString());
			} else {
				this.utilisateurLocal = new Utilisateur(UUID.fromString(uuid), InetAddress.getAllByName("localhost")[0], builder.toString(), EtatUtilisateur.CONNECTE);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.utilisateurs = FXCollections.observableArrayList(Utilisateur.extractor());
	}
	
	public Utilisateur getUtilisateurLocal() {
		return this.utilisateurLocal;
	}
	
	public String getPseudoLocal() {
		return this.utilisateurLocal.getPseudo();
	}
	
	public void setPseudoLocal(String pseudo) {
		this.utilisateurLocal.setPseudo(pseudo);
	}
	
	public ObservableList<Utilisateur> getUtilisateurs() {
		return this.utilisateurs;
	}
		
	private int getIndexById(UUID id) {
		return IntStream.range(0,utilisateurs.size())
				.filter(u -> utilisateurs.get(u).getIdentifiant().equals(id))
				.findFirst()
				.orElse(-1);
	}
	
	public void connexion(UUID identifiant, InetAddress adresse, String pseudo) {
		int trouve = getIndexById(identifiant);
		if (trouve == -1) {
			Utilisateur utilisateur = new Utilisateur(identifiant, adresse, pseudo, EtatUtilisateur.CONNECTE);
			utilisateurs.add(utilisateur);
		} else {
			Utilisateur util = utilisateurs.get(trouve);
			util.setPseudo(pseudo);
			if (util.getEtat() != EtatUtilisateur.EN_ATTENTE || util.getEtat() != EtatUtilisateur.EN_SESSION) {
				util.setEtat(EtatUtilisateur.CONNECTE);
			}
		}
	}

	public String getPseudo(UUID identifiant) {
		int trouve = getIndexById(identifiant);
		if (trouve >=0) {
			return utilisateurs.get(trouve).getPseudo();
		}
		return "";
	}
	
	public void setPseudo(UUID identifiant, String pseudo) {
		int trouve = getIndexById(identifiant);
		if (trouve >=0) {
			Utilisateur util = utilisateurs.get(trouve);
			util.setPseudo(pseudo);
		}
	}
	
	public EtatUtilisateur getEtat(UUID identifiant) {
		int trouve = getIndexById(identifiant);
		if (trouve >= 0) {
			return utilisateurs.get(trouve).getEtat();
		}
		return null;
	}
	
	public void setEtat(UUID identifiant, EtatUtilisateur etat) {
		int trouve = getIndexById(identifiant);
		if (trouve >=0) {
			utilisateurs.get(trouve).setEtat(etat);
		}
	}
	
	private int getIndexByAdresse(InetAddress inetAddress) {
		return IntStream.range(0,utilisateurs.size())
				.filter(u -> utilisateurs.get(u).getAdresse().equals(inetAddress))
				.findFirst()
				.orElse(-1);
	}
	
	public Utilisateur getUtilisateurWithAdresse(InetAddress inetAddress) {
		int trouve = getIndexByAdresse(inetAddress);
		if (trouve >=0) {
			return utilisateurs.get(trouve);
		}
		return null;
	}
	
	public Utilisateur getUtilisateurWithUUID(UUID identifiant) {
		int trouve = getIndexById(identifiant);
		if (trouve >= 0) {
			return utilisateurs.get(trouve);
		}
		return null;
	}
}
