package gei.barralberry.clavardage.reseau.messages;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Texte extends MessageAffiche {

	private String txt;
	
	public Texte(UUID author, String txt) {
		super(author);
		this.txt = txt;
	}
	
	public Texte(UUID author, String txt, Date date) {
		super(author,date);
		this.txt = txt;
	}

	@Override
	public void envoie(Socket sock) throws IOException {
		synchronized (sock.getOutputStream()) {
			PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
			writer.print("TXT "+txt+Message.END_MSG);
			writer.flush();
		}
	}

	@Override
	public Node affichage() {
		Label msg = new Label(txt);
		Label date = new Label(Message.DATE_FORMAT.format(getDate()));
		return new VBox(msg,date);
	}
	
	@Override
	public String toString() {
		return txt;
	}

	@Override
	public String description() {
		return this.txt;
	}
}
