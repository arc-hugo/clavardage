package gei.barralberry.clavardage.reseau.messages;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class MessageOK extends Message {

	public MessageOK(UUID author) {
		super(author);
	}

	@Override
	public void envoie(Socket sock) throws IOException {
		synchronized (sock.getOutputStream()) {
			PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
			writer.println("MSGOK");
		}
	}

}
