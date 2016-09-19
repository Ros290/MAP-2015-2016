package tree;

import data.Attribute;
import data.Data;
import data.DiscreteAttribute;

//classe che modella l'entità nodo di split relativo ad un attributo indipendente discreto
public class DiscreteNode extends SplitNode
{
	/**
	 * Costruttore di uno SplitNode con attributo indipendente di tipo discreto 
	 * 
	 * @param trainingSet collezione di training che si vuole analizzare
	 * @param beginExampleIndex indica l'esempio di partenza del sotto-insieme di training da analizzare tramite indice intero
	 * @param endExampleIndex indica l'ultimo esempio del sotto-insieme di training da analizzare tramite indice intero 
	 * @param attribute indica l'attributo di classe quale dovrà analizzare lo SplitNode, quale dovrà essere di tipo Discreto
	 */
	public DiscreteNode (Data trainingSet, int beginExampleIndex, int endExampleIndex, DiscreteAttribute attribute)
	{
		super (trainingSet, beginExampleIndex, endExampleIndex, attribute);
	}
	
	/**
	 * Popola lo SplitNode, in base al sotto-insieme di training in analisi, 
	 * categorizzando gli esempi in base all'attributo indipendente passato come parametro
	 * 
	 * @param trainingSet collezione di training che si vuole analizzare
	 * @param beginExampleIndex indica l'esempio di partenza del sotto-insieme di training da analizzare tramite indice intero
	 * @param endExampleIndex indica l'ultimo esempio del sotto-insieme di training da analizzare tramite indice intero 
	 * @param attribute indica l'attributo di classe con la quale saranno categorizzati gli esempi all'interno del sottoinsieme 
	 */
	@Override
	void setSplitInfo(Data trainingSet,int beginExampleIndex, int endExampleIndex, Attribute attribute)
	{
		Object currentSplitValue = trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());
		int start = beginExampleIndex;
		int child = 0;
		for(int i = beginExampleIndex + 1; i < endExampleIndex; i++)
		{
			if ( currentSplitValue.equals(trainingSet.getExplanatoryValue(i, attribute.getIndex())) == false)
			{
				mapSplit.add(child, new SplitInfo(currentSplitValue, start, i-1, child));
				currentSplitValue = trainingSet.getExplanatoryValue(i, attribute.getIndex());
				child ++;
				start = i;
			}
		}
		mapSplit.add(child, new SplitInfo(currentSplitValue, start, endExampleIndex, child));
	}
	
	
	/**
	 * Effettua il confronto di value con i valori di tutti i nodi figli dello splitNode in analisi, restituisce 
	 * l'identificativo del nodo figlio qualora la condizione si verifichi, altrimenti torna -1
	 * 
	 * @param value valore con cui confrontare i valori dei nodi figli di uno splitNode
	 * @return identificativo di un nodo figlio, se il valore di tale nodo corrisponde a value, altrimenti -1 
	 */
	@Override
	public int testCondition (Object value)
	{
		for (int i = 0; i < mapSplit.size(); i++)
		{
			if (value.equals(mapSplit.get(i).getSplitValue()))
				return mapSplit.get(i).numberChild;
		}
		return -1;
	}
	
	/**
	 *Restituisce in formato stringa tutto il contenuto di DiscreteNode
	 *
	 *@return Stringa con tutti gli elementi all'interno di DiscreteNode
	 */
	public String toString ()
	{	
		return "DISCRETE " + super.toString();
	}
	
	/**
	 * Effettua il confronto con un altro SplitNode, in base al risultato si può capire quale sia lo splitNode con meno Varianza
	 * 
	 * @param o SplitNode da confrontare con quello corrente
	 * @return 0 se gli SplitNode hanno stessa varianza, 1 se o ha la varianza minore, -1 altrimenti
	 */
	public int compareTo(SplitNode o) 
	{
		if(o.getVariance() == this.getVariance())
		     return 0;
		else
			if(o.getVariance() < this.getVariance())
				 return 1;
		return -1;
	}

}
