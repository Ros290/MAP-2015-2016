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
import exception.UnknownValueException;


//Classe che modella l'insieme degli esempi di training 
@SuppressWarnings("serial")
public class Data implements Serializable
{
	//contenitore di oggeti di tipo Example che contiene il trainingSet
	private List<Example> data = new ArrayList<Example>();
	
	//cardinalità del trainingSet
	private int numberOfExamples;
	
	//contenitore di oggetti di tipo Attribute per rappresentare gli attributi indipendenti
	private List<Attribute> explanatorySet = new LinkedList<Attribute>();
	
	//oggetto per modellare l'attributo di classe
	private ContinuousAttribute classAttribute;
	
	/**
     * Avvalora explanatorySet, avvalora classAttribue, avvalora il numero di esempi e popola data con gli esempi di taining 
     *
     * @param tableName nome del file contenente i dati
	 * @throws UnknownValueException 
     * @throw FileNotFoundException se il file non esiste
     * @throw TrainingDataException
     * @throw ClassNotFoundException
     * @throw DatabaseConnectionException se la connessione con il database fallisce 
     * @throw SQLException
     * @throw EmptySetException
     */
	public Data(String tableName)throws FileNotFoundException, TrainingDataException, ClassNotFoundException, DatabaseConnectionException, SQLException, EmptySetException, UnknownValueException
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
	 *Restituisce in formato stringa tutto il contenuto di data
	 *
	 *@return Stringa con tutti gli elementi all'interno di data
	 */
	public String toString()
	{
		String value="";
		for(int i=0;i<numberOfExamples;i++)
		{
			for(int j=0;j<explanatorySet.size();j++)
				value+=data.get(i).get(j)+",";
			value+=data.get(i).get(explanatorySet.size())+"\n";
		}
		return value;
	}
	
	/**
	 * Restituisce il valore del membro numberOfExamples
	 * Restituisce il numero degli esempi riportati all'interno di data
	 * 
	 * @return cardinalità di esempi
	 */
	public int getNumberOfExamples ()
	{
		return numberOfExamples;
	}
	
	/**
	 * Restituisce la cardinalità dell'insieme explanatorySet
	 *
	 * @return Numero di Attributi Indipendenti
	 */
	public int getNumberOfExplanatoryAttributes()
	{
		return this.explanatorySet.size();
	}
	
	/**
	 * Restituisce il valore associato all'attributo di classe per l'esempio indicizzato da exampleIndex
	 * 
	 * @param exampleIndex indice dell'esempio che si desidera analizzare
	 * @return il valore all'interno dell'attributo di classe
	 */
	public Double getClassValue (int exampleIndex)
	{
		return (Double) data.get(exampleIndex).get(this.explanatorySet.size());
	}
	
	/**
	 * Restituisce il valore associato all'attributo con indice attributeIndex, riferito all'esempio con indice exampleIndex 
	 * 
	 * @param exampleIndex  indice dell'esempio che si desidera analizzare
	 * @param attributeIndex indice di un determinato attributo (indipendente o di classe)
	 * @return il valore dell'attributo indicizzato da attributeIndex per l'esempio exampleIndex
	 */
	public Object getExplanatoryValue(int exampleIndex, int attributeIndex)
	{
		return data.get(exampleIndex).get(attributeIndex);
	}
	
	/**
	 * Restituisce l'attributo indicizzato da index in explanatorySet
	 * 
	 * @param index indice di un determinato attributo
	 * @return l'attributo che si vuole analizzare
	 */
	public Attribute getExplanatoryAttribute(int index)
	{
		return this.explanatorySet.get(index);
	}

	/**
	 * Restituisce l'oggetto corripondente all'attributo di classe
	 * 
	 * @return oggetto ContinuousAttribute associato al membro classAttribute
	 */
	public ContinuousAttribute getClassAttribute()
	{
		return this.classAttribute;
	}
	
	/**
	 * Permette l'ordinamento degli esempi da beginExampleIndex a endExampleIndex, in base al valore che contengono
	 * in un determinato attributo
	 * 
	 * @param attribute attributo indipendente, in modo tale che gli esempi vengano ordinati in base al valore assunto per quel 
	 * determinato attributo
	 * @param beginExampleIndex indice di partenza della sotto-collezione di data che si vuole ordinare
	 * @param endExampleIndex indica la "fine" della sotto-collezione di data che si vuole ordinare
	 */
	public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex)
	{
		quicksort(attribute, beginExampleIndex, endExampleIndex);
	}
	
	
	/**
	 * Funzione accessoria per lo scambio
	 * Scambia l'esempio i con l'esempio j
	 * 
	 * @param i indice 
	 * @param j indice 
	 */
	private void swap(int i,int j)
	{
		Example temp;
		for (int k=0;k<getNumberOfExplanatoryAttributes()+1;k++){
			
			temp=data.get(i);
			data.set(i, data.get(j));
			data.set(j,temp);	
		}
	}
	
	
	/**
	 * Partiziona il vettore rispetto all'elemento x e restiutisce il punto di separazione
	 * 
	 * @param attribute attributo 
	 * @param inf indice
	 * @param sup indice
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
	
	/**
	 * Algoritmo quicksort per l'ordinamento di un array di interi 
	 * 
	 * @param attribute attributo 
	 * @param inf indice
	 * @param sup indice
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
