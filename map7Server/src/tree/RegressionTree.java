package tree;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Scanner;
import java.util.TreeSet;

import data.Attribute;
import data.ContinuousAttribute;
import data.Data;
import data.DiscreteAttribute;
import exception.UnknownValueException;

/*
 * Struttura ad albero, ogni root può contenere o uno SplitNode (sotto-albero di profondità 1) o un nodo foglia.
 * Qualora root avesse dei nodi figli, essi saranno definiti ricorsivamente come RegressionTree nel campo childTree
 * Classe che modella l'entità albero di decisione come insieme di sotto-alberi
 */
@SuppressWarnings("serial")
public class RegressionTree implements Serializable 
{
	//radice del sotto-albero corrente
	Node root;
	
	//array di sotto-alberi originati nel nodo root
	//vi è un array per ogni livello di profondità nell'intero albero
	RegressionTree childTree[];
	
	/**
	 *Istanzia un sotto-albero dell'intero albero
	 */
	RegressionTree ()
	{
		this.childTree = new RegressionTree [1];
	}
	
	/**
	 * Istanzia un sotto-albero dell'intero albero ed avvia l'induzione dell'albero dagli esempi di training in input
	 * Definisce regressionTree come albero contenente i dati all'interno di Data
	 * 
	 * @param trainingSet collezione di esempi che si vuole riportare tramite RegressionTree
	 */
	public RegressionTree (Data trainingSet)
	{
		learnTree (trainingSet, 0, trainingSet.getNumberOfExamples()-1, (trainingSet.getNumberOfExamples() * 10)/100);
		/*
		 * il limite di figli per ogni nodo è pari al 10% del numero totale di tutti gli esempi all'interno della collezione
		 */
	}
	
	/**
	 * Serializza l’oggetto riferito da this nel file il cui nome è passato come parametro.
	 * 
	 * @param nomeFile               nome del file
	 * @throws FileNotFoundException se il file non esiste
	 * @throws IOException           se le operazioni di I/O sono fallite o interrotte
	 */
	public void salva(String nomeFile) throws FileNotFoundException, IOException 
	{
		FileOutputStream outFile = new FileOutputStream(nomeFile);
		ObjectOutputStream outStream = new ObjectOutputStream(outFile);
		outStream.writeObject(this);
		outStream.close();		
	}
	
