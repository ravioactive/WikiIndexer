/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 *
 * This class is used to write an index to the disk
 * 
 */
public class IndexWriter implements Writeable {

    private Properties properties;
    private int partitionNumber;
    private int maxPartitions;

    //Type dependent index variable
    private LocalDictionary fieldDictionary = null;
    private Index fieldIndex = null;

    private String starter = "!";
    private String separator = "|";
    private String entrySeparator = "-";
    private String postingsSeparator = ",";
    private String frequencySeparator = ":";
    private String indexName = null;
    private INDEXFIELD type = null;
    private String filepath = null;
    private String fileName = null;

	/**
	 * Constructor that assumes the underlying index is inverted
	 * Every index (inverted or forward), has a key field and the value field
	 * The key field is the field on which the postings are aggregated
	 * The value field is the field whose postings we are accumulating
	 * For term index for example:
	 * 	Key: Term (or term id) - referenced by TERM INDEXFIELD
	 * 	Value: Document (or document id) - referenced by LINK INDEXFIELD
	 * @param props: The Properties file
	 * @param keyField: The index field that is the key for this index
	 * @param valueField: The index field that is the value for this index
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField, INDEXFIELD valueField) {
		this(props, keyField, valueField, false);
	}
	
	/**
	 * Overloaded constructor that allows specifying the index type as
	 * inverted or forward
	 * Every index (inverted or forward), has a key field and the value field
	 * The key field is the field on which the postings are aggregated
	 * The value field is the field whose postings we are accumulating
	 * For term index for example:
	 * 	Key: Term (or term id) - referenced by TERM INDEXFIELD
	 * 	Value: Document (or document id) - referenced by LINK INDEXFIELD
	 * @param props: The Properties file
	 * @param keyField: The index field that is the key for this index
	 * @param valueField: The index field that is the value for this index
	 * @param isForward: true if the index is a forward index, false if inverted
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField, INDEXFIELD valueField, boolean isForward) {
		//TODO: Implement this method
        properties = props;
        partitionNumber = -1;
        maxPartitions = Partitioner.getNumPartitions();
        filepath = properties.getProperty("root.dir", System.getProperty("user.dir"));
        type = keyField;
        indexName = keyField.toString();
        fileName = filepath + File.separator + "index_" + indexName;
        init(props, keyField, valueField, isForward);
    }

    private void init(Properties properties, INDEXFIELD keyField, INDEXFIELD valueField, boolean isForward) {
        fieldDictionary = new LocalDictionary(properties, keyField);
        fieldIndex = new Index(fieldDictionary, properties, keyField, valueField, isForward);
    }

	/**
	 * Method to make the writer self aware of the current partition it is handling
	 * Applicable only for distributed indexes.
	 * @param pnum: The partition number
	 */
	public void setPartitionNumber(int pnum) {
		partitionNumber = pnum;
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * @param keyId: The id for the key field, pre-converted
	 * @param valueId: The id for the value field, pre-converted
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, int valueId, int numOccurances) throws IndexerException {
		//TODO: Implement this method
        //add in dictionary to convert this to int, regardless of the data type (int/string)
        fieldIndex.addWithDictionaryLookup(keyId, valueId, numOccurances);
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param keyId: The id for the key field, pre-converted
	 * @param value: The value for the value field
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, String value, int numOccurances) throws IndexerException {
		//TODO: Implement this method
        fieldIndex.addWithDictionaryLookup(keyId, value, numOccurances);
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param key: The key for the key field
	 * @param valueId: The id for the value field, pre-converted
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(String key, int valueId, int numOccurances) throws IndexerException {
		//TODO: Implement this method
        fieldIndex.addWithDictionaryLookup(key, valueId, numOccurances);
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param key: The key for the key field
	 * @param value: The value for the value field
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(String key, String value, int numOccurances) throws IndexerException {
        fieldIndex.addWithDictionaryLookup(key, value, numOccurances);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		// TODO Implement this method

        if(type != INDEXFIELD.LINK) {
            fieldDictionary.writeToDisk();
        }

        try {

            File indexFile = new File(fileName);
            FileChannel idxChannel = new FileOutputStream(indexFile, true).getChannel();
            int block_entry_counts = 250;


            StringBuilder content = new StringBuilder();

            int count = 0;
            Iterator<Map.Entry<Integer,Map<Integer,GenericIndexEntry>>> fieldIndexIterator = fieldIndex.getIndexMapQueue()
                                                                                                .entrySet().iterator();
            Iterator<Map.Entry<Integer, GenericIndexEntry>> valueIterator = null;

            while(fieldIndexIterator.hasNext()) {
                count = 0;

                while(fieldIndexIterator.hasNext() && count < block_entry_counts) {
                    Map.Entry<Integer,Map<Integer,GenericIndexEntry>> entry = fieldIndexIterator.next();
                    content.append(entry.getKey()).append(entrySeparator);
                    Map<Integer, GenericIndexEntry> value = entry.getValue();
                    valueIterator = value.entrySet().iterator();

                    while(valueIterator.hasNext()) {
                        Map.Entry<Integer, GenericIndexEntry> valueEntry = valueIterator.next();
                        content.append(valueEntry.getKey());
                        if(valueEntry.getValue().getFrequency()>0) {
                            content.append(frequencySeparator).append(valueEntry.getValue().getFrequency());
                        }
                        content.append(postingsSeparator);
                    }
                    content.append(separator);
                    ++count;
                }

                byte[] byteArray = new byte[256 * 1024];
                ByteBuffer buffer = ByteBuffer.wrap(byteArray);
                buffer.clear();
                buffer.put(content.toString().getBytes());
                buffer.flip();
                while(buffer.hasRemaining()) {
                    idxChannel.write(buffer);
                }

                content.delete(0, content.length());
            }

            idxChannel.force(true);
            idxChannel.close();

        } catch (FileNotFoundException e) {
            System.err.println(e.getStackTrace());
            throw new IndexerException(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getStackTrace());
            throw new IndexerException(e.getMessage());
        }
    }

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method

	}

}
