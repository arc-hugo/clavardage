package gei.clavardage.modeles;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class Texte extends Message {

	private String txt;
	
	public Texte(UUID author, String txt) {
		super(author);
		this.txt = txt;
	}

	@Override
	public void send(Socket sock) throws IOException {
		PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
		writer.println(txt);
	}
}
