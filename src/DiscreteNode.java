
public class DiscreteNode extends SplitNode 
{
	/**
	 * Costruttore di uno SplitNode con attributo indipendente di tipo discreto 
	 * @param trainingSet collezione di training che si vuole analizzare
	 * @param beginExampleIndex indica l'esempio di partenza del sotto-insieme di training da analizzare tramite indice intero
	 * @param endExampleIndex indica l'ultimo esempio del sotto-insieme di training da analizzare tramite indice intero 
	 * @param attribute indica l'attributo di classe quale dovr� analizzare lo SplitNode, quale dovr� essere di tipo Discreto
	 */
	public DiscreteNode (Data trainingSet, int beginExampleIndex, int endExampleIndex, DiscreteAttribute attribute)
	{
		super (trainingSet, beginExampleIndex, endExampleIndex, attribute);
	}
	
	/**
	 * Popola lo SplitNode, in base al sotto-insieme di training in analisi, categorizzando gli esempi in base all'attributo indipendente passato come parametro
	 * @param trainingSet collezione di training che si vuole analizzare
	 * @param beginExampleIndex indica l'esempio di partenza del sotto-insieme di training da analizzare tramite indice intero
	 * @param endExampleIndex indica l'ultimo esempio del sotto-insieme di training da analizzare tramite indice intero 
	 * @param attribute indica l'attributo di classe con la quale saranno categorizzati gli esempi all'interno del sottoinsieme 
	 */
	@Override
	void setSplitInfo(Data trainingSet,int beginExampleIndex, int endExampleIndex, Attribute attribute)
	{
		/*
		 * Definisco una collezione di splitNode di nome mapSplit2, tale collezione avr� tanti figli quanti sono i valori quali
		 * l'attributo indipendente POTREBBE contenere (poich� non � detto che, nella collezione di esempi, siano riportati
		 * tutti i valori.
		 * 
		 * Esempio: se attributo indipendete di nome "Figura" pu� assumere valori come "quadrato, cerchio, rettangolo",
		 * ma negli esempi analizzati si possono trovare solo esempi che si riferiscono unicamente a "quadrato"
		 * 
		 * Infatti per ovviare a ci�, scandendo uno per uno gli esempi del sotto-insieme, si contano quanti sono i valori della classe indipendente
		 * effettivamente assunti dagli esempi, cos� che si possa ritornare una collezione costituita UNICAMENTE da valori effettivamente
		 * presnti nella collezione di Training 
		 */
		
		SplitInfo mapSplit2 [] = new SplitInfo [((DiscreteAttribute)attribute).getNumberOfDistinctValues()];
		int child = 0;
		int i;
		int start = beginExampleIndex;
		for(i = beginExampleIndex + 1; i < endExampleIndex; i++)
		{
			/*
			 * Supponendo che il trainingSet sia stato ordinato in base all'attributo di cui stiamo i nodi split candidati
			 * allora, scandendo un esempio alla volta, quando individua un esempio (con indice i) con attributo differente dall'esempio precedente (indice i - 1),
			 * allora inserisce nel mapSplit il nodo split con il valore dell'attributo dell'esempio precedente 
			 */
			if (!trainingSet.getExplanatoryValue(i, attribute.getIndex()).equals(trainingSet.getExplanatoryValue(i-1, attribute.getIndex())))
			{
				mapSplit2 [child] = new SplitInfo (trainingSet.getExplanatoryValue(i-1, attribute.getIndex()), start, i - 1, child);
				child ++;
				start = i;
			}
		}
		mapSplit2 [child] = new SplitInfo (trainingSet.getExplanatoryValue(i-1, attribute.getIndex()), start, i, child);
		mapSplit = new SplitInfo [child+1];
		i = 0;
		while (i <= child)
		{
			mapSplit [i] = mapSplit2 [i];
			i++;
		}
	}
	
	/**
	 * Effettua il confronto di value con i valori di tutti i nodi figli dello splitNode in analisi, restituisce 
	 * l'identificativo del nodo figlio qualora la condizione si verifichi, altrimenti torna -1
	 * @param value valore con cui confrontare i valori dei nodi figli di uno splitNode
	 * @return identificativo di un nodo figlio, se il valore di tale nodo corrisponde a value, altrimenti -1 
	 */
	@Override
	public int testCondition (Object value)
	{
		for (int i = 0; i < mapSplit.length; i++)
		{
			if (value.equals(mapSplit[i].getSplitValue()))
				return mapSplit[i].numberChild;
		}
		return -1;
	}
	
	public String toString ()
	{	
		return "DISCRETE " + super.toString();
	}
}
