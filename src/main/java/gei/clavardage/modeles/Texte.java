package gei.clavardage.modeles;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

import javafx.scene.Node;

public class Texte extends Message {

	private String txt;
	
	public Texte(UUID author, String txt) {
		super(author);
		this.txt = txt;
	}

	@Override
	public void envoie(Socket sock) throws IOException {
		PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
		writer.print("TXT "+txt);
		writer.print(Message.END_MSG);
	}

	@Override
	public Node affichage() {
		// TODO Auto-generated method stub
		return null;
	}
}
