package gei.clavardage.modeles;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import javax.imageio.ImageIO;

public class Image extends Message {

	private File image;

	public Image(UUID author, File image) {
		super(author);
		this.image = ImageIO;
	}

	@Override
	public void send(Socket sock) throws IOException {
	}

}
