package data;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import database.DbAccess;
import database.Example;
import database.TableData;
import database.TableSchema;
import exception.DatabaseConnectionException;
import exception.EmptySetException;
import exception.TrainingDataException;



@SuppressWarnings("serial")
public class Data implements Serializable
{
	
	
	private List<Example> data = new ArrayList<Example>();
	
	private int numberOfExamples;
	private List<Attribute> explanatorySet = new LinkedList<Attribute>();
	private ContinuousAttribute classAttribute;
	
	public Data(String tableName)throws FileNotFoundException, TrainingDataException, ClassNotFoundException, DatabaseConnectionException, SQLException, EmptySetException
	{
		int i;
		DbAccess db = new DbAccess();
		TableData td = new TableData (db);
		
			
			DbAccess.initConnection();
			TableSchema ts = new TableSchema (db, tableName);
			this.data = td.getTransazioni(tableName);
			this.numberOfExamples = data.size();
			for (i = 0; i < ts.getNumberOfAttributes() - 1; i++)
			{
				if (ts.getColumn(i).isNumber())
					this.explanatorySet.add(new ContinuousAttribute (ts.getColumn(i).getColumnName(),i));
				else
				{
					Set<Object> discreteValues = td.getDistinctColumnValues(tableName, ts.getColumn(i));
					this.explanatorySet.add ( new DiscreteAttribute (ts.getColumn(i).getColumnName(),i, discreteValues));
				}
			}
			this.classAttribute = new ContinuousAttribute (ts.getColumn(i).getColumnName(),i);
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
				//value+=data[i][j]+",";
				value+=data.get(i).get(j)+",";
			//value+=data[i][explanatorySet.size()]+"\n";
			value+=data.get(i).get(explanatorySet.size())+"\n";
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
		
		return (Double) data.get(exampleIndex).get(this.explanatorySet.size());
	}
	
	/**
	 * Ritorna il valore associato all'attributo con indice attributeIndex, riferito all'esempio con indice exampleIndex 
	 * @param exampleIndex  indice dell'esempio che si desidera analizzare
	 * @param attributeIndex indice di un determinato attributo (indipendente o di classe)
	 * @return
	 */
	public Object getExplanatoryValue(int exampleIndex, int attributeIndex)
	{
		
		return data.get(exampleIndex).get(attributeIndex);
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
		Example temp;
		for (int k=0;k<getNumberOfExplanatoryAttributes()+1;k++){
			
			temp=data.get(i);
			data.set(i, data.get(j));
			data.set(j,temp);	
		}
	}
	

	
	
	/*
	 * Partiziona il vettore rispetto all'elemento x e restiutisce il punto di separazione
	 */
	private  int partition(Attribute attribute, int inf, int sup)
	{
		int i,j;
	
		i=inf; 
		j=sup; 
		int	med=(inf+sup)/2;
		String x=getExplanatoryValue(med, attribute.getIndex()).toString();
		swap(inf,med);
	
		while (true) 
		{
			
			while(i<=sup && (getExplanatoryValue(i, attribute.getIndex())).toString().compareTo(x)<=0)
			{ 
				i++; 	
			}
		
			while((getExplanatoryValue(j, attribute.getIndex())).toString().compareTo(x)>0)
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
			
			pos=partition((Attribute)attribute, inf, sup);
					
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
