package exception;

//classe eccezione per gestire il caso di acquisizione di un valore mancante o fuori range di un attributo
public class UnknownValueException extends Exception{
	
	/**
	 *Eccezione per gestire il caso di acqisizione di un valore mancante o fuori rinage di un attributo
	 */
	public UnknownValueException()
	{ 
		super("Errore: valore mancante o fuori range per l'attributo!");
	}
	
	/**
	 *Eccezione per gestire il caso di acqisizione di un valore mancante o fuori rinage di un attributo
	 *
	 *@param msg messaggio di errore
	 */
	public UnknownValueException(String msg)
	{
		super(msg);
	}

}