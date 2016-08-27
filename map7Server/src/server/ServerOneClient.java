package server;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import data.Data;
import exception.TrainingDataException;
import exception.UnknownValueException;
import tree.LeafNode;
import tree.RegressionTree;

class ServeOneClient extends Thread {
	private Socket socket;

	RegressionTree tree;

	public ServeOneClient(Socket s) throws IOException {
		socket = s;
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

	public void run() {
		System.out.println("Nuovo client connesso");
		try {
			while (true) {
				try{	
					String nome = "";
					//int command = ((Integer)in.readObject()).intValue();
					int command = ((Integer)readObject(socket)).intValue();
					switch(command)
					{
					case 1: // LEARNING A REGRESSION TREE
						//nome = (String)in.readObject();
						nome = (String) readObject (socket);
						Data trainingSet = new Data(nome);
						tree = new RegressionTree(trainingSet);
						//out.writeObject("Acquisito.");
						writeObject(socket,tree.printRules() + tree.printTree());
						break;
					case 2: // SERIALIZE THE CURRENT REGRESSION TREE ON A FILE
						if( tree != null)
						{
							writeObject (socket,'T');
							//nome = (String)in.readObject();
							nome = (String)readObject(socket);
							tree.salva(nome);
							//out.writeObject("Salvato.");
							writeObject(socket,"Salvato");
						}
						else
						{
							//out.writeObject("false");
							writeObject(socket,'F');
							throw new TrainingDataException();
						}
						break;
					case 3: // STORE THE REGRESSION TREE FROM FILE
						//nome = (String)in.readObject();
						nome = (String) readObject(socket);
						tree = RegressionTree.carica(nome);
						//out.writeObject("Caricato.");
						writeObject(socket,tree.printRules() + tree.printTree());
						break;
					
					case 4: //USE THE CURRENT TREE TO PREDICT AN EXAMPLE
						try
						{
						if (tree != null)
						{
							writeObject (socket, 'T');
							writeObject(socket,tree.predictClassServer(socket));
						}
						else
						{
							writeObject(socket,'F');
							throw new TrainingDataException();
						}
						}
						catch (UnknownValueException e)
						{
							writeObject(socket,e.toString());
						}
						break;
					default:
						System.out.println("COMANDO INESISTENTE");
						//out.writeObject("COMANDO INESISTENTE");
						writeObject (socket, "COMANDO INESISTENTE");
					}// END SWITCH
				} 
				catch(IOException e)
				{
					if (e instanceof FileNotFoundException)
						try 
						{
							//out.writeObject(e.toString());
							writeObject(socket,e.toString());
						} 
						catch (IOException e1) 
						{
							break;
						}
					else 
						break;
				} 
				catch(ClassNotFoundException e)
				{} 
				catch (TrainingDataException e1) 
				{
					try 
					{
						//out.writeObject(e1.toString());
						writeObject(socket,e1.toString());
					} 
					catch (IOException e) 
					{
						break;
					}
				}
			}
		} 
		finally 
		{
			try 
			{
				socket.close();
			} 
			catch (IOException e) 
			{
				System.out.println("Socket non chiuso!");
			}
		}
	}

}

