/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument.Section;

/**
 * A Callable document transformer that converts the given WikipediaDocument
 * object into an IndexableDocument object using the given Tokenizer
 * 
 *
 * 
 */
public class DocumentTransformer implements Callable<IndexableDocument> {
	private Map<INDEXFIELD, Tokenizer> tokenizerMap = null;
	private WikipediaDocument wikiDoc = null;
	private TokenStream linkTokenStream = null;
	private TokenStream authorTokenStream = null;
	private TokenStream categoryTokenStream = null;
	private TokenStream termTokenStream = null;

	/**
	 * Default constructor, DO NOT change
	 * 
	 * @param tknizerMap
	 *            : A map mapping a fully initialized tokenizer to a given field
	 *            type
	 * @param doc
	 *            : The WikipediaDocument to be processed
	 */
	public DocumentTransformer(Map<INDEXFIELD, Tokenizer> tknizerMap,
			WikipediaDocument doc) {
		// TODO: Implement this method
		this.tokenizerMap = tknizerMap;
		this.wikiDoc = doc;
		if (doc.getAuthor() != null) {
			authorTokenStream = new TokenStream(doc.getAuthor());
		}
		List<Section> sections = doc.getSections();
		if (sections != null && sections.size() > 0) {
			termTokenStream = new TokenStream(doc.getTitle());
			Iterator<Section> iterator = sections.iterator();
			while (iterator.hasNext()) {
				Section section = iterator.next();
				termTokenStream.append(section.getTitle(), section.getText());
			}
		}
		List<String> categories = doc.getCategories();
		if (categories != null && categories.size() > 0) {
			Iterator<String> iterator = categories.iterator();
			while (iterator.hasNext()) {
				String category = iterator.next();
				if (categoryTokenStream == null) {
					categoryTokenStream = new TokenStream(category);
				} else {
					categoryTokenStream.append(category);
				}
			}
		}
		Set<String> links = doc.getLinks();
		if (links != null && links.size() > 0) {
			Iterator<String> iterator = links.iterator();
			while (iterator.hasNext()) {
				String link = iterator.next();
				if (linkTokenStream == null) {
					linkTokenStream = new TokenStream(link);
				} else {
					linkTokenStream.append(link);
				}
			}
		}

	}

	/**
	 * Method to trigger the transformation
	 * 
	 * @throws TokenizerException
	 *             Inc ase any tokenization error occurs
	 */
	public IndexableDocument call() throws TokenizerException {
		// TODO Implement this methodString termTokenStream = null;
		IndexableDocument indexDoc = null;
		if (tokenizerMap != null && wikiDoc != null && tokenizerMap.size() > 0) {
			if (tokenizerMap.containsKey(INDEXFIELD.TERM)) {
				Tokenizer tokenizer = tokenizerMap.get(INDEXFIELD.TERM);
				if (tokenizer != null && termTokenStream != null) {
					if (indexDoc == null) {
						indexDoc = new IndexableDocument();
					}
					tokenizer.tokenize(termTokenStream);
					indexDoc.addField(INDEXFIELD.TERM, termTokenStream);
				}
			}

            if (tokenizerMap.containsKey(INDEXFIELD.CATEGORY)) {
				Tokenizer tokenizer = tokenizerMap.get(INDEXFIELD.CATEGORY);
				if (tokenizer != null && categoryTokenStream != null) {
					if (indexDoc == null) {
						indexDoc = new IndexableDocument();
					}
					tokenizer.tokenize(categoryTokenStream);
					indexDoc.addField(INDEXFIELD.CATEGORY, categoryTokenStream);
				}
			}

            if (tokenizerMap.containsKey(INDEXFIELD.LINK)) {
				Tokenizer tokenizer = tokenizerMap.get(INDEXFIELD.LINK);
				if (tokenizer != null && linkTokenStream != null) {
					if (indexDoc == null) {
						indexDoc = new IndexableDocument();
					}
					tokenizer.tokenize(linkTokenStream);
					indexDoc.addField(INDEXFIELD.LINK, linkTokenStream);
				}
			}

            if (tokenizerMap.containsKey(INDEXFIELD.AUTHOR)) {
				Tokenizer tokenizer = tokenizerMap.get(INDEXFIELD.AUTHOR);
				if (tokenizer != null && authorTokenStream != null) {
					if (indexDoc == null) {
						indexDoc = new IndexableDocument();
					}
					tokenizer.tokenize(authorTokenStream);
					indexDoc.addField(INDEXFIELD.AUTHOR, authorTokenStream);
				}
			}

			if (indexDoc != null) {
				indexDoc.setDocumentId("" + wikiDoc.getTitle());
			}
		}
		return indexDoc;
	}

}
