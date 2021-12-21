package gei.clavardage.modeles.messages;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class OK extends Message {

	public OK(UUID author) {
		super(author);
	}

	@Override
	public void envoie(Socket sock) throws IOException {
		synchronized (sock.getOutputStream()) {
			PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
			writer.println("OK");
		}
	}
}
