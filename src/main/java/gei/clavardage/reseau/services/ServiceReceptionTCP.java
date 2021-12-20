package gei.clavardage.reseau.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import gei.clavardage.concurrent.ExecuteurSession;
import gei.clavardage.controleurs.ControleurSession;
import gei.clavardage.modeles.messages.Message;
import gei.clavardage.modeles.messages.Texte;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceReceptionTCP extends Service<Void> {

	private ControleurSession session;
	private BufferedReader reader;
	private ExecuteurSession executeur;
	
	public ServiceReceptionTCP(ControleurSession session, Socket sock) throws IOException {
		this.session = session;
		this.reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		this.executeur = ExecuteurSession.getInstance();
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			
			private Message msg;
			
			private void texte() throws IOException {
				String txt = "";
				char cha = (char) reader.read();
				while(cha != Message.END_MSG) {
					txt += cha;
					cha = (char) reader.read();
				}
				msg = new Texte(session.getIdentifiantLocal(), txt);
				
				executeur.ajoutTache(new Runnable() {	
					@Override
					public void run() {
						session.receptionMessage(msg);
					}
				});
			}
			
			private void fin() {
				executeur.ajoutTache(new Runnable() {
					@Override
					public void run() {
						session.fermetureDistante();
					}
				});
			}
			
			private void finok() {
				executeur.ajoutTache(new Runnable() {
					@Override
					public void run() {
						session.confirmerFermeture();
					}
				});
			}
			
			@Override
			protected Void call() throws IOException {
				String type = "";
				System.out.println("OK Reception");
				while (true) {
					char cha = (char) reader.read();
					while (cha >= 0 && cha != ' ' && cha != '\n' && cha != '\t') {
						type += cha;
						cha = (char) reader.read();
					}
					if (cha >= 0) {
						switch (type) {
						case "TXT":
							System.out.println("OK TXT");
							texte();
							break;
						case "FIN":
							System.out.println("OK FIN");
							fin();
						case "FINOK":
							finok();
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
