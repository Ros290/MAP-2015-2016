package database;

public class EmptySetException extends Exception {
	
	public EmptySetException(){
		super("ResultSet vuoto!!!");
	}
	
	public EmptySetException(String msg){
		super(msg);
	}

}
