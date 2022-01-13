package gei.barralberry.clavardage.reseau.messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.UUID;

import gei.barralberry.clavardage.util.Alerte;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class Fichier extends MessageAffiche {

	private File file;
	
	public Fichier(UUID author, File file) {
		super(author);
		this.file = file;
	}
	
	public Fichier(UUID author, File file, Date date) {
		super(author,date);
		this.file = file;
	}

	@Override
	public void envoie(Socket sock) throws IOException {
		synchronized (sock.getOutputStream()) {
			FileInputStream reader = new FileInputStream(this.file);
			PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
			writer.print("FICHIER "+file.getName()+Message.END_MSG+file.length()+" ");
			
			OutputStream out = sock.getOutputStream();
			byte[] buffer = new byte[1024];
		    int bytesRead = 0;
			writer.flush();
			while ((bytesRead = reader.read(buffer)) != -1) {
		        out.write(buffer, 0, bytesRead);
		    }
			out.write(Message.END_MSG);
			out.flush();
		}
	}

	@Override
	public VBox affichage() {
		Hyperlink msg = new Hyperlink(file.getName());
		msg.setOnAction(e ->{
			FileChooser choix = new FileChooser();
			choix.setInitialFileName(msg.getText());
			File en = choix.showSaveDialog(msg.getScene().getWindow());
			if (en != null) {
				try {
					Files.copy(new FileInputStream(file), en.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e1) {
					Alerte ex = Alerte.exceptionLevee(e1);
					ex.show();
				}
			}
		});
		
		Label date = new Label(Message.DATE_FORMAT.format(getDate()));
		VBox vb = new VBox(date,msg);
		return vb;
	}

	@Override
	public String description() {
		return file.getName();
	}

}
