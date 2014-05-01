/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;

/**
 *
 * This class is used to introspect a given index
 * The expectation is the class should be able to read the index
 * and all associated dictionaries.
 */
public class IndexReader {

    private INDEXFIELD indexType;
    private Properties properties;

    private Dictionary feildDictionary = null;
    private Dictionary documentDictionary = null;
    private Index feildIndex = null;
    private boolean inverted = false;

	/**
	 * Constructor to create an instance 
	 * @param props: The properties file
	 * @param field: The index field whose index is to be read
	 */
	public IndexReader(Properties props, INDEXFIELD field) {
		//TODO: Implement this method
        properties = props;
        indexType = field;
        init();
	}

    public void init() {
        try {
            respawnIndex();
        } catch (IndexerException e) {
            e.printStackTrace();
            System.err.println("Bad Initialization. Could not read index.");
        }
    }

    private void respawnDictionary(INDEXFIELD dictionaryType) throws IndexerException {
        String filePath = properties.getProperty("root.dir", System.getProperty("user.dir"));
        String dictionaryFileName = (dictionaryType == INDEXFIELD.LINK)?"DOC":dictionaryType.toString();
        String fileName = filePath + File.separator +  IndexerConstants.DICTIONARY_FILE_PREFIX + dictionaryFileName;
        File dictionaryFile = new File(fileName);
        Charset cs = Charset.forName("ASCII");
        try {
            FileChannel dictChannel = new FileInputStream(dictionaryFile).getChannel();
            byte[] byteArray = new byte[256 * 1024];
            ByteBuffer buffer = ByteBuffer.wrap(byteArray);
            int numBytesRead = -1;
            StringBuilder dictionaryBuilder = new StringBuilder();
            while((numBytesRead = dictChannel.read(buffer)) != -1) {
                buffer.flip();
                CharBuffer chBuf = cs.decode(buffer);
                dictionaryBuilder.append(chBuf.toString());
                parseDictionaryFromFile(dictionaryBuilder, dictionaryType);
                buffer.clear();
            }

            dictChannel.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getStackTrace());
            throw new IndexerException(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getStackTrace());
            throw new IndexerException(e.getMessage());
        }
    }

    private void parseDictionaryFromFile(StringBuilder dictionaryBuilder, INDEXFIELD dictionaryType) {
        Dictionary workingCopy;

        if(dictionaryType == INDEXFIELD.LINK) {
            if(documentDictionary == null) {
                documentDictionary = new SharedDictionary(properties, dictionaryType);
            }
            workingCopy = documentDictionary;
        } else {
            if(feildDictionary == null) {
                feildDictionary = new LocalDictionary(properties, dictionaryType);
            }
            workingCopy = feildDictionary;
        }

        List<String> dictionaryEntries = new ArrayList<String>(Arrays.asList(dictionaryBuilder.toString().split("\\|")));

        String incompleteEntry = null;
        if(dictionaryBuilder.charAt(dictionaryBuilder.length()-1) != '|') {
            incompleteEntry = new String(dictionaryEntries.get(dictionaryEntries.size()-1));
            dictionaryEntries.remove(dictionaryEntries.size()-1);
        }

        Iterator<String> dictionaryEntryIterator = dictionaryEntries.iterator();
        boolean set = false;
        while(dictionaryEntryIterator.hasNext()) {
            String entry = dictionaryEntryIterator.next();
            if(!entry.isEmpty()) {
                set = false;
                String[] kvPair = entry.split(",");
                try {
                    if((kvPair!=null && !kvPair[0].isEmpty()) && (kvPair[1]!=null && !kvPair[1].isEmpty())) {
                        set = workingCopy.setValue(kvPair[0], Integer.parseInt(kvPair[1]));
                    }
                } catch(NumberFormatException nfe) {
                    System.err.println("Skipping for NumberFormatException while parsing key:"+kvPair[0]+", value="+kvPair[1]);
                    continue;
                }

                if(!set) {
                    System.err.println("Dictionary value not set for key:"+kvPair[0]+", value="+kvPair[1]);
                }
            }
        }

        dictionaryBuilder.delete(0, dictionaryBuilder.length());
        if(incompleteEntry!=null) {
            dictionaryBuilder.append(incompleteEntry);
        }
    }

    private void respawnIndex() throws IndexerException {
        if(documentDictionary == null) {
            respawnDictionary(INDEXFIELD.LINK);
        }

        if(feildDictionary == null) {
            respawnDictionary(indexType);
        }

        if(documentDictionary == null || feildDictionary == null) {
            return;
        }

        String filePath = properties.getProperty("root.dir", System.getProperty("user.dir"));
        String fileName = filePath + File.separator +  IndexerConstants.INDEX_FILE_PREFIX + indexType.toString();

        File indexFile = new File(fileName);
        Charset cs = Charset.forName("ASCII");
        try {
            FileChannel idxChannel = new FileInputStream(indexFile).getChannel();
            byte[] byteArray = new byte[256*1024];
            ByteBuffer buffer = ByteBuffer.wrap(byteArray);
            int numBytesRead = -1;
            StringBuilder indexBuilder = new StringBuilder();

            while((numBytesRead = idxChannel.read(buffer)) != -1) {
                buffer.flip();
                CharBuffer chBuf = cs.decode(buffer);
                indexBuilder.append(chBuf.toString());
                parseIndexFromFile(indexBuilder);
                buffer.clear();
            }
            idxChannel.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getStackTrace());
            throw new IndexerException(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getStackTrace());
            throw new IndexerException(e.getMessage());
        }
    }

    private void parseIndexFromFile(StringBuilder indexBuilder) {
        if(feildIndex == null) {
            feildIndex = new Index(feildDictionary, properties, indexType);
        }

        List<String> indexEntries = new ArrayList<String>(Arrays.asList(indexBuilder.toString().split("\\|")));

        String incompleteEntry = null;
        if(indexBuilder.charAt(indexBuilder.length()-1) != '|') {
            incompleteEntry = indexEntries.get(indexEntries.size()-1);
            indexEntries.remove(indexEntries.size()-1);
        }

        Iterator<String> indexEntryIterator = indexEntries.iterator();
        boolean added = false;
        while(indexEntryIterator.hasNext()) {
            String entry = indexEntryIterator.next();
            String[] kvPair = entry.split("-");

            if((kvPair[0]!=null && !kvPair[0].isEmpty()) && (kvPair[1]!=null && !kvPair[1].isEmpty())) {
                try {
                    int keyValue = Integer.parseInt(kvPair[0]);
                    String[] postingsLists = kvPair[1].split(",");
                    if(postingsLists!=null) {
                        int numPostings = postingsLists.length;

                        if(numPostings>0) {
                            for(int i=0;i<numPostings;i++) {

                                if(postingsLists[i]!=null && !postingsLists[i].isEmpty()) {

                                    String[] positingPair = postingsLists[i].split(":");

                                    if((positingPair[0]!=null && !positingPair[0].isEmpty()) &&
                                            (positingPair[1]!=null && !positingPair[1].isEmpty())) {

                                        try {
                                            GenericIndexEntry indexEntry = new GenericIndexEntry(keyValue,
                                                    Integer.parseInt(positingPair[0]), Integer.parseInt(positingPair[1]), inverted);
                                            added = feildIndex.add(indexEntry);
                                        } catch (NumberFormatException nfe) {
                                            System.err.println("Skipping due to NumberFormatException in parsing docId: "
                                                    +positingPair[0]+", frequency: "+positingPair[1]+" for key: "+keyValue);
                                            continue;
                                        }
                                    }
                                }
                            }
                        }
                    }

                } catch (NumberFormatException nfe) {
                    System.err.println("Skipping due to NumberFormatException in parsing key: "+kvPair[0]);
                    continue;
                }
            }


            if(!added) {
                System.err.println("Index value not set for key:"+kvPair[0]+", value="+kvPair[1]);
            }
        }

        indexBuilder.delete(0, indexBuilder.length());
        if(incompleteEntry!=null) {
            indexBuilder.append(incompleteEntry);
        }
    }


    /**
	 * Method to get the total number of terms in the key dictionary
	 * @return The total number of terms as above
	 */
	public int getTotalKeyTerms() {
        //TODO: Implement this method
        int totalKeyTerms = -1;
        if(feildDictionary != null) {
            totalKeyTerms = feildDictionary.getTotalTerms();
        }
		return totalKeyTerms;
	}
	
	/**
	 * Method to get the total number of terms in the value dictionary
	 * @return The total number of terms as above
	 */
	public int getTotalValueTerms() {
        //TODO: Implement this method
		int totalValueTerms = -1;
        if(documentDictionary != null) {
            totalValueTerms = documentDictionary.getTotalTerms();
        }
		return totalValueTerms;
	}
	
	/**
	 * Method to retrieve the postings list for a given dictionary term
	 * @param key: The dictionary term to be queried
	 * @return The postings list with the value term as the key and the
	 * number of occurrences as value. An ordering is not expected on the map
	 */
	public Map<String, Integer> getPostings(String key) {
		//TODO: Implement this method
        Map<String, Integer> postings = null;
        if(feildIndex!=null) {
            postings = feildIndex.getIndexCountFor(key);
        }
		return postings;
	}
	
	/**
	 * Method to get the top k key terms from the given index
	 * The top here refers to the largest size of postings.
	 * @param k: The number of postings list requested
	 * @return An ordered collection of dictionary terms that satisfy the requirement
	 * If k is more than the total size of the index, return the full index and don't 
	 * pad the collection. Return null in case of an error or invalid inputs
	 */
	public Collection<String> getTopK(int k) {
        //TODO: Implement this method
        Collection<String> topK = null;
        if(feildIndex!=null) {
            topK = feildIndex.getTopK_Keys(k);
        }
		return topK;
	}
	
	/**
	 * Method to execute a boolean AND query on the index
	 * @param terms The terms to be queried on
	 * @return An ordered map containing the results of the query
	 * The key is the value field of the dictionary and the value
	 * is the sum of occurrences across the different postings.
	 * The value with the highest cumulative count should be the
	 * first entry in the map.
	 */
	public Map<String, Integer> query(String... terms) {
		//TODO: Implement this method (FOR A BONUS)
		return null;
	}
}
