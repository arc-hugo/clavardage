package gei.clavardage.modeles.messages;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class FinOK extends Fin {

	public FinOK(UUID author) {
		super(author);
	}

	@Override
	public void envoie(Socket sock) throws IOException {
		PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
		writer.print("FINOK ");
		writer.print(Message.END_MSG);
	}
}
