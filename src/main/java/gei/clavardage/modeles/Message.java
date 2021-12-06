package gei.clavardage.modeles;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

public abstract class Message {

	public static char END_MSG = 3;
	
	private Date date;
	private UUID auteur;
	
	public Message(UUID author) {
		this.auteur = author;
		this.date = new Date(System.currentTimeMillis());
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public UUID getAuteur() {
		return this.auteur;
	}
	
	public abstract void send(Socket sock) throws IOException;
}
