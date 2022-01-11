package gei.barralberry.clavardage.reseau.messages;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class Texte extends MessageAffiche {

	private String txt;
	
	public Texte(UUID author, String txt) {
		super(author);
		this.txt = txt;
	}
	
	public Texte(UUID author, String txt, Date date) {
		super(author,date);
		this.txt = txt;
	}

	@Override
	public void envoie(Socket sock) throws IOException {
		synchronized (sock.getOutputStream()) {
			PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
			writer.print("TXT "+txt+Message.END_MSG);
			writer.flush();
		}
	}
	@FXML 
	private TabPane tabs;

	@Override
	public Node affichage() {
		
		
		Label msg = new Label(txt);
		Label date = new Label(Message.DATE_FORMAT.format(getDate()));
		date.setStyle("fx-alignement: center");
		date.setAlignment(Pos.CENTER);
		msg.getStylesheets().add("msgrecep.css");
		
		VBox vb = new VBox(date,msg);
		AnchorPane ap = new AnchorPane(tabs);
		ap.setRightAnchor(vb, null);
		ap.setLeftAnchor(vb, null);
		ap.setTopAnchor(vb, null);
		ap.setBottomAnchor(vb, null);
		ap.getChildren().addAll(vb);
		
		return ap;
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
