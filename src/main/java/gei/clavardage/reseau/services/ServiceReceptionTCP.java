package gei.clavardage.reseau.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import gei.clavardage.concurrent.ExecuteurSession;
import gei.clavardage.controleurs.ControleurSession;
import gei.clavardage.modeles.messages.Message;
import gei.clavardage.modeles.messages.Texte;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class ServiceReceptionTCP extends ScheduledService<Void> {

	private ControleurSession session;
	private BufferedReader reader;
	private ExecuteurSession executeur;
	
	public ServiceReceptionTCP(ControleurSession session, Socket sock) throws IOException {
		this.session = session;
		this.reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		this.executeur = ExecuteurSession.getInstance();
	}

	@Override
	protected void cancelled() {
		super.cancelled();
		try {
			this.reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("cancel");
	}
	
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			private void texte() throws IOException {
				System.out.println("TXT recu");
				String txt = "";
				char cha = (char) reader.read();
				while(cha != Message.END_MSG) {
					txt += cha;
					cha = (char) reader.read();
				}
				Texte msg = new Texte(session.getDestinataire().getIdentifiant(), txt);
				
				executeur.ajoutTache(new Runnable() {	
					@Override
					public void run() {
						session.receptionMessage(msg);
					}
				});
			}
			
			private void fin() {
				System.out.println("FIN recu");
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						session.fermetureDistante();
					}
				});
			}
			
			private void finok() {
				System.out.println("FINOK recu");
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
					char cha = (char) reader.read();
					while (cha >= 0 && cha != ' ' && cha != '\n' && cha != '\t') {
						type += cha;
						cha = (char) reader.read();
					}
					if (cha >= 0) {
						System.out.println(type);
						switch (type) {
						case "TXT":
							texte();
							break;
						case "FIN":
							fin();
							break;
						case "FINOK":
							finok();
							break;
						default:
							break;
						}
						type = "";
					}
					return null;
				}
		};
	}

}
