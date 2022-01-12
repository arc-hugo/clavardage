package gei.barralberry.clavardage.reseau.services;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import gei.barralberry.clavardage.concurrent.ExecuteurSession;
import gei.barralberry.clavardage.controleurs.ControleurSession;
import gei.barralberry.clavardage.reseau.messages.Message;
import gei.barralberry.clavardage.reseau.messages.MessageOK;
import gei.barralberry.clavardage.reseau.messages.Texte;
import gei.barralberry.clavardage.reseau.taches.TacheEnvoiTCP;
import gei.barralberry.clavardage.util.Alerte;
import gei.barralberry.clavardage.util.Configuration;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceReceptionTCP extends Service<Void> {

	private ControleurSession session;
	private Socket sock;
	private BufferedReader reader;
	private ExecuteurSession executeur;
	private File dossierSession;

	public ServiceReceptionTCP(ControleurSession session, Socket sock) throws IOException {
		this.session = session;
		this.sock = sock;
		this.reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		this.executeur = ExecuteurSession.getInstance();
		this.dossierSession = new File(Configuration.DOSSIER_CACHE+"/"+session.getDestinataire().getIdentifiant().toString());
		this.dossierSession.mkdirs();
	}

	@Override
	protected void cancelled() {
		super.cancelled();
		try {
			this.sock.close();
		} catch (IOException e) {
			Platform.runLater(new Runnable() {
				public void run() {
					Alerte ex = Alerte.exceptionLevee(e);
					ex.show();
				}
			});
		}
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			private void texte() throws IOException {
				String txt = "";
				char cha = (char) reader.read();
				while (cha != Message.END_MSG) {
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
				executeur.ajoutTache(new TacheEnvoiTCP(sock, new MessageOK(session.getIdentifiantLocal())));
			}

			private void fichier() throws IOException {
				// Récupère le nom et crée le fichier approprié
				String nom = "";
				char cha = (char) reader.read();
				while (cha != Message.END_MSG) {
					nom += cha;
					cha = (char) reader.read();
				}
				
				// Récupération de l'extension du fichier
				int extPos = nom.lastIndexOf('.');
				String extension;
				if (extPos != -1) {
					extension = nom.substring(extPos);
					nom = nom.substring(0,extPos);
				} else {
					extension = "";
				}
				
				// Création du fichier de réception
				File fichier = new File(dossierSession + "/" + nom + extension);
				int i = 1;
				while (fichier.exists()) {
					fichier = new File(dossierSession + "/" + String.format("%s(%d)", nom, i) + extension);
					i++;
				}
				fichier.createNewFile();
				
				// Récupération de la taille du fichier
				String taille = "";
				cha = (char) reader.read();
				while (cha != ' ') {
					taille += cha;
					cha = (char) reader.read();
				}
				long max = Long.parseLong(taille);
				System.out.println("Taille : "+max);
				
				InputStream in = sock.getInputStream();
				FileOutputStream ecriture = new FileOutputStream(fichier);
				byte buffer[] = new byte[1024];
				int recu = 0;
				long total = 0;
				while ((recu = in.read(buffer)) != -1 && total < max) {
					total += recu;
					System.out.println("Pourcentage du fichier reçu : "+(int)((total/max)*100)+"%");
					ecriture.write(buffer, (int)total, recu);
				}
				ecriture.flush();
				ecriture.close();
			}
			
			private void messageok() {
				session.envoiRecu();
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
				while (true) {
					String type = "";
					char cha = (char) reader.read();
					while (cha >= 0 && cha != ' ' && cha != '\n' && cha != Message.END_MSG) {
						type += cha;
						cha = (char) reader.read();
					}
					if (cha >= 0) {
						switch (type) {
						case "TXT":
							texte();
							break;
						case "MSGOK":
							messageok();
							break;
						case "FICHIER":
							fichier();
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
				}
			}
		};
	}

}
