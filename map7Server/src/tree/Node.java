package tree;
import java.io.Serializable;

import data.Data;

//Classe che modella l'astrazione dell'entità nodo dell'albero di decisione
@SuppressWarnings("serial")
abstract class Node implements Serializable
{
	//contatore dei nodi generati nell'albero
	private static int idNodeCount = 0;
	
	//identificativo numerico del nodo
	private int idNode;
	
	//indice che individua l'inizio del sotto-insieme di training
	private int beginExampleIndex;
	
	//indice che individua la fine del sotto-insieme di training
	private int endExampleIndex;
	
	//valore della varianza calcolata, rispetto all'attributo di classe, nel sotto-insieme di training del nodo
	private double variance;
	
	/**
	 * Costruttore della classe Node
	 * Conterrà la varianza calcolato rispetto agli attributi di classe presenti nel sotto-insieme di training.
	 * 
	 * @param trainingSet viene passato l'intero insieme di training, da cui si ricaverà il sotto-insieme in base agli indici passati in parametro
	 * @param beginExampleIndex Indice che individua il "punto di partenza" del sotto-insieme di training
	 * @param endExampleIndex Indice che individua il "punto di arrivo" del sotto-insieme di training
	 */
	Node (Data trainingSet, int beginExampleIndex, int endExampleIndex)
	{
		this.beginExampleIndex = beginExampleIndex;
		this.endExampleIndex = endExampleIndex;
		//NON è stato specifica come definire l'id del nodo, quindi ho fatto un po di fantasia
		this.idNode = (this.endExampleIndex - this.beginExampleIndex) * this.endExampleIndex;
		Double somm1 = 0.0;
		Double somm2 = 0.0;
		for (int i = this.beginExampleIndex; i < this.endExampleIndex +1; i++)
		{
			Double x = trainingSet.getClassValue(i);
			somm1 += x*x;
			somm2 += x;
		}
		this.variance = somm1 - ((somm2 * somm2)/((this.endExampleIndex+1) - this.beginExampleIndex));
	}
	
	/**
	 * Restituisce il valore del membro idNode
	 * Restituisce l'ID univoco del dato Nodo
	 * 
	 * @return ID del nodo
	 */
	int getIdNode ()
	{
		return this.idNode;
	}
	
	/**
	 * Restituisce la posizione da cui inizia il sotto-insieme di training contenuto nel Nodo
	 * 
	 * @return Indice per un dato esempio del trainingSet, quale sarebbe il primo esempio del sotto-insieme
	 */
	int getBeginExampleIndex ()
	{
		return this.beginExampleIndex;
	}

	/**
	 * Restituisce la posizione in cui finisce il sotto-insieme di training contenuto nel Nodo
	 * 
	 * @return Indice per un dato esempio del trainingSet, quale sarebbe l'ultimo esempio del sotto-insieme
	 */
	int getEndExampleIndex ()
	{
		return this.endExampleIndex;
	}
	
	/**
	 * Restituisce la varianza che è stata ricavata in base agli attributi di classe appartenenti al sotto-insieme di training
	 * 
	 * @return Varianza degli esempi coperti dal Nodo
	 */
	double getVariance ()
	{
		return this.variance;
	}
	
	/**
	 * Indica il numero di nodi sottostanti a quello corrente
	 * 
	 * @return numero di nodi figli a quello corrente
	 */
	abstract int getNumberOfChildren ();
	
	/**
	 *Restituisce in formato stringa, tutto il contenuto di Node
	 *
	 *@return Stringa con tutti gli elementi all'interno di Node
	 */
	public String toString ()
	{
		return " Nodo: [Examples:" + this.beginExampleIndex + "-" + (this.endExampleIndex) + "] variance:" + this.variance;
	}
}
