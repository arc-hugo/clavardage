package gei.barralberry.clavardage.reseau.messages;

import java.util.Date;
import java.util.UUID;

import javafx.scene.Node;

public abstract class MessageAffiche extends Message {

	public MessageAffiche(UUID author) {
		super(author);
	}
	
	public MessageAffiche(UUID author, Date date) {
		super(author, date);
	}
	
	public abstract Node affichage();
	public abstract String description();
}
