package data;

/*
 * Classe che contiene un campo attributo (stringa) e un indice (int)
 */
public abstract class Attribute 
{

	protected String name;
	protected int index;
	
    /**
     * Inizializza un attributo
     *
     * @param name  nome dell'attributo
     * @param index identificativo dell'attributo
     */
    Attribute(String name, int index) 
    {
        this.name = name;
        this.index = index;
    }
    
    Attribute(){}
    
    /**
     * Ottiene il nome dell'attributo
     *
     * @return nome dell'attributo
     */
    public String getName() 
    {
        return this.name;
    }

    /**
     * Ottiene l'identificatore dell'attributo
     *
     * @return identificatore dell'attributo
     */
    public int getIndex() 
    {
        return this.index;
    }
}
