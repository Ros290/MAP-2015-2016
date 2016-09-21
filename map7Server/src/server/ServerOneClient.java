package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

import tree.RegressionTree;
import data.Data;
import exception.DatabaseConnectionException;
import exception.EmptySetException;
import exception.TrainingDataException;
import exception.UnknownValueException;

class ServeOneClient extends Thread {
	
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
    
	RegressionTree tree;

	public ServeOneClient(Socket s) throws IOException {
		socket = s;
		in = new ObjectInputStream(socket.getInputStream());
	    out = new ObjectOutputStream(socket.getOutputStream());
		start();
	}
	
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
	
	
	public void run()
	{
		String result = "";
		String nome = "";
		Object o;
		boolean flag=true;
		
		while (flag) 
		{
			try {
				o = readObject(socket);
				switch((int)o){
				case 0:	
					writeObject(socket,"OK");
					nome = (String)readObject(socket);
					break;							
				
				case 1:
					// LEARNING A REGRESSION TREE AND SERIALIZE THE CURRENT REGRESSION TREE ON A FILE
					try{					
						nome = (String)readObject(socket);
						Data TrainingSet = new Data(nome);	
						tree = new RegressionTree(TrainingSet);
						writeObject(socket,"OK");
						result = new String (tree.printRules() + tree.printTree());
																				
					if( tree != null) {
						tree.salva(nome);
						System.out.println("Salvataggio su file .dmp riuscito correttamente!!!");
					}	
					else
						System.out.println("Salvataggio su file .dmp non riuscito!!!");
					}
					
					catch(IOException | ClassNotFoundException e){
						e.printStackTrace();
					} catch (EmptySetException e){
						
						System.out.println("ResultSet vuoto!!!");
					}
					catch (SQLException e){
						System.out.println("Errore database: tabella inesistente!!!");
					}
					catch(DatabaseConnectionException e)
					{
						System.out.println("Impossibile connettersi al database specificato");
					}
					catch(TrainingDataException e)
					{
						System.out.println("Impossibile trovare il file specificato");
					}
					writeObject(socket,result);
			       break;
				
				case 2:
					// STORE THE REGRESSION TREE FROM FILE
					try{
						nome = (String)readObject(socket);
						tree = RegressionTree.carica(nome);
						System.out.println("Caricamento da file .dmp riuscito correttamente!!!");
						writeObject(socket,"OK"); 
						result = new String(tree.printRules() + tree.printTree());
					}
					catch(IOException | ClassNotFoundException e){
						
					} 
					
					writeObject(socket,result);
				break;	
					
				case 3:
					//USE THE CURRENT TREE TO PREDICT AN EXAMPLE
					writeObject(socket,tree.predictClass(socket).toString());
					break;
					
				}
			} catch (ClassNotFoundException e) {flag=false;} 
			  catch (IOException e) { flag=false;}
			catch (UnknownValueException e) {flag = false;}
		}
		try{
			socket.close();
			System.out.println("Server disponbile per un nuovo Client");
		} 
		catch (IOException e) 
		{
			System.out.println("Socket non chiuso!");
		}
	}
}


