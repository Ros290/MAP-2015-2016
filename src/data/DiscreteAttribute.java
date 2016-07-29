package data;


public class DiscreteAttribute extends Attribute 
{
	private String values[];

    /**
     * Inizializza
     *
     * @param name   nome dell'attributo
     * @param index  identificativo dell'attributo
     * @param values dominio dell'attributo
     */
	public DiscreteAttribute (String name, int index, String values[])
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
		return this.values.length;
	}
	
	/**
	 * Ottiene il valore discreto in posizione i-esima
	 * @param i posizione in cui si trova il valore discreto desiderato
	 * @return valore discreto in posizione i-esima
	 */
	String getValue (int i)
	{
		return this.values[i];
	}
}
