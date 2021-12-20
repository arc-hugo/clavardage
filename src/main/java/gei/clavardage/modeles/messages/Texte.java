package gei.clavardage.modeles.messages;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Texte extends Message {

	private String txt;
	
	public Texte(UUID author, String txt) {
		super(author);
		this.txt = txt;
	}

	@Override
	public void envoie(Socket sock) throws IOException {
		PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
		System.out.println(writer);
		writer.print("TXT "+txt);
		writer.print(Message.END_MSG);
	}

	@Override
	public Node affichage() {
		System.out.println(txt);
		Label msg = new Label(txt);
		Label date = new Label(getDate().toString());
		return new VBox(msg,date);
	}
	
	@Override
	public String toString() {
		return txt;
	}
}
