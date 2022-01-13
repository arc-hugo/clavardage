package gei.barralberry.clavardage.reseau.messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.UUID;

import gei.barralberry.clavardage.util.Alerte;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class Fichier extends MessageAffiche {

	private File fichier;
	private String extension;

	public Fichier(UUID author, File fichier) {
		super(author);
		this.fichier = fichier;

		int extPos = fichier.getName().lastIndexOf('.');
		if (extPos != -1) {
			this.extension = fichier.getName().substring(extPos);
		} else {
			this.extension = "";
		}
	}

	public Fichier(UUID author, File fichier, Date date) {
		super(author, date);
		this.fichier = fichier;

		int extPos = fichier.getName().lastIndexOf('.');
		if (extPos != -1) {
			this.extension = fichier.getName().substring(extPos);
		} else {
			this.extension = "";
		}
	}

	@Override
	public void envoie(Socket sock) throws IOException {
		synchronized (sock.getOutputStream()) {
			FileInputStream reader = new FileInputStream(this.fichier);
			PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
			writer.print("FICHIER " + fichier.getName() + Message.END_MSG + fichier.length() + " ");

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
		try {
			Node msg;
			if (this.extension.toLowerCase().equals(".jpg")  ||
				this.extension.toLowerCase().equals(".jpeg")  ||
				this.extension.toLowerCase().equals(".png")  ||
				this.extension.toLowerCase().equals(".gif")) {
				Image image = new Image(new FileInputStream(fichier));
				msg = new ImageView(image);
				if (image.getWidth() > 506) {
					((ImageView) msg).setFitWidth(506);
				}
				if (image.getHeight() > 506) {
					((ImageView) msg).setFitHeight(506);
				}
				((ImageView) msg).setPreserveRatio(true);
			} else {
				msg = new Hyperlink(fichier.getName());
			}
			msg.setOnMouseClicked(e -> {
				if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
					FileChooser choix = new FileChooser();
					choix.setInitialFileName(this.fichier.getName());
					File en = choix.showSaveDialog(msg.getScene().getWindow());
					if (en != null) {
						try {
							Files.copy(new FileInputStream(fichier), en.toPath(), StandardCopyOption.REPLACE_EXISTING);
						} catch (IOException e1) {
							Alerte ex = Alerte.exceptionLevee(e1);
							ex.show();
						}
					}
				}
			});

			Label date = new Label(Message.DATE_FORMAT.format(getDate()));
			VBox vb = new VBox(date, msg);
			return vb;
		} catch (FileNotFoundException e1) {
			return null;
		}
	}

	@Override
	public String description() {
		return fichier.getName();
	}

}
