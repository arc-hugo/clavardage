package gei.clavardage.modeles;

import java.util.*;
import java.util.stream.IntStream;

public class ModeleUtilisateurs {
	
	private Utilisateur utilisateurLocal;
	private List<Utilisateur> utilisateurs;
	
	public ModeleUtilisateurs() {
		String range = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder builder = new StringBuilder();
		Random rdn = new Random();
		while (builder.length() < 10) {
			int i = (int) (rdn.nextFloat() * range.length());
			builder.append(range.charAt(i));
		}
		
		this.utilisateurLocal = new Utilisateur(UUID.randomUUID(), "localhost", builder.toString(), true);
		this.utilisateurs = new ArrayList<Utilisateur>();
	}
	
	public Utilisateur getUtilisateurLocal() {
		return utilisateurLocal;
	}
	
	public String getPseudoLocal() {
		return utilisateurLocal.getPseudo();
	}
	
	public void setPseudoLocal(String pseudo) {
		utilisateurLocal.setPseudo(pseudo);
	}
		
	private int getIndexById(UUID id) {
		return IntStream.range(0,utilisateurs.size())
				.filter(u -> utilisateurs.get(u).getIdentifiant().equals(id))
				.findFirst()
				.orElse(-1);
	}
	
	public void connexion(UUID identifiant, String adresse, String pseudo) {
		int trouve = getIndexById(identifiant);
		if (trouve == -1) {
			Utilisateur utilisateur = new Utilisateur(identifiant, adresse, pseudo, true);
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
		int trouve = IntStream.range(0,utilisateurs.size())
				.filter(u -> utilisateurs.get(u).getIdentifiant().equals(identifiant))
				.findFirst()
				.orElse(-1);
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
