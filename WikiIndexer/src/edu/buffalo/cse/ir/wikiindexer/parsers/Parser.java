/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.parsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaParser;

/**
 *
 * 
 */
public class Parser extends DefaultHandler {
	/* */
	private final Properties props;
	private Integer idFromXml = null;
	private String authorFromXml = null;
	private String timestampFromXml = null;
	private String ttl = null;
	private String text = null;
	private String tempString = null;
	private boolean inRevisionTag = false;
	WikipediaDocument tempWikiDoc = null;
	private Collection<WikipediaDocument> wikiDocQueue = null;

	/**
	 * 
	 * @param idxConfig
	 * @param parser
	 */
	public Parser(Properties idxProps) {
		props = idxProps;
	}

	boolean isID(String id) {
		return "id".equalsIgnoreCase(id);
	}

	boolean isRevision(String revision) {
		return "revision".equalsIgnoreCase(revision);
	}

	boolean isTitle(String title) {
		return "title".equalsIgnoreCase(title);
	}

	boolean isAuthor(String author) {
		return "username".equalsIgnoreCase(author)
				|| "ip".equalsIgnoreCase(author);
	}

	boolean isTimeStamp(String timeStamp) {
		return "timeStamp".equalsIgnoreCase(timeStamp);
	}

	boolean isPage(String page) {
		return "page".equalsIgnoreCase(page);
	}

	boolean isText(String text) {
		return "text".equalsIgnoreCase(text);
	}

	/* TODO: Implement this method */
	/**
	 * 
	 * @param filename
	 * @param docs
	 */
	public void parse(String filename, Collection<WikipediaDocument> docs) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser xmlParser = factory.newSAXParser();
			this.wikiDocQueue = docs;
			if (filename != null && filename != "") {
                xmlParser.parse(filename, this);
			}

			docs = wikiDocQueue;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to add the given document to the collection. PLEASE USE THIS
	 * METHOD TO POPULATE THE COLLECTION AS YOU PARSE DOCUMENTS For better
	 * performance, add the document to the collection only after you have
	 * completely populated it, i.e., parsing is complete for that document.
	 * 
	 * @param doc
	 *            : The WikipediaDocument to be added
	 * @param documents
	 *            : The collection of WikipediaDocuments to be added to
	 */
	private synchronized void add(WikipediaDocument doc,
			Collection<WikipediaDocument> documents) {
		documents.add(doc);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (isPage(qName)) {
			tempWikiDoc = null;
		}
		if (isRevision(qName)) {
			inRevisionTag = true;
		}
		tempString = new String();
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (isPage(qName)) {
			try {
				WikipediaParser wikiParser = new WikipediaParser(authorFromXml,
						timestampFromXml, idFromXml, ttl, text);
				tempWikiDoc = wikiParser.getWikipediaDocument();
				add(tempWikiDoc, wikiDocQueue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (isRevision(qName)) {
			inRevisionTag = false;
		}
		if (isID(qName) && !inRevisionTag) {
			idFromXml = null;
			idFromXml = Integer.parseInt(tempString);
		}
		if (isTitle(qName)) {
			ttl = null;
			ttl = tempString;
		}
		if (isAuthor(qName)) {
			authorFromXml = null;
			authorFromXml = tempString;
		}
		if (isTimeStamp(qName)) {
			timestampFromXml = null;
			timestampFromXml = tempString;
		}
		if (isText(qName)) {
			text = null;
			text = tempString;
		}
		tempString = null;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		tempString += new String(ch, start, length);
	}

}
