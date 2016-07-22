public class RegressionTree 
{
	Node root;
	RegressionTree childTree[];
	
	RegressionTree ()
	{
		this.childTree = new RegressionTree [1];
	}
	
	public RegressionTree (Data trainingSet)
	{
		learnTree (trainingSet, 0, trainingSet.getNumberOfExamples()-1, (trainingSet.getNumberOfExamples() * 10)/100);
	}
	
	boolean isLeaf(Data trainingSet,int begin, int end,int numberOfExamplesPerLeaf)
	{
		return ((end - begin)<= numberOfExamplesPerLeaf);
	}
	
	SplitNode determineBestSplitNode(Data trainingSet,int begin,int end)
	{
		DiscreteNode splitNodoPreferito = new DiscreteNode (trainingSet, begin, end, (DiscreteAttribute)trainingSet.getExplanatoryAttribute(0));
		for (int i = 1; i < trainingSet.getNumberOfExplanatoryAttributes(); i++)
		{
			DiscreteNode splitNodoCandidato = new DiscreteNode (trainingSet, begin, end, (DiscreteAttribute)trainingSet.getExplanatoryAttribute(i));
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
		
		
}