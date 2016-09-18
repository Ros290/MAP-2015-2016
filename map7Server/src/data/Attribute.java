package data;

import java.io.Serializable;

/*
 * La classe modella un generico attributo, discreto o continuo
 * Classe che contiene un campo attributo (stringa) e un indice (int)
 */
public abstract class Attribute implements Serializable
{

	//nome simbolico dell'attributo
	protected String name;
	
	// identificativo numerico dell'attributo
	protected int index;
	
    /**
     * Costruttore di classe
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
    
    
    /**
     * Restitusice il valore del membro name
     * Ottiene il nome dell'attributo
     *
     * @return nome dell'attributo
     */
    public String getName() 
    {
        return this.name;
    }

    /**
     * Restitusice il valore del membro index
     * Ottiene l'identificatore dell'attributo
     *
     * @return identificatore dell'attributo
     */
    public int getIndex() 
    {
        return this.index;
    }
}
