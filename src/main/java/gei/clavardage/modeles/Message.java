package gei.clavardage.modeles;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

public abstract class Message {

	private Date date;
	private UUID author;
	
	public Message(UUID author) {
		this.author = author;
		this.date = new Date(System.currentTimeMillis());
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public UUID getAuthor() {
		return this.author;
	}
	
	public abstract void send(Socket sock) throws IOException;
}
