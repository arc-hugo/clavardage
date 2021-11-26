package gei.clavardage.services;

import gei.clavardage.controleurs.ControleurUDP;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceEnvoiUDP extends Service<Void> {

	public final static int ENVOI_PORT = 24581;
	
	private ControleurUDP controleur;
	
	@Override
	protected Task<Void> createTask() {
		
		return null;
	}

}
