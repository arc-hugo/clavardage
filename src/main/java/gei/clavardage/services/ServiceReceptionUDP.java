package gei.clavardage.services;

import gei.clavardage.controleurs.ControleurUDP;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class ServiceReceptionUDP extends ScheduledService<Void> {
	
	private final static int RECEPTION_PORT = 22540;
	
	private ControleurUDP controleur;
	
	public ServiceReceptionUDP(ControleurUDP controleur) {
		this.controleur = controleur;
	}

	@Override
	protected Task<Void> createTask() {
		return null;
	}

}
