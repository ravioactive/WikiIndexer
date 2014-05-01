/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 *  An abstract class that represents a dictionary object for a
 *         given index
 */
public abstract class Dictionary implements Writeable {

    private static int termCounter = 0;
    private static int authorCounter = 0;
    private static int linkCounter = 0;
    private static int categoryCounter = 0;

    protected static void incrementCounterForType(INDEXFIELD type) {
        switch(type) {
            case AUTHOR:
                ++authorCounter;
                break;
            case LINK:
                ++linkCounter;
                break;
            case CATEGORY:
                ++categoryCounter;
                break;
            case TERM:
                ++termCounter;
                break;
        }
    }

    protected static int getCounterForType(INDEXFIELD type) {
        int value = 0;

        switch(type) {
            case AUTHOR:
                value = authorCounter;
                break;
            case LINK:
                value = linkCounter;
                break;
            case CATEGORY:
                value = categoryCounter;
                break;
            case TERM:
                value = termCounter;
                break;
        }

        return value;
    }

	private final Properties properties;
	private INDEXFIELD type = null;
    private String dictionaryName = "";

    private String starter = "!";
    private String separator = "|";
    private String entrySeparator = ",";
    private String filepath = null;
    private String fileName = null;

    private Map<String, Integer> internalFwdTable = new HashMap<String, Integer>();
    private Map<Integer, String> internalBkwTable = new HashMap<Integer, String>();

	public Dictionary(Properties props, INDEXFIELD field) {
		properties = props;
		type = field;
        dictionaryName = (this instanceof SharedDictionary)?"DOC":type.toString();
        filepath = properties.getProperty("root.dir",
                System.getProperty("user.dir"));
        fileName = filepath + File.separator + "dictionary_" + dictionaryName;
		// TODO Implement this method
	}

	/**
	 * Method to check if the given value exists in the dictionary or not Unlike
	 * the subclassed lookup methods, it only checks if the value exists and
	 * does not change the underlying data structure
	 * 
	 * @param value
	 *            : The value to be looked up
	 * @return true if found, false otherwise
	 */
	public boolean exists(String value) {
		// TODO Implement this method
		boolean exists = false;
		if (internalFwdTable.containsKey(value))
			exists = true;

		return exists;
	}

	/**
	 * MEthod to lookup a given string from the dictionary. The query string can
	 * be an exact match or have wild cards (* and ?) Must be implemented ONLY
	 * AS A BONUS
	 * 
	 * @param queryStr
	 *            : The query string to be searched
	 * @return A collection of ordered strings enumerating all matches if found
	 *         null if no match is found
	 */
	public Collection<String> query(String queryStr) {
		// TODO: Implement this method (FOR A BONUS) -- INCOMPLETE
		//int value = getValue(queryStr);
		List<String> matchedSet = null;
		return matchedSet;
	}

	private Collection<String> splitQuery(String query) {
		Set<String> queryTokens = new HashSet<String>();

		return queryTokens;
	}

	protected int getValue(String key) {
		int value = -1;
		if (exists(key))
			value = internalFwdTable.get(key);

		return value;
	}

	protected String getKey(int value) {
		String key = null;
		if (internalBkwTable.containsKey(value))
			key = internalBkwTable.get(value);
			
		return key;
	}

	public boolean setValue(String key, int value) {
		boolean valueSet = false;
		if (!exists(key)) {
			internalFwdTable.put(key, value);
			internalBkwTable.put(value, key);
			valueSet = true;
		}
		return valueSet;
	}

	public INDEXFIELD getType() {
		return type;
	}

	/**
	 * Method to get the total number of terms in the dictionary
	 * 
	 * @return The size of the dictionary
	 */
	public int getTotalTerms() {
		int dictionarySize = -1;
		dictionarySize = internalFwdTable.size();
		return dictionarySize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		// TODO Implement this method
		// 1. call Dictionary.writeToDisk() - Dictionary writes itself
		// 2. Write the index's map instead of the whole index object - with -1
		// for metadata
		// 3. metadata: -1
		try {

			File dictionaryFile = new File(fileName);
            FileChannel dictChannel = new FileOutputStream(dictionaryFile, true).getChannel();
			int block_entry_counts = 1000;

			StringBuilder content = new StringBuilder();

			int count = 0;
			Iterator<Map.Entry<String, Integer>> internalTableIterator = internalFwdTable.entrySet().iterator();
			while (internalTableIterator.hasNext()) {
				count = 0;

				while (internalTableIterator.hasNext() && count < block_entry_counts) {
					Map.Entry<String, Integer> entry = internalTableIterator
							.next();
					content.append(entry.getKey()).append(entrySeparator)
							.append(entry.getValue());
					content.append(separator);
                    ++count;
				}

				byte[] byteArray = new byte[256 * 1024];
				ByteBuffer buffer = ByteBuffer.wrap(byteArray);
				buffer.clear();
				buffer.put(content.toString().getBytes());
				buffer.flip();
				while (buffer.hasRemaining()) {
					dictChannel.write(buffer);
				}

				content.delete(0, content.length());
			}

			dictChannel.force(true);
			dictChannel.close();

		} catch (FileNotFoundException e) {
			System.err.println(e.getStackTrace());
			throw new IndexerException(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getStackTrace());
			throw new IndexerException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method
	}

}
