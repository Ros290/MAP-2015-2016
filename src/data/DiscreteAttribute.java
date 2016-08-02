package data;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


public class DiscreteAttribute extends Attribute implements Iterable<String>
{
	private Set<String> values=new TreeSet<String>();

    /**
     * Inizializza
     *
     * @param name   nome dell'attributo
     * @param index  identificativo dell'attributo
     * @param values dominio dell'attributo
     */
	public DiscreteAttribute (String name, int index, Set<String> values)
	{
		super (name, index);
		this.values=values;
	}
	
	/**
	 * Ottiene il numero di valori discreti nell'attributo
	 * 
	 * @return numero di valori discreti nell'attributo 
	 */
	public int getNumberOfDistinctValues()
	{
		return this.values.size();
	}
	
	@Override
	public Iterator<String> iterator()
	{
		return values.iterator();
	}	
}
