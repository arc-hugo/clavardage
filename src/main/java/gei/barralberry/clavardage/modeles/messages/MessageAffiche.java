package gei.barralberry.clavardage.modeles.messages;

import java.util.UUID;

import javafx.scene.Node;

public abstract class MessageAffiche extends Message {

	public MessageAffiche(UUID author) {
		super(author);
	}

	public abstract Node affichage();

}
