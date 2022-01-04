package gei.barralberry.clavardage.donnees;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import gei.barralberry.clavardage.reseau.messages.Fichier;
import gei.barralberry.clavardage.reseau.messages.MessageAffiche;
import gei.barralberry.clavardage.reseau.messages.Texte;

public class AccesDB {

	private final static String DB_DRIVER = "jdbc:sqlite:";
	private final static File DB_PATH = new File(System.getProperty("user.home")+"/.config/clavardage/clavardage.db");
	
	private final static String CREATE_TABLE_UTILISATEUR = "CREATE TABLE IF NOT EXISTS UTILISATEUR(ID UUID PRIMARY KEY)";
	private final static String CREATE_TABLE_MESSAGE = "CREATE TABLE IF NOT EXISTS MESSAGE("
			+ "ID INT AUTO_INCREMENT UNIQUE NOT NULL PRIMARY KEY,"
			+ "CONTENU TEXT,"
			+ "DATE TIMESTAMP,"
			+ "FICHIER BOOLEAN,"
			+ "RECU BOOLEAN,"
			+ "UTILISATEUR UUID REFERENCES UTILISATEUR(ID) ON DELETE CASCADE)";
	private final static String CREATE_UTILISATEUR = "INSERT INTO UTILISATEUR VALUES(?)";
	private final static String ADD_MESSAGE = "INSERT INTO MESSAGE(CONTENU, DATE, FICHIER, RECU, UTILISATEUR) VALUES (?,?,?,?,?)";
	
	private final static String GET_UTILISATEUR = "SELECT * FROM UTILISATEUR WHERE ID=?";
	private final static String GET_DERNIERS_MESSAGES = "SELECT ID, CONTENU, DATE, FICHIER, RECU FROM MESSAGE WHERE UTILISATEUR=? ORDER BY ID DESC LIMIT ?";
	
	
	private Connection conn;
	private UUID destinataire;
	private UUID local;
	
	public AccesDB(UUID destinataire, UUID local) throws SQLException, IOException, ClassNotFoundException {
		this.destinataire = destinataire;
		Class.forName("org.sqlite.JDBC");
		
		DB_PATH.getParentFile().mkdirs();
		boolean nouveau = DB_PATH.createNewFile();
		
		this.conn = DriverManager.getConnection(DB_DRIVER+DB_PATH.getAbsolutePath());
		
		if (nouveau) {
			Statement stm = conn.createStatement();
			stm.executeUpdate(CREATE_TABLE_UTILISATEUR);
			stm.executeUpdate(CREATE_TABLE_MESSAGE);
		}
		
		PreparedStatement ps = conn.prepareStatement(GET_UTILISATEUR);
		ps.setString(1, this.destinataire.toString());
		ResultSet rs = ps.executeQuery();
		if (!rs.next()) {
			ps = conn.prepareStatement(CREATE_UTILISATEUR);
			ps.setString(1, this.destinataire.toString());
			ps.execute();
		}
	}
	
	public List<MessageAffiche> getDerniersMessages(int max) throws SQLException {
		List<MessageAffiche> list = new Vector<>();
		
		PreparedStatement ps = conn.prepareStatement(GET_DERNIERS_MESSAGES);
		ps.setString(1, this.destinataire.toString());
		ps.setInt(2, max);
		
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			UUID auteur;
			if (rs.getBoolean("RECU")) {
				auteur = this.destinataire;
			} else {
				auteur = this.local;
			}
			String contenu;
			if (rs.getBoolean("FICHIER")) {
				contenu = "transmission du fichier "+rs.getString("CONTENU");
			} else {
				contenu = rs.getString("CONTENU");
			}
			list.add(new Texte(auteur, contenu, rs.getTimestamp("DATE")));
		}
		
		return list;
	}
	
	
	public int ajoutMessage(MessageAffiche msg) throws SQLException {
		PreparedStatement ps = this.conn.prepareStatement(ADD_MESSAGE);
		ps.setString(1, msg.description());
		ps.setTimestamp(2, new Timestamp(msg.getDate().getTime()));
		// TODO image
		ps.setBoolean(3, msg instanceof Fichier);
		ps.setBoolean(4, msg.getAuteur().equals(this.destinataire));
		ps.setObject(5, this.destinataire);
		return ps.executeUpdate();
	}

	public void close() throws SQLException {
		this.conn.close();
	}
}
