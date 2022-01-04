package gei.barralberry.clavardage.concurrent;

import java.util.concurrent.ExecutorService;

public abstract class Executeur {

	private ExecutorService executeur;
	
	public Executeur(ExecutorService service) {
		this.executeur = service;
	}
	
	public void ajoutTache(Runnable tache) {
		this.executeur.execute(tache);
	}
}
