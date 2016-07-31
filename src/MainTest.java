import java.io.FileNotFoundException;
import java.util.Scanner;

import tree.RegressionTree;
import utility.Keyboard;
import data.Data;
import exception.TrainingDataException;
import exception.UnknownValueException;

public class MainTest 
{

	public static void main(String[] args)
	{
		char flag;
		Data trainingSet = null;
		System.out.println("Inserire il nome del file contenente il training set desiderato");
		String ts = Keyboard.readString();
		try 
		{
			trainingSet = new Data(ts);
			System.out.println("Starting data acquisition phase!");
			System.out.println("Starting learning phase!");
			RegressionTree tree=new RegressionTree(trainingSet);
			tree.printRules();
			tree.printTree();
			do
			{
				try
				{
					System.out.println("Starting prediction phase!");
					double attClass = tree.predictClass();
					System.out.println(attClass);
				}
				catch (UnknownValueException e)
				{
					System.out.println(e.toString());
				}
				System.out.println("Vuoi ripetere? (y/n)");
				flag = Keyboard.readChar();
			}while((flag=='y') || (flag=='Y'));
		}
		
		catch (TrainingDataException e) 
		{
			System.out.println(e.getMessage());
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println(e.getMessage());
		}
		

		
	}
	
}
