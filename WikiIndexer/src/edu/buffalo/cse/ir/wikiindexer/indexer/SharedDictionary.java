/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Properties;

/**
 *
 * This class represents a subclass of a Dictionary class that is
 * shared by multiple threads. All methods in this class are
 * synchronized for the same reason.
 */
public class SharedDictionary extends Dictionary {

	/**
	 * Public default constructor
	 * @param props: The properties file
	 * @param field: The field being indexed by this dictionary
	 */
	public SharedDictionary(Properties props, INDEXFIELD field) {
		super(props, field);
		// TODO Add more code here if needed
	}
	
	/**
	 * Method to lookup and possibly add a mapping for the given value
	 * in the dictionary. The class should first try and find the given
	 * value within its dictionary. If found, it should return its
	 * id (Or hash value). If not found, it should create an entry and
	 * return the newly created id.
	 * @param value: The value to be looked up
	 * @return The id as explained above.
	 */
	public synchronized int lookup(String value) {
		//TODO Implement this method
        int dictionaryValue = -1;
        dictionaryValue = getValue(value);
        if(dictionaryValue<0) {
            Dictionary.incrementCounterForType(this.getType());
            if(setValue(value, Dictionary.getCounterForType(this.getType()))) {
                dictionaryValue = Dictionary.getCounterForType(this.getType());
            } else {
                System.err.println("Could not set value in \""+getType().toString()+"\" dictionary for keyword: "+value);
            }
        }
        return dictionaryValue;
	}

    public String getKeyForValue(int value) {
        String key = null;
        key = getKey(value);
        return key;
    }
}
