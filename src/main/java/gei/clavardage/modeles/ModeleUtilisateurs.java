package gei.clavardage.modeles;

import java.net.InetAddress;
import java.util.*;
import java.util.stream.IntStream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ModeleUtilisateurs {
	
	private Utilisateur utilisateurLocal;
	private ObservableList<Utilisateur> utilisateurs;
	
	public ModeleUtilisateurs() {
		String range = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder builder = new StringBuilder();
		Random rdn = new Random();
		while (builder.length() < 10) {
			int i = (int) (rdn.nextFloat() * range.length());
			builder.append(range.charAt(i));
		}
		
		this.utilisateurLocal = new Utilisateur(UUID.randomUUID(), "localhost", builder.toString(), true);
		this.utilisateurs = FXCollections.observableArrayList();
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
			Utilisateur utilisateur = new Utilisateur(identifiant, adresse.getHostName(), pseudo, true);
			utilisateurs.add(utilisateur);
			utilisateur.setActif(true);
		} else {
			Utilisateur util = utilisateurs.get(trouve);
			util.setPseudo(pseudo);
			util.setActif(true);
		}
	}
	
	public void deconnexion(UUID identifiant) {
		int trouve = getIndexById(identifiant);
		if (trouve >=0) {
			Utilisateur util = utilisateurs.get(trouve);
			util.setActif(false);
		}
		
	}
	
	public void changementPseudo(UUID identifiant, String Pseudo) {
		int trouve = getIndexById(identifiant);
		if (trouve >=0) {
			Utilisateur util = utilisateurs.get(trouve);
			util.setPseudo(Pseudo);
		}
	}
	
	public boolean estActif(UUID identifiant) {
		int trouve = getIndexById(identifiant);
		if (trouve >=0) {
			Utilisateur util = utilisateurs.get(trouve);
			return util.isActif();
		
		} else {
			return false;
		}
	}
	
	private int getIndexByAdresse(String adresse) {
		return IntStream.range(0,utilisateurs.size())
				.filter(u -> utilisateurs.get(u).getAdresse().equals(adresse))
				.findFirst()
				.orElse(-1);
	}
	
	public Utilisateur getUtilisateurWithAdresse(String adresse) {
		int trouve = getIndexByAdresse(adresse);
		if (trouve >=0) {
			Utilisateur util = utilisateurs.get(trouve);
			return util;
		} else {
			return null;
		}
	}
	
}
