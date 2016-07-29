import java.io.FileNotFoundException;
import java.util.Scanner;

import tree.RegressionTree;
import data.Data;
import data.TrainingDataException;

public class MainTest 
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException
	{
		String flag;
		do		
		{
		Data trainingSet = null;
		System.out.println("Inserire il nome del file contenente il training set desiderato");
		Scanner in = new Scanner(System.in);
		String ts = in.nextLine();
		try {
			//trainingSet = new Data("prova.dat");
			trainingSet = new Data(ts);
			System.out.println("Starting data acquisition phase!");
			System.out.println("Starting learning phase!");
		} catch (TrainingDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		Data trainingSet2 = null;
		try {
			trainingSet2 = new Data("servo.dat");
		} catch (TrainingDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		RegressionTree tree=new RegressionTree(trainingSet);
		//RegressionTree tree2 = new RegressionTree(trainingSet2);
		
		tree.printRules();
		tree.printTree();
		
		//tree2.printRules();
		//tree2.printTree();
		System.out.println("Vuoi ripetere? (y/n)");
		Scanner in2 = new Scanner(System.in);
		flag = in2.nextLine();
		}while(flag=="y" || flag=="Y");

	}
	
}
