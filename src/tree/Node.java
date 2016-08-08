package tree;
import data.Data;


abstract class Node 
{
	private static int idNodeCount = 0;
	private int idNode;
	private int beginExampleIndex;
	private int endExampleIndex;
	private double variance;
	
	/**
	 * Costruttore della classe Node, quale definisce il sotto-insieme di training quale il nodo conterrà.
	 * In aggiunta avrà un ID univoco e conterrà la Varianza calcolato dagli attributi di classe presenti nel sotto-insieme di training.
	 * 
	 * Esempio: supponendo che il traingSet abbia 100 esempi, e vogliamo definire un nodo che copra gli elementi dalla posizione 55 al 66,
	 * allora i successivi parametri saranno, appunto, 55 e 66
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
	 * Restituisce l'ID univoco del dato Nodo
	 * @return ID del nodo
	 */
	int getIdNode ()
	{
		return this.idNode;
	}
	
	/**
	 * Restituisce la posizione da cui inizia il sotto-insieme di training contenuto nel Nodo
	 * @return Indice per un dato esempio del trainingSet, quale sarebbe il primo esempio del sotto-insieme
	 */
	int getBeginExampleIndex ()
	{
		return this.beginExampleIndex;
	}

	/**
	 * Restituisce la posizione in cui finisce il sotto-insieme di training contenuto nel Nodo
	 * @return Indice per un dato esempio del trainingSet, quale sarebbe l'ultimo esempio del sotto-insieme
	 */
	int getEndExampleIndex ()
	{
		return this.endExampleIndex;
	}
	
	/**
	 * Restituisce la varianza che è stata ricavata in base agli attributi di classe appartenenti al sotto-insieme di training
	 * @return Varianza degli esempi coperti dal Nodo
	 */
	double getVariance ()
	{
		return this.variance;
	}
	
	/**
	 * Indica il numero di nodi sottostanti a quello corrente
	 * @return numero di nodi figli a quello corrente
	 */
	abstract int getNumberOfChildren ();
	
	public String toString ()
	{
		return " Nodo: [Examples:" + this.beginExampleIndex + "-" + (this.endExampleIndex) + "] variance:" + this.variance;
	}
}
