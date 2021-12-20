package gei.clavardage.modeles.utilisateurs;

public interface Etat {
	public boolean isActif();
	public boolean isEnSession();
	public boolean isEnAttente();
}
