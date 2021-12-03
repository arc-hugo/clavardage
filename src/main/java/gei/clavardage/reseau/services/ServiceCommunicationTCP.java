package gei.clavardage.reseau.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import gei.clavardage.controleurs.ControleurSession;
import gei.clavardage.modeles.Message;
import gei.clavardage.modeles.Texte;
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
			
			private Message msg;
			
			private void texte() throws IOException {
				String txt = "";
				char cha = (char) reader.read();
				while(cha != 3) {
					txt += cha;
					cha = (char) reader.read();
				}
				msg = new Texte(session.getIdentifiant(), txt);
				
				Platform.runLater(new Runnable() {	
					@Override
					public void run() {
						session.receptionMessage(msg);
					}
				});
			}
			
			@Override
			protected Void call() throws IOException {
				String type = "";
				while (true) {
					char cha = (char) reader.read();
					while (cha >= 0 && cha != ' ' && cha != '\n' && cha != '\t') {
						type += cha;
						cha = (char) reader.read();
					}
					if (cha >= 0) {
						switch (type) {
						case "TXT":
							texte();
							break;
						default:
							break;
						}
						type = "";
					}
				}
			}
		};
	}

}
