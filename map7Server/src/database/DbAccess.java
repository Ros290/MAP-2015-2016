package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbAccess {
	
	private static String DRIVER_CLASS_NAME = "org.gjt.mm.mysql.Driver";
	private final static String DBMS = "jdbc:mysql";
	private final static String SERVER = "localhost";
	private final static String DATABASE = "MapDb";
	private final static String PORT = "3306";
	private final static String USER_ID = "MapUser";
	private final static String PASSWORD = "map";
	private static Connection conn;
	
	/*
	public DbAccess(List<String> selectedValues){
		DbAccess.PORT = selectedValues.get(0);
		DbAccess.USER_ID = selectedValues.get(1);
		DbAccess.PASSWORD = selectedValues.get(2);
	}*/
	
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
