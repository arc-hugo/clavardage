package gei.barralberry.clavardage.reseau.messages;

import java.util.Date;
import java.util.UUID;

import javafx.scene.layout.VBox;

public abstract class MessageAffiche extends Message {

	public MessageAffiche(UUID author) {
		super(author);
	}

	public MessageAffiche(UUID author, Date date) {
		super(author, date);
	}

	public abstract VBox affichage();

	public abstract String description();
}
