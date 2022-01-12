package gei.barralberry.clavardage.reseau.messages;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class Fichier extends MessageAffiche {

	private File file;
	
	public Fichier(UUID author, File file) {
		super(author);
		this.file = file;
	}
	
	public Fichier(UUID author, File file, Date date) {
		super(author,date);
		this.file = file;
	}

	@Override
	public void envoie(Socket sock) throws IOException {
		synchronized (sock.getOutputStream()) {
			PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
			writer.print("FICHIER "+ file.getName() +" ");
			writer.print(Message.END_MSG);
		}
	}

	@Override
	public VBox affichage() {
		
		return null;
	}

	@Override
	public String description() {
		return file.getName();
	}

}
