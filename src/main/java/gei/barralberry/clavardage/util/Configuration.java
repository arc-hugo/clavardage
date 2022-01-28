package gei.barralberry.clavardage.util;

import java.io.File;

public abstract class Configuration {

	public static int UDP_PORT_ENVOI = 22540;
	public static int UDP_PORT_RECEPTION = 22540;
	public static int TCP_PORT_ENVOI = 30861;
	public static int TCP_PORT_RECEPTION = 30861;

	public final static File DOSSIER_CACHE = new File(
			System.getProperty("user.home") + "/.cache/barralberry/clavardage/");
	public final static File DOSSIER_CONFIG = new File(
			System.getProperty("user.home") + "/.config/barralberry/clavardage/");
}
