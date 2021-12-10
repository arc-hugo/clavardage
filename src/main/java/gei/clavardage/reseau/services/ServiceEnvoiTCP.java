package gei.clavardage.reseau.services;

import java.net.Socket;

import gei.clavardage.modeles.Message;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceEnvoiTCP extends Service<Void> {

	private Socket sock;
	private Message msg;
	
	public ServiceEnvoiTCP(Socket sock) {
		this.sock = sock;
		this.msg = null;
	}
	
	public void setMessage(Message msg) {
		this.msg = msg;
	}
	
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				if (msg != null) {
					msg.send(sock);
				}
				return null;
			}
		};
	}

}
