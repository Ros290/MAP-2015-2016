package server;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import data.Data;
import exception.TrainingDataException;
import tree.LeafNode;
import tree.RegressionTree;

class ServeOneClient extends Thread {
	private Socket socket;
	private ObjectInputStream in; // stream con richieste del client
	private ObjectOutputStream out;

	RegressionTree tree;

	public ServeOneClient(Socket s) throws IOException {
		socket = s;
		in = new ObjectInputStream(socket.getInputStream());
		out = new ObjectOutputStream(socket.getOutputStream());
		start();
	}

	public void run() {
		System.out.println("Nuovo client connesso");
		try {
			while (true) {
				try{	
					String nome = "";
					int command = ((Integer)in.readObject()).intValue();
					switch(command)
					{
					case 1: // LEARNING A REGRESSION TREE
						nome = (String)in.readObject();
						Data trainingSet = new Data(nome);
						tree = new RegressionTree(trainingSet);
						out.writeObject("Acquisito.");
						break;
					case 2: // SERIALIZE THE CURRENT REGRESSION TREE ON A FILE
						if( tree != null){
							nome = (String)in.readObject();
							tree.salva(nome);
							out.writeObject("Salvato.");}
						else{
							out.writeObject("false");
							throw new TrainingDataException();}
						break;
					case 3: // STORE THE REGRESSION TREE FROM FILE
						nome = (String)in.readObject();
						tree = RegressionTree.carica(nome);
						out.writeObject("Caricato.");
						break;
					/*	
					case 4: //USE THE CURRENT TREE TO PREDICT AN EXAMPLE
						try {
							String classValue = predictClass(tree);
							out.writeObject("Transmitting class ...");
							out.writeObject("CLASSE:" + classValue);
						}
						catch (NullPointerException e) {
							System.out.println("Prediction: Albero non esistente.");
							out.writeObject(e);
							out.writeObject("Albero non esistente.");
						}
						catch(UnknownValueException e){
							System.out.println("Prediction: Opzione scelta inesistente.");
							out.writeObject((Exception)e);
							out.writeObject("Scelta non valida.");
						}
						break;*/
					default:
						System.out.println("COMANDO INESISTENTE");
						out.writeObject("COMANDO INESISTENTE");
					}// END SWITCH
				} catch(IOException e){
					if (e instanceof FileNotFoundException)
						try {
							out.writeObject(e.toString());
						} catch (IOException e1) {
							break;
						}
					else {
						break;}
				} catch(ClassNotFoundException e){

				} catch (TrainingDataException e1) {
					try {
						out.writeObject(e1.toString());
					} catch (IOException e) {
						break;
					}
				}
			}
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Socket non chiuso!");
			}
		}
	}

	/*
	private String predictClass(RegressionTree tree)throws UnknownValueException, ClassNotFoundException, IOException{
		if (tree.getRoot() instanceof LeafNode)
			return ((LeafNode) tree.getRoot()).getPredictedClassValue();
		else {
			int risp;
			out.writeObject((((SplitNode)tree.getRoot()).formulateQuery()));
			risp = ((Integer)in.readObject()).intValue();
			if (risp == -1 || risp>=tree.getRoot().getNumberOfChildren())
				throw new UnknownValueException();
			else {
				return predictClass(tree.subTree(risp));
			}
		}
	}
	*/
}

