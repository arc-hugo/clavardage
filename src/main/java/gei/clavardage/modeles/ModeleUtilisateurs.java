package gei.clavardage.modeles;

import java.util.*;
import java.util.stream.IntStream;

public class ModeleUtilisateurs {
	
	private Utilisateur utilisateurLocal;
	private List<Utilisateur> utilisateurs;
	
	Utilisateur getUtilisateurLocal() {
		return utilisateurLocal;
	}
	
	String getPseudoLocal() {
		return utilisateurLocal.getPseudo();
	}
	
	void setPseudoLocal(String pseudo) {
		utilisateurLocal.setPseudo(pseudo);
	}
		
	void connexion(Utilisateur utilisateur) {
		UUID id = utilisateur.getIdentifiant();
		String pseudo = utilisateur.getPseudo();
		
		int trouve = IntStream.range(0,utilisateurs.size())
					.filter(u -> utilisateurs.get(u).getIdentifiant().equals(id))
					.findFirst()
					.orElse(-1);
		if (trouve == -1) {
			utilisateurs.add(utilisateur);
			utilisateur.setActif(true);
		} else {
			Utilisateur util = utilisateurs.get(trouve);
			util.setPseudo(pseudo);
			util.setActif(true);
		}
		
	}
	
	void deconnexion(UUID identifiant) {
		int trouve = IntStream.range(0,utilisateurs.size())
				.filter(u -> utilisateurs.get(u).getIdentifiant().equals(identifiant))
				.findFirst()
				.orElse(-1);
		if (trouve >=0) {
			Utilisateur util = utilisateurs.get(trouve);
			util.setActif(false);
		}
		
		
		
	}
	
	void changementPseudo(UUID identifiant, String Pseudo) {
		int trouve = IntStream.range(0,utilisateurs.size())
				.filter(u -> utilisateurs.get(u).getIdentifiant().equals(identifiant))
				.findFirst()
				.orElse(-1);
		if (trouve >=0) {
			Utilisateur util = utilisateurs.get(trouve);
			util.setPseudo(Pseudo);
		}
	}
	
	boolean estActif(UUID identifiant) {
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
	
	Utilisateur getUtilisateurWithAdresse(String adresse) {
		int trouve = IntStream.range(0,utilisateurs.size())
				.filter(u -> utilisateurs.get(u).getAdresse().equals(adresse))
				.findFirst()
				.orElse(-1);
		if (trouve >=0) {
			Utilisateur util = utilisateurs.get(trouve);
			return util;
		} else {
			return null;
		}
	}
	
}
