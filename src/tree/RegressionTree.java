package tree;
import data.Data;
import data.DiscreteAttribute;
import exception.UnknownValueException;
import utility.Keyboard;

/*
 * Struttura ad albero, ogni root può contenere o uno SplitNode (sotto-albero di profondità 1) o un nodo foglia.
 * Qualora root avesse dei nodi figli, essi saranno definiti ricorsivamente come RegressionTree nel campo childTree
 */
public class RegressionTree 
{
	Node root;
	RegressionTree childTree[];
	
	RegressionTree ()
	{
		this.childTree = new RegressionTree [1];
	}
	
	/**
	 * definisce regressionTree come albero contenente i dati all'interno di Data
	 * @param trainingSet collezione di esempi che si vuole riportare tramite RegressionTree
	 */
	public RegressionTree (Data trainingSet)
	{
		learnTree (trainingSet, 0, trainingSet.getNumberOfExamples()-1, (trainingSet.getNumberOfExamples() * 10)/100);
		/*
		 * il limite di figli per ogni nodo è pari al 10% del numero totale di tutti gli esempi all'interno della collezione
		 */
	}
	
	boolean isLeaf(Data trainingSet,int begin, int end,int numberOfExamplesPerLeaf)
	{
		return ((end - begin)<= numberOfExamplesPerLeaf);
	}
	
	/**
	 * determina il "miglior" splitNode definibile con gli esempi compresi tra gli indici begin ed end nella collezione
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
		 * le classi indipendenti che definiscono la collezione. comincio a definire il primo splitNode col primo attributo indipendente
		 * e lo assumo come splitNodePreferito, inteso come il miglior splitnode fin'ora trovato
		 */
		DiscreteNode splitNodoPreferito = new DiscreteNode (trainingSet, begin, end, (DiscreteAttribute)trainingSet.getExplanatoryAttribute(0));
		for (int i = 1; i < trainingSet.getNumberOfExplanatoryAttributes(); i++)
		{
			/*
			 * definisco lo splitNodeCandidato, ovvero uno splitNode definito con un altro attributo indipendente, e lo 
			 * confronterò con lo splitNodePreferito
			 */
			DiscreteNode splitNodoCandidato = new DiscreteNode (trainingSet, begin, end, (DiscreteAttribute)trainingSet.getExplanatoryAttribute(i));
			/*
			 * confronto i due splitNode tramite le loro varianze, quello con la varianza migliore allora
			 * sarà il miglior splitNode
			 */
			if (splitNodoCandidato.getVariance() < splitNodoPreferito.getVariance())
				splitNodoPreferito = splitNodoCandidato;
		}
		
		/*
		 * Poichè ad ogni nuovo splitNode definito, l'ordine degli esempi presenti nel trainingSet varia
		 * allora svolgo un nuovo ordinamento, in base all'attributo dello splitNode che è stato scelto
		 * così da poter garantire una corretta esecuzione qualora si necessitase di definire ultieriori
		 * splitnode nel sottoinsieme del trainingSet
		 */
		trainingSet.sort(splitNodoPreferito.getAttribute(), begin, end);
		return splitNodoPreferito;
	}
	
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
		

		public void printRules()
		{
			System.out.println("********* RULES ***********\n");
			System.out.println(printRules2(""));
			System.out.println("**************************\n");
		}
		
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
					rule2 += childTree[i].printRules2( rule + sn.getAttribute().getName() + " = " + sn.getSplitInfo(i).getSplitValue().toString());
				}
			}
			return rule2;
		}
		
		public void printTree()
		{
			System.out.println("********* TREE **********\n");
			System.out.println(toString());
			System.out.println("*************************\n");
		}
		
		/**
		 * consente all'utente di percorrere in profondità l'albero di decisione, seguendo il percorso
		 * a proprio piacimento
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
				int choice = Keyboard.readInt();
				if ((choice < 0) || (choice >= childTree.length))
					throw new UnknownValueException("The answer should be an integer between 0 and "+(childTree.length-1)+"!");
				//ricevuta la scelta (choice) dall'utente, procedo con la predizione analizzando il figlio scelto
				return childTree[choice].predictClass();
			}
		}
		
		
}