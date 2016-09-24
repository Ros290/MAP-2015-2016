package exception;

//classe eccezione che modella la restituzione di un resultSet vuoto 
public class EmptySetException extends Exception {
	
	public EmptySetException(){
		super("ResultSet vuoto!!!");
	}
	
	public EmptySetException(String msg){
		super(msg);
	}

}
