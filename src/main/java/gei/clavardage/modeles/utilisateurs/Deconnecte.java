package gei.clavardage.modeles.utilisateurs;

public class Deconnecte implements Etat {

	@Override
	public boolean isActif() {
		return false;
	}

	@Override
	public boolean isEnSession() {
		return false;
	}

	@Override
	public boolean isEnAttente() {
		return false;
	}

}
