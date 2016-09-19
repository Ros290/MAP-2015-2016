package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import exception.DatabaseConnectionException;

//classe che realizza l'accesso alla base di dati
public class DbAccess {
	
	private static String DRIVER_CLASS_NAME = "org.gjt.mm.mysql.Driver";
	private final static String DBMS = "jdbc:mysql";
	
	//identificativo del server su cui risiede la base di dati
	private final static String SERVER = "localhost";
	
	//nome della base di dati
	private final static String DATABASE = "MapDb";
	
	//porta su cui il DBMS MySQL accetta le connessioni
	private final static String PORT = "3306";
	
	//nome utente per l'accesso alla base di dati
	private final static String USER_ID = "MapUser";
	
	//password di autenticazione per l'utente dentificato da USER_ID 
	private final static String PASSWORD = "map";
	
	//gestisce una connessione
	private static Connection conn;
	
	public static void initConnection() throws DatabaseConnectionException, ClassNotFoundException {
		String url = DBMS+"://" + SERVER + ":" + PORT + "/" + DATABASE;
		Class.forName(DRIVER_CLASS_NAME);
		
		try { 
			conn = DriverManager.getConnection(url, USER_ID, PASSWORD);
		}
		catch (SQLException e) { 
			throw new DatabaseConnectionException("Errore! - Impossibile Connettersi al Database!");
		}
	}
	
	public static Connection getConnection(){
		return conn;
	}
	
	public void closeConnection() throws SQLException{
		conn.close();
	}

}
