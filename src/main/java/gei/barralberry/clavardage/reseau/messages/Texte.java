package gei.barralberry.clavardage.reseau.messages;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Texte extends MessageAffiche {

	private String txt;

	public Texte(UUID author, String txt) {
		super(author);
		this.txt = txt;
	}

	public Texte(UUID author, String txt, Date date) {
		super(author, date);
		this.txt = txt;
	}

	@Override
	public void envoie(Socket sock) throws IOException {
		synchronized (sock.getOutputStream()) {
			PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
			writer.print("TXT " + txt + Message.END_MSG);
			writer.flush();
		}
	}

	@Override
	public VBox affichage() {

		Label msg = new Label(txt);
		Label date = new Label(Message.DATE_FORMAT.format(getDate()));
		// msg.setAlignment(Pos.CENTER_RIGHT);
		VBox vb = new VBox(date, msg);
		// vb.prefWidthProperty().bind(tabs.widthProperty());
		// vb.prefHeightProperty().bind(tabs.heightProperty());
		/*
		 * AnchorPane ap = new AnchorPane(); ap.setRightAnchor(vb, null);
		 * ap.setLeftAnchor(vb, null); ap.setTopAnchor(vb, null); ap.setBottomAnchor(vb,
		 * null); x = tabs.getHeight(); ap.setPrefHeight(x); String recep =
		 * "-fx-background-color: red; -fx-text-fill: #f9f9f9; -fx-background-radius: 5; -fx-border-radius: 5;"
		 * ; noeud.setStyle(recep); String envoi =
		 * "-fx-background-color: #f9f9f9; -fx-text-fill: red; -fx-background-radius: 5; -fx-border-radius: 5;"
		 * ; Node noeud = msg.affichage(); noeud.setStyle(envoi);
		 */
		return vb;
	}

	@Override
	public String toString() {
		return txt;
	}

	@Override
	public String description() {
		return this.txt;
	}
}
