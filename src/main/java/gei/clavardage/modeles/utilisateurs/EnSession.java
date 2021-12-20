package gei.clavardage.modeles.utilisateurs;

public class EnSession implements Etat {

	@Override
	public boolean isActif() {
		return true;
	}

	@Override
	public boolean isEnSession() {
		return true;
	}

	@Override
	public boolean isEnAttente() {
		return false;
	}

}
