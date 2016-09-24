package tree;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import data.Attribute;
import data.Data;

//Classe atratta che modella l'astrazione dell'entità nodo di split (continuo o discreto) estendendo la superclasse Node
public abstract class SplitNode extends Node implements Comparable<SplitNode>
{
	// Classe che colleziona informazioni descrittive dello split
	class SplitInfo implements Serializable
	{
		// valore di tipo Object che definisce uno split
		Object splitValue;
		
		//indice che individua l'inizio del sotto-insieme di training
		int beginIndex;
		
		//indice che individua la fine del sotto-insieme di training
		int endIndex;
		
		//numero di split originati dal nodo corrente
		int numberChild;
		
		//operatore matematico che definisce il test nel nodo corrente
		String comparator = "=";
		
		/**
		 * Costruttore di tipo SplitInfo
		 * Costruttore che avvalora gli attributi di classe per split a valori discreti
		 * 
		 * @param splitValue Valore dell'attributo che sarà contenuto nel nodo Split
		 * @param beginIndex Indice che individua il "punto di partenza" del sotto-insieme di training
		 * @param endIndex Indice che individua il "punto di arrivo" del sotto-insieme di training
		 * @param numberChild identificativo del nodo Split
		 */
		SplitInfo(Object splitValue,int beginIndex,int endIndex,int numberChild)
		{
			this.splitValue = splitValue;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.numberChild = numberChild;
		}
		
		/**
		 * Costruttore di tipo SplitInfo
		 * Costruttore che avvalora gli attributi di classe per generici split
		 * 
		 * @param splitValue Valore dell'attributo che sarà contenuto nel nodo Split
		 * @param beginIndex Indice che individua il "punto di partenza" del sotto-insieme di training
		 * @param endIndex Indice che individua il "punto di arrivo" del sotto-insieme di training
		 * @param numberChild identificativo del nodo Split
		 * @param comparator operatore matematico che definisce il test nel nodo corrente ( il comparatore predefinito è " = " )
		 */
		SplitInfo(Object splitValue,int beginIndex,int endIndex,int numberChild, String comparator)
		{
			this.splitValue = splitValue;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.numberChild = numberChild;
			this.comparator = comparator;
		}
		
	   /**
		*Restituisce la posizione da cui inizia il sotto-insieme di training contenuto nello splitNode
		*
		*@return Indice per un dato esempio del trainingSet, quale sarebbe il primo esempio del sotto-insieme
		*/
		int getBeginindex()
		{
			return beginIndex;			
		}
		
		/**
		 * Restituisce la posizione in cui finisce il sotto-insieme di training contenuto nello splitNode
		 * 
		 * @return Indice per un dato esempio del trainingSet, quale sarebbe l'ultimo esempio del sotto-insieme
		 */
		int getEndIndex()
		{
			return endIndex;
		}
		
		/**
		 * Restituisce il valore dello split
		 * 
		 * @return valore contenuto nello splitNode
		 */
		Object getSplitValue()
		{
			return splitValue;
		}
		
		/**
		 *Restituisce in formato stringa, tutto il contenuto di SplitNode
		 *
		 *@return Stringa con tutti gli elementi all'interno di SplitNode
		 */
		public String toString()
		{
			return "child " + numberChild +" split value"+comparator+splitValue + "[Examples:"+beginIndex+"-"+endIndex+"]";
		}
		
		/**
		 *Restituisce il valore dell'operatore matemtico che definisce il test
		 *
		 *@return operatore matematio che definisce il test
		 */
		public String getComparator()
		{
			return comparator;
		}
		
	}

	//oggetto Attribute che modella l'attributo indipendente sul quale lo split è generato
	private Attribute attribute;	

	//memorizza gli split candidati in una struttura dati di dimensione pari ai possibili valori di test
	List<SplitInfo> mapSplit = new ArrayList<SplitInfo>();
	
