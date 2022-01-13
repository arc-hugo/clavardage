package gei.barralberry.clavardage.reseau.messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

import javafx.scene.Node;

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
			FileInputStream reader = new FileInputStream(this.file);
			PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
			writer.print("FICHIER "+file.getName()+Message.END_MSG+file.length()+" ");
			
			System.out.println("Fichier "+file.getName()+" en cours d'envoi");
			
			OutputStream out = sock.getOutputStream();
			byte[] buffer = new byte[1024];
		    int bytesRead = 0;
			writer.flush();
			while ((bytesRead = reader.read(buffer)) != -1) {
		        out.write(buffer, 0, bytesRead);
		    }
			out.write(Message.END_MSG);
			out.flush();

			System.out.println("Fichier "+file.getName()+" envoyé");
		}
	}

	@Override
	public Node affichage() {
		
		return null;
	}

	@Override
	public String description() {
		return file.getName();
	}

}
