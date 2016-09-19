package exception;

//classe eccezione che modella il fallimento della connesione al database 
public class DatabaseConnectionException extends Exception{
	
	/**
	 *Eccezione per gestire il caso di fallimento della connessione al database
	 */
	public DatabaseConnectionException()
	{
		super("Impossibile connettersi al database specificato");
	}
	
	/**
	 *Eccezione per gestire il caso di acqisizione errata del TrainingSet
	 *
	 *@param msg messaggio di errore
	 */
	public DatabaseConnectionException(String msg)
	{
		super(msg);
	}

}
