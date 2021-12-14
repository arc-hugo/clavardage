package gei.clavardage.modeles;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

import javafx.scene.Node;

public class Fin extends Message {

	public Fin(UUID author) {
		super(author);
	}

	@Override
	public void envoie(Socket sock) throws IOException {
		PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
		writer.print("FIN ");
		writer.print(Message.END_MSG);
	}

	@Override
	public Node affichage() {
		return null;
	}

}
