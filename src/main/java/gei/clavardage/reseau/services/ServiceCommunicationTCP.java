package gei.clavardage.reseau.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import gei.clavardage.controleurs.ControleurSession;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceCommunicationTCP extends Service<Void> {

	private ControleurSession session;
	private BufferedReader reader;
	
	public ServiceCommunicationTCP(ControleurSession session, Socket sock) throws IOException {
		this.session = session;
		this.reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	}
	
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				while (true) {
					String input = reader.readLine();
					if (!input.equals("\n")) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								
							}
						});
					}
				}
			}
		};
	}

}
