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
import java.util.Stack;
import java.util.UUID;
import gei.barralberry.clavardage.reseau.messages.Fichier;
import gei.barralberry.clavardage.reseau.messages.MessageAffiche;
import gei.barralberry.clavardage.reseau.messages.Texte;
import gei.barralberry.clavardage.util.Alerte;
import gei.barralberry.clavardage.util.Configuration;
import javafx.application.Platform;

public class AccesDB {

	private final static String DB_DRIVER = "jdbc:sqlite:";
	private final static File DB_PATH = new File(Configuration.DOSSIER_CACHE + "/clavardage.db");

	private final static String CREATE_TABLE_UTILISATEUR = "CREATE TABLE IF NOT EXISTS UTILISATEUR(ID UUID UNIQUE PRIMARY KEY)";
	private final static String CREATE_TABLE_MESSAGE = "CREATE TABLE IF NOT EXISTS MESSAGE("
			+ "ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + "CONTENU TEXT," + "DATE TIMESTAMP,"
			+ "FICHIER BOOLEAN," + "RECU BOOLEAN," + "UTILISATEUR UUID REFERENCES UTILISATEUR(ID) ON DELETE CASCADE)";
	private final static String CREATE_UTILISATEUR = "INSERT INTO UTILISATEUR VALUES(?)";
	private final static String ADD_MESSAGE = "INSERT INTO MESSAGE(CONTENU,DATE,FICHIER,RECU,UTILISATEUR) VALUES (?,?,?,?,?)";

	private final static String GET_UTILISATEUR = "SELECT * FROM UTILISATEUR WHERE ID=?";
	private final static String GET_DERNIERS_MESSAGES = "SELECT ID, CONTENU, DATE, FICHIER, RECU FROM MESSAGE WHERE UTILISATEUR=?";

	private static Connection conn;
	private UUID destinataire;
	private UUID local;

	public static boolean bloquerDB() {
		try {
			DB_PATH.getParentFile().mkdirs();
			DB_PATH.createNewFile();
			if (AccesDB.conn == null) {
				Class.forName("org.sqlite.JDBC");
				AccesDB.conn = DriverManager.getConnection(DB_DRIVER + DB_PATH.getAbsolutePath());
			}
		} catch (SQLException | ClassNotFoundException | IOException e) {
			return false;
		}
		return true;
	}

	public AccesDB(UUID local, UUID destinataire) throws SQLException, IOException, ClassNotFoundException {
		this.local = local;
		this.destinataire = destinataire;

		DB_PATH.getParentFile().mkdirs();
		DB_PATH.createNewFile();

		if (AccesDB.conn == null || AccesDB.conn.isClosed()) {
			Class.forName("org.sqlite.JDBC");
			AccesDB.conn = DriverManager.getConnection(DB_DRIVER + DB_PATH.getAbsolutePath());
		}

		Statement stm = conn.createStatement();
		stm.executeUpdate(CREATE_TABLE_UTILISATEUR);
		stm.executeUpdate(CREATE_TABLE_MESSAGE);

		PreparedStatement ps = conn.prepareStatement(GET_UTILISATEUR);
		ps.setString(1, this.destinataire.toString());
		ResultSet rs = ps.executeQuery();
		if (!rs.next()) {
			ps = conn.prepareStatement(CREATE_UTILISATEUR);
			ps.setString(1, this.destinataire.toString());
			ps.execute();
		}
	}

	public List<MessageAffiche> getDerniersMessages() throws SQLException {
		if (AccesDB.conn.isClosed()) {
			AccesDB.conn = DriverManager.getConnection(DB_DRIVER + DB_PATH.getAbsolutePath());
		}
		File dossierCache = new File(Configuration.DOSSIER_CACHE + "/" + destinataire);
		Stack<MessageAffiche> pile = new Stack<>();

		PreparedStatement ps = conn.prepareStatement(GET_DERNIERS_MESSAGES);
		ps.setString(1, this.destinataire.toString());

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			UUID auteur;
			if (rs.getBoolean("RECU")) {
				auteur = this.destinataire;
			} else {
				auteur = this.local;
			}
			if (rs.getBoolean("FICHIER")) {
				File fichier = new File(dossierCache + "/" + rs.getString("CONTENU"));
				if (fichier.exists()) {
					pile.push(new Fichier(auteur, fichier, rs.getTimestamp("DATE")));
				} else {
					pile.push(new Texte(auteur, "transmission du fichier " + rs.getString("CONTENU"),
							rs.getTimestamp("DATE")));
				}
			} else {
				pile.push(new Texte(auteur, rs.getString("CONTENU"), rs.getTimestamp("DATE")));
			}
		}
		return pile;
	}

	public synchronized int ajoutMessage(MessageAffiche msg) {
		try {
			if (AccesDB.conn.isClosed()) {
				AccesDB.conn = DriverManager.getConnection(DB_DRIVER + DB_PATH.getAbsolutePath());
			}
			PreparedStatement ps = AccesDB.conn.prepareStatement(ADD_MESSAGE);
			ps.setString(1, msg.description());
			ps.setTimestamp(2, new Timestamp(msg.getDate().getTime()));
			ps.setBoolean(3, msg instanceof Fichier);
			ps.setBoolean(4, msg.getAuteur().equals(this.destinataire));
			ps.setObject(5, this.destinataire);
			return ps.executeUpdate();
		} catch (SQLException e) {
			Platform.runLater(new Runnable() {
				public void run() {
					Alerte exeception = Alerte.exceptionLevee(e);
					exeception.show();
				}
			});
		}
		return 0;
	}

	public void close() throws SQLException {
		AccesDB.conn.close();
	}
}
