package gei.clavardage.modeles.utilisateurs;

public class EnAttente implements Etat {

	@Override
	public boolean isActif() {
		return true;
	}

	@Override
	public boolean isEnSession() {
		return false;
	}

	@Override
	public boolean isEnAttente() {
		return true;
	}

}
