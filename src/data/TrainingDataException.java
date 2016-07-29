package data;

public class TrainingDataException extends Exception{
	
	public TrainingDataException(){
		super("Impossibile trovare il file specificato");
	}
	
	public TrainingDataException(String msg){
		super(msg);
	}
}
