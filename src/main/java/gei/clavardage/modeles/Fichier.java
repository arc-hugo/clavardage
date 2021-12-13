package gei.clavardage.modeles;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

import javafx.scene.Node;

public class Fichier extends Message {

	private File file;
	
	public Fichier(UUID author, File file) {
		super(author);
		this.file = file;
	}

	@Override
	public void envoie(Socket sock) throws IOException {
		OutputStream out = sock.getOutputStream();
		PrintWriter writer = new PrintWriter(out, true);
		writer.print("FICHIER "+ file.getName() +" ");
		writer.println();
	}

	@Override
	public Node affichage() {
		
		return null;
	}

}
