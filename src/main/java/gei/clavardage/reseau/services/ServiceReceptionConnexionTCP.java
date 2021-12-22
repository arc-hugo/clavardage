package gei.clavardage.reseau.services;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import gei.clavardage.App;
import gei.clavardage.concurrent.ExecuteurReseau;
import gei.clavardage.reseau.AccesTCP;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceReceptionConnexionTCP extends Service<Void> {
	
	private AccesTCP tcp;
	private ExecuteurReseau executeur;
	
	public ServiceReceptionConnexionTCP(AccesTCP tcp) {
		this.tcp = tcp;
		this.executeur = ExecuteurReseau.getInstance();
	}
	
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				@SuppressWarnings("resource")
				ServerSocket sock = new ServerSocket(App.TCP_PORT_RECEPTION);
				while (true) {
					Socket link = sock.accept();
					executeur.ajoutTache(new Runnable() {
						@Override
						public void run() {
							try {
								tcp.receptionConnexion(link);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		};
	}

}
