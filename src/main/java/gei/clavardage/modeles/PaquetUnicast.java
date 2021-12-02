package gei.clavardage.modeles;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PaquetUnicast extends Paquet {

	public PaquetUnicast(String message, InetAddress adresse) throws UnknownHostException {
		super(message, adresse);
	}

}
