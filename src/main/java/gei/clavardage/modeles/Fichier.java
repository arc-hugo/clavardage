package gei.clavardage.modeles;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class Fichier extends Message {

	private File file;
	
	public Fichier(UUID author, File file) {
		super(author);
		this.file = file;
	}

	@Override
	public void send(Socket sock) throws IOException {
		
	}

}
