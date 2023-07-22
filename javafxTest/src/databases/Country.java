package databases;

/**
 * A model class represents a country.
 * @author www.codejava.net
 *
 */
public class Country {
    private String name;
    private String flag;
 
    public Country(String name, String flag) {
        super();
        this.name = name;
        this.flag = flag;
    }
 
    public String getName() {
        return name;
    }
    
    public String getFlag() {
    	return flag;
    }
 
    public void setName(String name) {
        this.name = name;
    }
    
    public void setFlag(String flag) {
    	this.flag = flag;
    }
     
    public String toString() {
        return this.name;
    }
     
}