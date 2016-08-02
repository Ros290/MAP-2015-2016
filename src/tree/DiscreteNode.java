package tree;
import tree.SplitNode.SplitInfo;
import data.Attribute;
import data.Data;
import data.DiscreteAttribute;


public class DiscreteNode extends SplitNode 
{
	/**
	 * Costruttore di uno SplitNode con attributo indipendente di tipo discreto 
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
		 * Definisco una collezione di splitNode di nome mapSplit2, tale collezione avrà tanti figli quanti sono i valori quali
		 * l'attributo indipendente POTREBBE contenere (poichè non è detto che, nella collezione di esempi, siano riportati
		 * tutti i valori.
		 * 
		 * Esempio: se attributo indipendete di nome "Figura" può assumere valori come "quadrato, cerchio, rettangolo",
		 * ma negli esempi analizzati si possono trovare solo esempi che si riferiscono unicamente a "quadrato"
		 * 
		 * Infatti per ovviare a ciò, scandendo uno per uno gli esempi del sotto-insieme, si contano quanti sono i valori della classe indipendente
		 * effettivamente assunti dagli esempi, così che si possa ritornare una collezione costituita UNICAMENTE da valori effettivamente
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
		
		//mapSplit = new SplitInfo[child+1];
		mapSplit.add(child, new SplitInfo(trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex()), start, endExampleIndex, child+1));
		i = 0;
		while (i <= child)
		{
			//mapSplit[i] = mapSplit2 [i];
			mapSplit.add(i, new SplitInfo (trainingSet.getExplanatoryValue(i-1, attribute.getIndex()), start, i, i));
			i++;
		}
	}
	
	
	/*
	 * Luigi io metto tutto fra commenti in modo tale da non confondere nulla, e non creare danni, comunque scrivo qui una nuova versione della 
	 * funzione setSpliValue
	 * questa funzione dovrebbe a rigor di logica fare esattamente la stessa cosa della tua,
	 * solo che non usa mapSplit2
	 * tuttavia non ho provato, volevo prima fartela vedere
	 
	void setSplitInfo(Data trainingSet,int beginExampleIndex, int endExampleIndex, Attribute attribute)
	{
		Object currentSplitValue = trainingSet.getExpanatoryValue(beginExampleIndex, attribute.getIndex());
		int start = beginExampleIndex;
		int child = 0;
		for(i = beginExampleIndex + 1; i < endExampleIndex; i++)
		{
			if ( currentSplitValue.equals(trainingSet.getExplanatoryValue(i, attribute.getIndex())) == false)
			{
				mapSplit.add(child, new SplitInfo(currentSplitValue, beginSplit, i-1, child));
				currentSplitValue = trainingSet.getExplanatoryValue(i, attribute,getIndex());
				child ++;
				beginSplit = i;
			}
		}
		mapSplit.add(child, new SplitInfo(currentSplitValue, bginSplit, endExampleIndex, child);
	}
	 * */
	
	
	
	
	/**
	 * Effettua il confronto di value con i valori di tutti i nodi figli dello splitNode in analisi, restituisce 
	 * l'identificativo del nodo figlio qualora la condizione si verifichi, altrimenti torna -1
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
	
	public String toString ()
	{	
		return "DISCRETE " + super.toString();
	}

	
	@Override
	public int compareTo(SplitNode o) {
		if(o.getVariance() == this.getVariance())
		     return 0;
		else
			if(o.getVariance() < this.getVariance())
				 return -1;
		return 1;
	}

}
