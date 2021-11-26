package gei.clavardage.modeles;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PaquetUnicast extends Paquet {

	public PaquetUnicast(String message, String hote) throws UnknownHostException {
		super(message, InetAddress.getByName(hote));
	}

}
