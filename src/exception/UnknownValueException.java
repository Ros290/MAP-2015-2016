package exception;

public class UnknownValueException extends Exception{
	
	public UnknownValueException(){ 
		super("Errore: valore mancante o fuori range per l'attributo!");
	}
	
	public UnknownValueException(String msg){
		super(msg);
	}

}
