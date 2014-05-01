/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;

/**
 * A simple map based token view of the transformed document
 * 
 *
 * 
 */
public class IndexableDocument {
	private Map<INDEXFIELD, TokenStream> indexableMap = null;
	private String documentId = null;

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	/**
	 * Default constructor
	 */
	public IndexableDocument() {
		// TODO: Init state as needed
		indexableMap = new HashMap<INDEXFIELD, TokenStream>();
	}

	public IndexableDocument(String id) {
		// TODO: Init state as needed
		indexableMap = new HashMap<INDEXFIELD, TokenStream>();
		documentId = id;
	}

	/**
	 * MEthod to add a field and stream to the map If the field already exists
	 * in the map, the streams should be merged
	 * 
	 * @param field
	 *            : The field to be added
	 * @param stream
	 *            : The stream to be added.
	 */
	public void addField(INDEXFIELD field, TokenStream stream) {
		// TODO: Implement this method
		if (field != null && stream != null) {
			if (indexableMap.containsKey(field)) {
				TokenStream tempStream = indexableMap.get(field);
				tempStream.merge(stream);
				indexableMap.put(field, tempStream);
			} else {
				indexableMap.put(field, stream);
			}
		}
	}

	/**
	 * Method to return the stream for a given field
	 * 
	 * @param key
	 *            : The field for which the stream is requested
	 * @return The underlying stream if the key exists, null otherwise
	 */
	public TokenStream getStream(INDEXFIELD key) {
		// TODO: Implement this method
		return indexableMap.get(key);
	}

	/**
	 * Method to return a unique identifier for the given document. It is left
	 * to the student to identify what this must be But also look at how it is
	 * referenced in the indexing process
	 * 
	 * @return A unique identifier for the given document
	 */
	public String getDocumentIdentifier() {
		// TODO: Implement this method
		return documentId;
	}

}