	/**
	 * Legge e restituisce l’oggetto come è memorizzato nel file il cui nome è passato come parametro.
	 * 
	 * @param nomeFile                nome del file
	 * @return                        oggetto memorizzato nel file
	 * @throws FileNotFoundException  se il file non esiste
	 * @throws IOException            se le operazioni di I/O sono fallite o interrotte
	 * @throws ClassNotFoundException se si verifica un type mismatch
	 */
	public static RegressionTree carica(String nomeFile) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		FileInputStream inFile = new FileInputStream(nomeFile);
		ObjectInputStream inStream = new ObjectInputStream(inFile);
		RegressionTree regT = (RegressionTree) inStream.readObject();
		inStream.close();
		return regT;
	}


	/**
	 * Verifica se il sotto-insieme corrente può essere coperto da un nodo foglia 
	 * controllando la cardinalità di tale sotto-insieme
	 * 
	 * @param trainingSet             trainingSet da anlizzare
	 * @param begin					  indica l'esempio di partenza del sotto-insieme di training da analizzare 
	 * @param end					  indica l'ultimo esempio del sotto-insieme di training da analizzare
	 * @param numberOfExamplesPerLeaf indica il numero minimo che una foglia deve contenere 
	 * @return esito sulle condiioni per i nodi fogliari
	 */
	boolean isLeaf(Data trainingSet,int begin, int end,int numberOfExamplesPerLeaf)
	{
		return ((end - begin)<= numberOfExamplesPerLeaf);
	}
	
	/**
	 * Determina il "miglior" splitNode definibile con gli esempi compresi tra gli indici begin ed end nella collezione
	 * 
	 * @param trainingSet collezione degli esempi
	 * @param begin indice della sotto-collezione da cui si vuole definire lo splitNode desiderato
	 * @param end indice della sotto-collezione entro cui si vuole definire lo splitNode desiderato
	 * @return ritorna il "miglior SplitNode definibile per la sotto-collezione passata in parametro
	 */
	SplitNode determineBestSplitNode(Data trainingSet,int begin,int end)
	{
		/*
		 * è molto semplice, ogni splitNode definito fa riferimento ad un particolare attributo indipendente,
		 * perciò quello che viene fatto qui è definire TUTTI i possibili splitNode definibili, quindi se ne creano tanti quanti sono
		 * le classi indipendenti che definiscono la sotto-collezione. lo SplitNode che ha la varianza minore rispetto agli altri,
		 * allora sarà il "miglior" splitNode ricavabile dalla sotto-collezione
		 */
		
		/*
		 * Il TreeSet permette di ordinare automaticamente, ovvero ad ogni nuova aggiunta (add) di un elemento, l'unica
		 * premessa è che la classe quale sarà contenuta nel treeSet (in questo caso "SplitNode") deve
		 * fare l'override della funzione compareTo , implementandolo dalla classe Comparator <SplitNode>
		 */
		TreeSet<SplitNode> ts = new TreeSet<SplitNode>();	
		for (int i = 0; i < trainingSet.getNumberOfExplanatoryAttributes(); i++)
		{
			Attribute a = trainingSet.getExplanatoryAttribute(i);
			SplitNode currentNode;
			if (a instanceof DiscreteAttribute)
				currentNode = new DiscreteNode (trainingSet, begin, end , (DiscreteAttribute)trainingSet.getExplanatoryAttribute(i));
			else
				currentNode = new ContinuousNode (trainingSet, begin, end , (ContinuousAttribute)trainingSet.getExplanatoryAttribute(i));
			ts.add(currentNode);
			//ts.add(new DiscreteNode (trainingSet, begin, end, (DiscreteAttribute)trainingSet.getExplanatoryAttribute(i)));
		}
		
		/*
		 * Poichè ad ogni nuovo splitNode definito, l'ordine degli esempi presenti nel trainingSet varia.
		 * allora svolgo un nuovo ordinamento, in base all'attributo dello splitNode che è stato scelto
		 * così da poter garantire una corretta esecuzione qualora si necessitase di definire ultieriori
		 * splitnode nel sottoinsieme del trainingSet (semplicemente, provate a non fare l'ordinamento e
		 * guardacaso gli splitNode saranno "sballati" da come ci si dovrebbe aspettare!)
		 * 
		 * Nel caso del treeSet, il "miglior" splitNode è quello più "piccolo" (inteso in base alla sua varianza 
		 * rispetto a quello degli altri "candidati")
		 */
		
		trainingSet.sort(ts.first().getAttribute(), begin, end);
		return ts.first();
	}
	
		/**
		 * Costruttore di alberi di regressione in maniera ricorsiva (definisce 
		 * il sotto-albero di prondità 1 e definisce gli eventuali sotto-alberi dei suoi figli, e 
		 * così via dicendo dei figli dei figli...). 
		 * 
		 * @param trainingSet collezione di esempi che si vuole riportare all'interno di un albero di regressione
		 * @param begin indice della sotto-collezione da cui si vuole definire lo splitNode desiderato
		 * @param end indice della sotto-collezione entro cui si vuole definire lo splitNode desiderato
		 * @param numberOfExamplesPerLeaf definisce il limite massimo di figli che può avere un padre
		 */
		private void learnTree(Data trainingSet,int begin, int end,int numberOfExamplesPerLeaf)
		{
			if( isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf))
			{
				//determina la classe che compare più frequentemente nella partizione corrente
				root=new LeafNode(trainingSet,begin,end);
			}
			else //split node
			{
				root=determineBestSplitNode(trainingSet, begin, end);
				if(root.getNumberOfChildren()>1)
				{
					childTree=new RegressionTree[root.getNumberOfChildren()];
					for(int i=0;i<root.getNumberOfChildren();i++)
					{
						childTree[i]=new RegressionTree();
						childTree[i].learnTree(trainingSet, ((SplitNode)root).getSplitInfo(i).beginIndex, ((SplitNode)root).getSplitInfo(i).endIndex, numberOfExamplesPerLeaf);
					}
				}
				else
					root=new LeafNode(trainingSet,begin,end);
			}
		}
		
		/**
		 *Restituisce in formato stringa tutto il contenuto di RegressionTree
		 *
		 *@return Stringa con tutti gli elementi all'interno di RegressionTree
		 */
		public String toString()
		{
			String tree=root.toString()+"\n";
			
			if( root instanceof LeafNode)
			{
			
			}
			else //split node
			{
				for(int i=0;i<childTree.length;i++)
					tree +=childTree[i];
			}
			return tree;
		}
		
		/**
		 *Restituisce in formato stringa tutte le regole dell'albero di rgressione 
		 *
		 *@return Stringa con tutte le regole dell'albero di regressione
		 */
		public String printRules()
		{
			String x = "";
			x += "********* RULES ***********\n";
			x += printRules2("");
			x += "**************************\n";
			return x;
		}
		
		/**
		 *Restituisce in formato stringa tutte le regole dell'albero di rgressione 
		 *
		 *@param rule informazioni del nodo di split del sotto-albero al livello superiore
		 *@return Stringa con tutte le regole dell'albero di regressione
		 */
		private String printRules2(String rule)
		{
			String rule2 = "";
			if (root instanceof LeafNode)
			{
				LeafNode lf = (LeafNode) root;
				rule2 += rule + " ==> " + "Class: " + lf.getPretictedClassValue() + "\n";
			}
			else
			{
				SplitNode sn = (SplitNode) root;
				for (int i = 0; i < root.getNumberOfChildren(); i++)
				{
					if (!rule.equals("") && i == 0)
						rule += " AND ";
					rule2 += childTree[i].printRules2( rule + sn.getAttribute().getName() + sn.getSplitInfo(i).getComparator() + sn.getSplitInfo(i).getSplitValue().toString());
				}
			}
			return rule2;
		}
		
		/**
		 *Stampa l'albero di regressione 
		 *
		 *@return l'albero di regressione
		 */
		public String printTree()
		{
			String x = "";
			x += "********* TREE **********\n";
			x += toString();
			x += "*************************\n";
			return x;
		}
		
		/**
		 * Consente all'utente di percorrere in profondità l'albero di decisione, seguendo il percorso
		 * a proprio piacimento
		 * Visualizza le informazioni di ciasuno split dell'albero
		 * e per il corrispondente attributo acquisisce il valore dell'esempio da predire da tastiera
		 * Se il nodo root corrente è leaf allora termina l'acquisizione e visualizza la predizione per l'attributo di classe, 
		 * altrimenti invoca riscorsivamente sul figlio di root in childTree, in base al valore acquisito da tastiera
		 * @return ritorna il risultato della predizione
		 * @throws UnknownValueException
		 */
		public Double predictClass() throws UnknownValueException
		{
			//controllo che tipo è root, se è foglia allora si è giunto a coclusione e ritorna l'attributo di classe trovato
			if (root instanceof LeafNode)
			{
				LeafNode lf = (LeafNode) root;
				return lf.predictedClassValue;
			}
			//altrimeni è splitNode
			else
			{
				SplitNode sn = (SplitNode) root;
				//mostro quali sono i percorsi possibili per continuare la predizione
				System.out.println(sn.formulateQuery());
				Scanner input = new Scanner(System.in);
				int choice = input.nextInt();
				if ((choice < 0) || (choice >= childTree.length))
					throw new UnknownValueException("The answer should be an integer between 0 and "+(childTree.length-1)+"!");
				//ricevuta la scelta (choice) dall'utente, procedo con la predizione analizzando il figlio scelto
				return childTree[choice].predictClass();
			}
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
		
		public Double predictClass(Socket socket) throws UnknownValueException
		{
			try
			{
			//controllo che tipo è root, se è foglia allora si è giunto a coclusione e ritorna l'attributo di classe trovato
			if (root instanceof LeafNode)
			{
				LeafNode lf = (LeafNode) root;
				writeObject(socket,"OK");
				return lf.predictedClassValue;
			}
			//altrimeni è splitNode
			else
			{
				SplitNode sn = (SplitNode) root;
				//mostro quali sono i percorsi possibili per continuare la predizione
				writeObject(socket,"QUERY");
				writeObject(socket,sn.formulateQuery());
				int choice = (int)readObject(socket);
				if ((choice < 0) || (choice >= childTree.length))
					throw new UnknownValueException("The answer should be an integer between 0 and "+(childTree.length-1)+"!");
				//ricevuta la scelta (choice) dall'utente, procedo con la predizione analizzando il figlio scelto
				return childTree[choice].predictClass(socket);
			}
			}
			catch (IOException e)
			{
			}
			catch (ClassNotFoundException e)
			{
			}
			return 0.0;
		}
		
		
}