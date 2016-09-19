package tree;
import java.io.Serializable;

import data.Data;

//classe che modella l'entità nodo fogliare
@SuppressWarnings("serial")
public class LeafNode extends Node implements Serializable
{
	//valore dell'attributo di classe espresso nella foglia corrente
	Double predictedClassValue;
	
	/**
	 *Istanzia un oggetto invocando il costruttore della superclasse e avvalora l'attributo predictClassValue
	 *
	 * @param trainingSet collezione di training che si vuole analizzare
	 * @param beginExampleIndex indica l'esempio di partenza del sotto-insieme di training da analizzare
	 * @param endExampleIndex indica l'ultimo esempio del sotto-insieme di training da analizzare 
	 */
	public LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex)
	{
		super (trainingSet, beginExampleIndex, endExampleIndex);
		this.predictedClassValue = 0.0;
		for (int i = beginExampleIndex; i < endExampleIndex + 1; i++)
			this.predictedClassValue += trainingSet.getClassValue(i);
		this.predictedClassValue /= (endExampleIndex+1) - beginExampleIndex;
	}
	
	/**
	 * Restituisce il valore del membro predictClassVlue
	 * 
	 * @return valore del membro predictClassValue
	 */
	Double getPretictedClassValue ()
	{
		return this.predictedClassValue;
	}
	
	/**
	 * Restituisce il numero di split originati dal nodo foglia
	 * 
	 * @return numero di split originati dal nodo foglia, ovvero 0
	 */
	int getNumberOfChildren ()
	{
		return 0;
	}
	
	/**
	 *Restituisce in formato stringa tutto il contenuto di LeafNode
	 *
	 *@return Stringa con tutti gli elementi all'interno di LeafNode
	 */
	public String toString ()
	{
		return "LEAF : class = " + this.predictedClassValue + super.toString();
	}
}
