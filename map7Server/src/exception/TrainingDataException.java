package exception;

//classe eccezione per gestire il caso di acquisizione errata del TrainingSet
public class TrainingDataException extends Exception{
	
	/**
	 *Eccezione per gestire il caso di acqisizione errata del TrainingSet
	 */
	public TrainingDataException()
	{
		super("Impossibile trovare il file specificato");
	}
	
	/**
	 *Eccezione per gestire il caso di acqisizione errata del TrainingSet
	 *
	 *@param msg messaggio di errore
	 */
	public TrainingDataException(String msg)
	{
		super(msg);
	}
}
