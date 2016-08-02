package data;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import exception.TrainingDataException;



public class Data 
{
	
	private Object data [][];
	private int numberOfExamples;
	//private Attribute explanatorySet[];
	private List<Attribute> explanatorySet = new LinkedList<Attribute>();
	private ContinuousAttribute classAttribute;
	
	public Data(String fileName)throws FileNotFoundException, TrainingDataException
	{
		
		  File inFile = new File (fileName);

		  Scanner sc = new Scanner (inFile);
	      String line = sc.nextLine();
	      if(!line.contains("@schema"))
	      {
	    	  sc.close();
	    	  throw new RuntimeException("Errore nello schema");
	      }
	      //String s[]=line.split(" ");
		  //popolare explanatory Set 
	      //@schema 4
	      
		  //explanatorySet = new Attribute[new Integer(s[1])];
	      short iAttribute=0;
	      line = sc.nextLine();
	      while(!line.contains("@data"))
	      {
	    	  //s=line.split(" ");
	    	  List <String> s = new ArrayList <String> (Arrays.asList(line.split(" ")));
	    	  //if(s[0].equals("@desc"))
	    	  if (s.get(0).equals("@desc"))
	    	  { // aggiungo l'attributo allo spazio descrittivo
		    		//@desc motor discrete A,B,C,D,E  
	    		  	Set<String> discreteValues = new HashSet <String> (Arrays.asList(s.get(2).split(",")));
	    		  	explanatorySet.add(new DiscreteAttribute (s.get(1), iAttribute, discreteValues ));
		      }
	    	  else if(s.get(0).equals("@target"))
	    			  classAttribute=new ContinuousAttribute(s.get(1), iAttribute);
	    			  
	    	  iAttribute++;
	    	  line = sc.nextLine();
	    	  
	      }
		      
		  //avvalorare numero di esempi
	      //@data 167
	      numberOfExamples=new Integer(line.split(" ")[1]);
	      
	      //popolare data
	      data=new Object[numberOfExamples][explanatorySet.size()+1];
	      short iRow=0;
	      while (sc.hasNextLine())
	      {
	    	  line = sc.nextLine();
	    	  // assumo che attributi siano tutti discreti
	    	  /*
	    	  s=line.split(","); //E,E,5,4, 0.28125095
	    	  for(short jColumn=0;jColumn<s.length-1;jColumn++)
	    	  	  data[iRow][jColumn]=s[jColumn];
	    	  data[iRow][s.length-1]=new Double(s[s.length-1]);
	    	   */
	    	  List <String> s = new ArrayList <String> (Arrays.asList(line.split(",")));
	    	  for(short jColumn=0;jColumn<s.size()-1;jColumn++)
	    		  data[iRow][jColumn]=s.get(jColumn);
	    	  data[iRow][s.size()-1]=new Double(s.get(s.size()-1));
	    	  iRow++;
	      }
		  sc.close();
	}
	
	/**
	 *Ritorna , in formato stringa, tutto il contenuto di Data
	 *
	 *@return Stringa con tutti gli elementi all'interno di Data
	 */
	public String toString()
	{
		String value="";
		for(int i=0;i<numberOfExamples;i++)
		{
			for(int j=0;j<explanatorySet.size();j++)
				value+=data[i][j]+",";
			value+=data[i][explanatorySet.size()]+"\n";
		}
		return value;
	}
	
	/**
	 * Ritorna il numero degli esempi rimportati all'interno di Data
	 * @return cardinalità di esempi
	 */
	public int getNumberOfExamples ()
	{
		return numberOfExamples;
	}
	
	/**
	 * Ritorna il numero di Attributi Indipendenti di cui è composta la collezione di esempi
	 * @return Numero di Attributi Indipendenti
	 */
	public int getNumberOfExplanatoryAttributes()
	{
		return this.explanatorySet.size();
	}
	
	/**
	 * Ritorna il valore associato all'attributo di classe riferito all'esempio in posizione exampleIndex
	 * @param exampleIndex indice dell'esempio che si desidera analizzare
	 * @return Il valore all'interno dell'attributo di classe
	 */
	public Double getClassValue (int exampleIndex)
	{
		return (Double) data[exampleIndex][this.explanatorySet.size()];
	}
	
	/**
	 * Ritorna il valore associato all'attributo con indice attributeIndex, riferito all'esempio con indice exampleIndex 
	 * @param exampleIndex  indice dell'esempio che si desidera analizzare
	 * @param attributeIndex indice di un determinato attributo (indipendente o di classe)
	 * @return
	 */
	public Object getExplanatoryValue(int exampleIndex, int attributeIndex)
	{
		return data[exampleIndex][attributeIndex];
	}
	
	/**
	 * Ritorna l'attributo di indice index
	 * @param index indice di un determinato attributo
	 * @return l'attributo che si vuole analizzare
	 */
	public Attribute getExplanatoryAttribute(int index)
	{
		return this.explanatorySet.get(index);
	}

	public ContinuousAttribute getClassAttribute()
	{
		return this.classAttribute;
	}
	
	/**
	 * Permette l'ordinamento degli esempi da beginExampleIndex a endExampleIndex, in base al valore che contengono
	 * in un determinato attributo
	 * @param attribute attributo indipendente, in modo tale che gli esempi vengano ordinati in base al valore assunto per quel 
	 * determinato attributo
	 * @param beginExampleIndex indice di partenza della sotto-collezione di data che si vuole ordinare
	 * @param endExampleIndex indica la "fine" della sotto-collezione di data che si vuole ordinare
	 */
	public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex)
	{
		quicksort(attribute, beginExampleIndex, endExampleIndex);
	}
	
	// scambio esempio i con esempi oj
	private void swap(int i,int j)
	{
		Object temp;
		for (int k=0;k<getNumberOfExplanatoryAttributes()+1;k++){
			temp=data[i][k];
			data[i][k]=data[j][k];
			data[j][k]=temp;
		}
		
	}
	

	
	
	/*
	 * Partiziona il vettore rispetto all'elemento x e restiutisce il punto di separazione
	 */
	private  int partition(DiscreteAttribute attribute, int inf, int sup)
	{
		int i,j;
	
		i=inf; 
		j=sup; 
		int	med=(inf+sup)/2;
		String x=(String)getExplanatoryValue(med, attribute.getIndex());
		swap(inf,med);
	
		while (true) 
		{
			
			while(i<=sup && ((String)getExplanatoryValue(i, attribute.getIndex())).compareTo(x)<=0)
			{ 
				i++; 	
			}
		
			while(((String)getExplanatoryValue(j, attribute.getIndex())).compareTo(x)>0) 
			{
				j--;
			}
			
			if(i<j) 
			{ 
				swap(i,j);
			}
			else break;
		}
		swap(inf,j);
		return j;

	}
	
	/*
	 * Algoritmo quicksort per l'ordinamento di un array di interi A
	 * usando come relazione d'ordine totale "<="
	 * @param A
	 */
	private void quicksort(Attribute attribute, int inf, int sup)
	{
		
		if(sup>=inf){
			
			int pos;
			
			pos=partition((DiscreteAttribute)attribute, inf, sup);
					
			if ((pos-inf) < (sup-pos+1)) {
				quicksort(attribute, inf, pos-1); 
				quicksort(attribute, pos+1,sup);
			}
			else
			{
				quicksort(attribute, pos+1, sup); 
				quicksort(attribute, inf, pos-1);
			}
			
			
		}
		
	}
}
