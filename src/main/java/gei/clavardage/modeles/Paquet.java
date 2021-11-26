package gei.clavardage.modeles;

import java.net.DatagramPacket;
import java.net.InetAddress;

import gei.clavardage.services.ServiceEnvoiUDP;
import gei.clavardage.services.ServiceReceptionUDP;

public abstract class Paquet {

	private DatagramPacket paquet;
	
	public Paquet(String message, InetAddress hote) {
		this.paquet = new DatagramPacket(message.getBytes(), message.length(), hote, ServiceReceptionUDP.RECEPTION_PORT);
	}
	
	public DatagramPacket getPaquet() {
		return paquet;
	}
}
