package tree;
import java.util.List;
import java.util.ArrayList;

import data.Attribute;
import data.Data;

public abstract class SplitNode extends Node implements Comparable<SplitNode>
{
	// Classe che colelzione informazioni descrittive dello split
	class SplitInfo
	{
		Object splitValue;
		int beginIndex;
		int endIndex;
		int numberChild;
		String comparator = "=";
		
		/**
		 * Costruttore di tipo SplitInfo, contiene tutte le informazioni di un particolare splitNode
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
		 * Costruttore di tipo SplitInfo, contiene tutte le informazioni di un particolare splitNode
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
		*@return Indice per un dato esempio del trainingSet, quale sarebbe il primo esempio del sotto-insieme
		*/
		int getBeginindex()
		{
			return beginIndex;			
		}
		
		/**
		 * Restituisce la posizione in cui finisce il sotto-insieme di training contenuto nello splitNode
		 * @return Indice per un dato esempio del trainingSet, quale sarebbe l'ultimo esempio del sotto-insieme
		 */
		int getEndIndex()
		{
			return endIndex;
		}
		
		/**
		 * Restituisce il valore di un determinato attributo indipendente cui è contenuto nel nodo in analisi
		 * @return valore contenuto nello splitNode
		 */
		Object getSplitValue()
		{
			return splitValue;
		}
		
		public String toString()
		{
			return "child " + numberChild +" split value"+comparator+splitValue + "[Examples:"+beginIndex+"-"+endIndex+"]";
		}
		
		String getComparator()
		{
			return comparator;
		}
		
	}

	private Attribute attribute;	

	//protected SplitInfo mapSplit[];
	List<SplitInfo> mapSplit = new ArrayList<SplitInfo>();
	
	protected double splitVariance;
		
	abstract void setSplitInfo(Data trainingSet,int beginExampeIndex, int endExampleIndex, Attribute attribute);
	
	abstract int testCondition (Object value);
	
	/**
	 * Costruttore dello SplitNode
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
	
	Attribute getAttribute()
	{
		return attribute;
	}
	
	double getVariance()
	{
		return splitVariance;
	}
	
	int getNumberOfChildren()
	{
		return mapSplit.size();
	}
	
	SplitInfo getSplitInfo(int child)
	{
		return mapSplit.get(child);
	}

	
	String formulateQuery()
	{
		String query = "";
		for(int i=0;i<mapSplit.size();i++)
			query += (i + ":" + attribute.getName() + mapSplit.get(i).getComparator() +mapSplit.get(i).getSplitValue().toString())+"\n";
		return query;
	}
	
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
