package gei.clavardage.reseau.taches;

import java.net.Socket;

import gei.clavardage.modeles.Message;
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
		if (msg != null) {
			msg.send(sock);
		}
		return null;
	}

}
