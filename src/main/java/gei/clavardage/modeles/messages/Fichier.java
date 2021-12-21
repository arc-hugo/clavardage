package gei.clavardage.modeles.messages;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

import javafx.scene.Node;

public class Fichier extends MessageAffiche {

	private File file;
	
	public Fichier(UUID author, File file) {
		super(author);
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
	public Node affichage() {
		
		return null;
	}

}
