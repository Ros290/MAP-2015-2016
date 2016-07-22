import java.io.FileNotFoundException;

public class MainTest 
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException
	{
		Data trainingSet= new Data("C:/Users/Windows 7/Desktop/DIB/Metodi Avanzati di Programmazione/CasoStudio 2015 -  2016/map2/prova.dat");
		Data trainingSet2= new Data("C:/Users/Windows 7/Desktop/DIB/Metodi Avanzati di Programmazione/CasoStudio 2015 -  2016/map1/servo.dat");
		
		RegressionTree tree=new RegressionTree(trainingSet);
		RegressionTree tree2 = new RegressionTree(trainingSet2);
		
		tree.printRules();
		
		tree.printTree();
		
		tree2.printRules();
		tree2.printTree();
		
	}

}
