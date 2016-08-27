package tree;
import java.io.Serializable;

import data.Data;


@SuppressWarnings("serial")
public class LeafNode extends Node implements Serializable
{
	Double predictedClassValue;
	
	public LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex)
	{
		super (trainingSet, beginExampleIndex, endExampleIndex);
		this.predictedClassValue = 0.0;
		for (int i = beginExampleIndex; i < endExampleIndex + 1; i++)
			this.predictedClassValue += trainingSet.getClassValue(i);
		this.predictedClassValue /= (endExampleIndex+1) - beginExampleIndex;
	}
	
	Double getPretictedClassValue ()
	{
		return this.predictedClassValue;
	}
	
	int getNumberOfChildren ()
	{
		return 0;
	}
	
	public String toString ()
	{
		return "LEAF : class = " + this.predictedClassValue + super.toString();
	}
}