	//attributo che contiene il valore di varianza a seguito del partizionamento indotto dallo split corrente
	protected double splitVariance;
		
	/**
	 * Metodo abstract per generare le informaioni necessarie per ciasuno degli split candidati in mapSplit
	 * 
	 * @param trainingSet collezione di training che si vuole analizzare
	 * @param beginExampeIndex indice di inizio del sotto-insieme di training
	 * @param endExampleIndex indice di fine del sotto-insieme di training
	 * @param attribute attributo indipendente  sul quale si definisce lo split
	 */
	abstract void setSplitInfo(Data trainingSet,int beginExampeIndex, int endExampleIndex, Attribute attribute);
	
	
	/**
	 * Metodo abstract per modellare la condizione di test
	 * 
	 * @param value valore dell'attributo che si vuole testare rispetto a tutti gli split
	 */
	abstract int testCondition (Object value);
	
	/**
	 * Costruttore dello SplitNode
	 * 
	 * @param trainingSet collezione di training che si vuole analizzare
	 * @param beginExampleIndex indica l'esempio di partenza del sotto-insieme di training da analizzare tramite indice intero
	 * @param endExampleIndex indica l'ultimo esempio del sotto-insieme di training da analizzare tramite indice intero 
	 * @param attribute indica l'attributo di classe quale dovrà analizzare lo SplitNode
	 */
	public SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute)
	{
			super(trainingSet, beginExampleIndex,endExampleIndex);
			this.attribute = attribute;
			trainingSet.sort(attribute, beginExampleIndex, endExampleIndex); // order by attribute
			setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);
						
			//compute variance
			splitVariance = 0;
			for(int i=0;i<mapSplit.size();i++)
			{
					double localVariance = new LeafNode(trainingSet, mapSplit.get(i).getBeginindex(),mapSplit.get(i).getEndIndex()).getVariance();
					splitVariance += (localVariance);
			}
	}
	
	/**
	 * Restituisce l'oggetto per l'attributo usato per lo split
	 * 
	 * @return l'oggetto per l'attributo usato per lo split
	 */
	Attribute getAttribute()
	{
		return attribute;
	}
	
	/**
	 * Restituisce l'information gain per lo split corrente
	 * 
	 * @return information gain per lo split corrente
	 */
	double getVariance()
	{
		return splitVariance;
	}
	
	/**
	 * Restituisce il numero dei rami originati nel nodo corrente
	 * 
	 * @return numero di rami originati nel nodo corrente
	 */
	int getNumberOfChildren()
	{
		return mapSplit.size();
	}
	
	/**
	 * Restituisce le informazioni per il ramo in mapSplit indicizzati da child
	 * 
	 * @param child indice del ramo di cui si vogliono conoscere le informazioni
	 * @return informazioni per il ramo in mapSplit indicizzato da child
	 */
	SplitInfo getSplitInfo(int child)
	{
		return mapSplit.get(child);
	}

	/**
	 *Restituisce in formato stringa tutte le informazioni di ciascun test
	 *Necessario per la predizione di nuovi esempi
	 *
	 *@return Stringa con tutte le informazioni di ciasun test
	 */
	String formulateQuery()
	{
		String query = "";
		for(int i=0;i<mapSplit.size();i++)
			query += (i + ":" + attribute.getName() + mapSplit.get(i).getComparator() +mapSplit.get(i).getSplitValue().toString())+"\n";
		return query;
	}
	
	/**
	 *Restituisce in formato stringa tutto il contenuto di SplitNode
	 *
	 *@return Stringa con tutti gli elementi all'interno di SplitNode
	 */
	public String toString()
	{
		String v= "SPLIT : attribute=" +attribute.getName() +" "+ super.toString()+  " Split Variance: " + getVariance()+ "\n" ;
		
		for(int i=0;i<mapSplit.size();i++)
		{
			v+= "\t"+mapSplit.get(i)+"\n";
		}
		
		return v;
	}
	
}
