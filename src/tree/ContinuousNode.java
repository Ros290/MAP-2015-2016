package tree;

import java.util.ArrayList;
import java.util.List;

import data.Attribute;
import data.ContinuousAttribute;
import data.Data;

public class ContinuousNode extends SplitNode
{


	public ContinuousNode (Data trainingSet,int beginExampelIndex, int endExampleIndex,ContinuousAttribute attribute)
	{
		super (trainingSet, beginExampelIndex, endExampleIndex, attribute);
	}

	 void setSplitInfo(Data trainingSet,int beginExampleIndex, int endExampleIndex, Attribute attribute)
	 {
			//Update mapSplit defined in SplitNode -- contiene gli indici del partizionamento
			Double currentSplitValue= (Double)trainingSet.getExplanatoryValue(beginExampleIndex,attribute.getIndex());
			double bestInfoVariance=0;
			List <SplitInfo> bestMapSplit=null;
			
			for(int i=beginExampleIndex+1;i<=endExampleIndex;i++){
				Double value=(Double)trainingSet.getExplanatoryValue(i,attribute.getIndex());
				if(value.doubleValue()!=currentSplitValue.doubleValue()){
				//	System.out.print(currentSplitValue +" var ");
					double localVariance=new LeafNode(trainingSet, beginExampleIndex,i-1).getVariance();
					double candidateSplitVariance=localVariance;
					localVariance=new LeafNode(trainingSet, i,endExampleIndex).getVariance();
					candidateSplitVariance+=localVariance;
					//System.out.println(candidateSplitVariance);
					if(bestMapSplit==null){
						bestMapSplit=new ArrayList<SplitInfo>();
						bestMapSplit.add(new SplitInfo(currentSplitValue, beginExampleIndex, i-1,0,"<="));
						bestMapSplit.add(new SplitInfo(currentSplitValue, i, endExampleIndex,1,">"));
						bestInfoVariance=candidateSplitVariance;
					}
					else{		
												
						if(candidateSplitVariance<bestInfoVariance){
							bestInfoVariance=candidateSplitVariance;
							bestMapSplit.set(0, new SplitInfo(currentSplitValue, beginExampleIndex, i-1,0,"<="));
							bestMapSplit.set(1, new SplitInfo(currentSplitValue, i, endExampleIndex,1,">"));
						}
					}
					currentSplitValue=value;
				}
			}
			mapSplit=bestMapSplit;
			//rimuovo split inutili (che includono tutti gli esempi nella stessa partizione)
			
			if((mapSplit.get(1).beginIndex==mapSplit.get(1).getEndIndex())){
				mapSplit.remove(1);
				
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
			for (int i = 0; i < mapSplit.size(); i++)
			{
				if (value.equals(mapSplit.get(i).getSplitValue()))
					return mapSplit.get(i).numberChild;
			}
			return -1;
		}
		public String toString ()
		{	
			return "CONTINUOUS " + super.toString();
		}
		
		/**
		 * Effettua il confronto con un altro SplitNode, in base al risultato si può capire quale sia lo splitNode con meno Varianza
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