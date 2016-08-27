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

		//ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		//ObjectInputStream in = new ObjectInputStream(socket.getInputStream());	; // stream con richieste del client

		int scelta;

		do{
			System.out.println("1. Learn Decision Tree :");
			System.out.println("2. Save Decision Tree (in tree.dat) :");
			System.out.println("3. Store Decision Tree (from tree.dat) :");
			System.out.println("4. Use Decision Tree for Prediction :");
			System.out.println("5. Exit :");
			scelta = Keyboard.readInt();
			Object outputVal=null;
			String stringa = "";
			char flag;
			switch(scelta)
			{
			case 1:
				// TO DO
				//out.writeObject(new Integer(1));
				writeObject(socket,new Integer(1));
				System.out.println("Inserire il nome della tabella:");
				//out.writeObject(Keyboard.readString());
				writeObject(socket,Keyboard.readString());
				//outputVal=in.readObject();
				outputVal = readObject (socket);
				break;
				
			case 2:
				//out.writeObject(new Integer(2));
				writeObject (socket, new Integer (2));
				flag = (char)readObject(socket);
				if (flag == 'T')
				{
					System.out.println("Inserire il nome del file su cui salvare l'albero:");
					//out.writeObject(Keyboard.readString());
					writeObject(socket, Keyboard.readString());
				}
				//outputVal=in.readObject();
				outputVal = readObject (socket);
				break;
				
			case 3:
				//out.writeObject(new Integer(3));
				writeObject (socket, new Integer (3));
				System.out.println("Inserire il nome del file da cui caricare l'albero:");
				//out.writeObject(Keyboard.readString());
				writeObject (socket, Keyboard.readString());
				//outputVal=in.readObject();
				outputVal = readObject (socket);
				break;
				
			case 4:
				writeObject (socket, new Integer (4));
				flag = (char)readObject(socket);
				if (flag != 'F')
				{
					stringa = new String ((String) readObject (socket));
					while (!stringa.equals("trovato"))
					{	
						System.out.println(stringa);
						writeObject(socket,Keyboard.readInt());
						stringa = new String ((String) readObject (socket));
					}
				}
				outputVal = readObject (socket);
				break;
				
			case 5:
				System.out.println("USCITA");
				System.exit(0);
				break;
			default:
				outputVal=new String("Comando errato.");
			}
			System.out.println(outputVal);
		}
		while (scelta!=5);	
	}
}
