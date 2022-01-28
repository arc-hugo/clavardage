package gei.barralberry.clavardage.reseau.messages;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AfficheErreur extends MessageAffiche {

	private MessageAffiche msg;

	public AfficheErreur(UUID author, MessageAffiche msg) {
		super(author);
		this.msg = msg;
	}

	@Override
	public void envoie(Socket sock) throws IOException {
		return;
	}

	@Override
	public VBox affichage() {
		Label msg = new Label("Erreur d'envoi de " + this.msg.description());
		Label date = new Label(Message.DATE_FORMAT.format(getDate()));
		VBox vb = new VBox(date, msg);
		return vb;
	}

	@Override
	public String description() {
		return "ERREUR : " + this.msg.description();
	}

}
