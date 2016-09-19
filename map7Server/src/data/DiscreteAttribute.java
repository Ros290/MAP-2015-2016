package data;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

//Estende la classe Attribute e rappresenta un attributo discreto
@SuppressWarnings("serial")
public class DiscreteAttribute extends Attribute implements Iterable<Object>, Serializable
{
	//insieme di oggetti stringa, uno per ciasun valore discreto che l'attributo può assumere
	private Set<Object> values=new TreeSet<Object>();

    /**
     * Invoca il costruttore della super-classe
     * Inizializza i valori di un attributo discreto
     *
     * @param name   nome dell'attributo
     * @param index  identificativo dell'attributo
     * @param values dominio dell'attributo
     */
	public DiscreteAttribute (String name, int index, Set<Object> values)
	{
		super (name, index);
		this.values=values;
	}
	
	/**
	 * Restituisce la carinalòità di values
	 * Ottiene il numero di valori discreti nell'attributo
	 * 
	 * @return numero di valori discreti nell'attributo 
	 */
	public int getNumberOfDistinctValues()
	{
		return this.values.size();
	}
	
	@Override
	public Iterator<Object> iterator()
	{
		return values.iterator();
	}	
}
