package map7Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import utility.Keyboard;


//public class MainTest {

	/**
	 * @param args
	 */
/*
	public MainTest(String[] args){
		
		InetAddress addr = InetAddress.getByName(args[0]);
		System.out.println("addr =" +addr);
		Socket socket = new Socket(args[0], new Integer(args[1]).intValue());
		System.out.println(socket);
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());	 // stream con richieste del client
	
		String answer="";	
		int decision=0;
		do{
			System.out.println("Scegli un opzione:");
			System.out.println("Leggi Regression Tree da db [1]");
			System.out.println("Carica Regression Tree da archivio [2]");
			System.out.println("Risposta: ");
			decision=Keyboard.readInt();
		}while(!(decision==1) && !(decision ==2));
		
		String tableName="";
		System.out.println("File name:");
		tableName=Keyboard.readString();
		try{
		
		if(decision==1)
		{
			System.out.println("Starting data acquisition phase!");
			out.writeObject(0);
			out.writeObject(tableName);
			answer=in.readObject().toString();
			if(!answer.equals("OK")){
				System.out.println(answer);
				return;
			}
			System.out.println("Starting learning phase!");
			out.writeObject(1);
		}
		else
		{
			out.writeObject(2);
			out.writeObject(tableName);
		}
		
		answer=in.readObject().toString();
		if(!answer.equals("OK")){
			System.out.println(answer);
			return;
		}
			
		
		
		// .........
		
		char risp='y';
		
		do{
			out.writeObject(3);
			
			System.out.println("Starting prediction phase!");
			answer=in.readObject().toString();
		
			
			while(answer.equals("QUERY")){
				// Formualting query, reading answer
				answer=in.readObject().toString();
				System.out.println(answer);
				int path=Keyboard.readInt();
				out.writeObject(path);
				answer=in.readObject().toString();
			}
		
			if(answer.equals("OK"))
			{ // Reading prediction
				answer=in.readObject().toString();
				System.out.println("Predicted class:"+answer);
				
			}
			else //Printing error message
				System.out.println(answer);
			
		
			System.out.println("Would you repeat ? (y/n)");
			risp=Keyboard.readChar();
				
		}while (Character.toUpperCase(risp)=='Y');
		
		}
		catch(IOException | ClassNotFoundException e){
			System.out.println(e.toString());
			
		}
	}
	
	
	
	

}
*/


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;

import utility.Keyboard;

public class MainTest {
	static final int PORT = 8080;
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	
	protected static Object readObject(Socket socket) throws ClassNotFoundException, IOException
	{
		Object o;
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		o = in.readObject();
		return o;
	}
	
	protected static void writeObject(Socket socket, Object o) throws IOException
	{
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(o);
		out.flush();
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException {

		// TO CHANGE: ip server e porta devono essere acquisiti come parametri
		InetAddress addr = InetAddress.getByName("127.0.0.1");
		System.out.println("addr = " + addr);
		Socket socket = new Socket(addr, MainTest.PORT);
		System.out.println(socket);
		
		/*
		 * La comunicazione tra Client/Server è molto semplice: si tratta di un "domanda/risposta", nel senso
		 * che ogni volta il client aspetta di riceve una richiesta dal server, dopo di che digita il comando
		 * desiderato. Da notare che prima di ricevere la domanda dal server, quest'ultimo deve mandare
		 * una flag (in questo caso un boolean), quale indica che la connessione è ancora attiva.
		 * Quindi la socket sarà così strutturata:
		 * 
		 * 		-FLAG: indica lo stato della connesione, TRUE se è attiva, altrimenti FALSE (qualora il server debba chiudere la connessione;
		 * 		
		 * 		-DOMANDA: generalmente in formato String, mostrera la "domanda" o la "risposta + domanda" (se si nota nel server, 
		 * 		il risultato di una determinata funzione viene accodata alla domanda successiva da porre al client, questo perchè
		 * 		il client è strutturato in modo tale che ad ogni messaggio ricevuto, debba corrispondere sempre un'azione da parte 
		 * 		del client;
		 *		
		 *		-RISPOSTA: anch'esso generalmente in formato String, sarà poi compito del server scindere, eventualmente, un eventuale
		 *		tipo differente dallo string qualora fosse richiesto, solitamente corrisponde ad una opzione richiesta dal client, in 
		 *		base alle opzioni messe a disposizioni dal server.
		 */
		while (true)
		{
			//controllo FLAG
			if (!(boolean)readObject(socket))
			{
				System.out.println("Connessione terminata\n");
				break;
			}
			//stampo il messaggio/risultato/richiesta da parte del server
			System.out.println((String)readObject(socket));
			//digito l'opzione che desidero effettuare, o immeto dei dati in particolari
			writeObject(socket,Keyboard.readString());
		}
	}
}
