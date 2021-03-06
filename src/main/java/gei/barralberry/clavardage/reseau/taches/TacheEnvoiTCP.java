package gei.barralberry.clavardage.reseau.taches;

import java.net.Socket;

import gei.barralberry.clavardage.reseau.messages.Message;
import javafx.concurrent.Task;

public class TacheEnvoiTCP extends Task<Void> {

	private Socket sock;
	private Message msg;

	public TacheEnvoiTCP(Socket sock, Message msg) {
		this.sock = sock;
		this.msg = msg;
	}

	@Override
	protected Void call() throws Exception {
		if (msg != null && !sock.isClosed()) {
			msg.envoie(sock);
		}
		return null;
	}

}
