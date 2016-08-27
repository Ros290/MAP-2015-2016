package exception;

public class DatabaseConnectionException extends Exception{
	
	public DatabaseConnectionException(){
		super("Impossibile connettersi al database specificato");
	}
	
	public DatabaseConnectionException(String msg){
		super(msg);
	}

}
