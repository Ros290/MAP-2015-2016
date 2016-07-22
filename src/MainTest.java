import java.io.FileNotFoundException;

public class MainTest 
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException
	{
		Data trainingSet= new Data("prova.dat");
		Data trainingSet2= new Data("servo.dat");
		
		/*
		 * Metto un commento a cazzo, giusto per controllare se github individua la modifica da me effettuata
		 */
		
		RegressionTree tree=new RegressionTree(trainingSet);
		RegressionTree tree2 = new RegressionTree(trainingSet2);
		
		tree.printRules();
		
		tree.printTree();
		
		tree2.printRules();
		tree2.printTree();
		
	}

}
