
				package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import tree.RegressionTree;
import data.Data;
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
		boolean esito = false;
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
					try
					{	
						esito = false;
						nome = (String)readObject(socket);
						Data TrainingSet = new Data(nome);	
						tree = new RegressionTree(TrainingSet);														
						if( tree != null) 
						{
							tree.salva(nome);
							System.out.println("Salvataggio su file .dmp riuscito correttamente!!!");
						}	
						else
							System.out.println("Salvataggio su file .dmp non riuscito!!!");
						result = new String (tree.printRules() + tree.printTree());
						esito = true;
					}
					catch (Exception e)
					{
						result = e.toString();
					}
					
					finally
					{
						if (esito)
							writeObject (socket,"OK");
						else
							writeObject (socket,"ERR");
						writeObject (socket, result);
					}
					break;
				case 2:
					// STORE THE REGRESSION TREE FROM FILE
					try{
						esito = false;
						nome = (String)readObject(socket);
						tree = RegressionTree.carica(nome);
						System.out.println("Caricamento da file .dmp riuscito correttamente!!!");
						result = new String(tree.printRules() + tree.printTree());
						esito = true;
						
					
					}
					catch ( Exception e)
					{
						result = e.toString();
						
					}
					
					
					finally
					{
						if (esito)
							writeObject (socket,"OK");
						else
							writeObject (socket,"ERR");
						writeObject (socket, result);
					}
					
					
				break;	
					//PREDICTION
				case 3:
					try
					{
						writeObject(socket,tree.predictClass(socket).toString());
					}
					
					/*
					 * Poichè il client è "costretto" a far passare unicamente i valori che gli vengono mostrati
					 * questa eccezzione, in questo caso, è inutile
					 */
					catch (UnknownValueException e)
					{}
					break;
					
				}
			} catch (ClassNotFoundException | IOException e) {flag=false;} 
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


