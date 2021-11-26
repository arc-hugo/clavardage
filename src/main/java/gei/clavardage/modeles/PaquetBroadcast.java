package gei.clavardage.modeles;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PaquetBroadcast extends Paquet {

	public PaquetBroadcast(String message) throws UnknownHostException {
		super(message, InetAddress.getByName("255.255.255.255"));
	}

}
