package gei.clavardage.modeles.messages;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

import javafx.scene.Node;

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
	
	public abstract void envoie(Socket sock) throws IOException;
	public abstract Node affichage();
}
