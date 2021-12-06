package gei.clavardage.reseau.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import gei.clavardage.modeles.Utilisateur;
import gei.clavardage.reseau.AccesTCP;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceConnexionTCP extends Service<Void> {
	
	private AccesTCP tcp;
	private String adresse;
	
	public ServiceConnexionTCP(AccesTCP tcp, Utilisateur destinataire) {
		this.tcp = tcp;
		this.adresse = destinataire.getAdresse();
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			
			@Override
			protected Void call() throws Exception {
				Socket sock = new Socket(adresse, ServiceReceptionConnexionTCP.RECEPTION_PORT);
				BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				
				if (reader.readLine().equals("OK")) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							try {
								tcp.connexionAccepte(sock);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
				}
				return null;
			}
		};
	}

}
