package gei.clavardage.modeles;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class Image extends Message {

	private File image;

	public Image(UUID author, File image) {
		super(author);
		this.image = image;
	}

	@Override
	public void send(Socket sock) throws IOException {
	}

}
