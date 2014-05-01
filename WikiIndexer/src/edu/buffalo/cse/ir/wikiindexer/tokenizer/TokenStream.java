/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * This class represents a stream of tokens as the name suggests. It wraps the
 * token stream and provides utility methods to manipulate it
 * 
 *
 * 
 */
public class TokenStream implements Iterator<String> {

	private List<String> tokenList = null;
	private ListIterator<String> iterator = null;

	/**
	 * Default constructor
	 * 
	 * @param bldr
	 *            : THe stringbuilder to seed the stream
	 */
	public TokenStream(StringBuilder bldr) {
		// TODO: Implement this method
		tokenList = new ArrayList<String>();
		iterator = tokenList.listIterator();
		if (bldr != null && !bldr.equals("")) {
			iterator.add(bldr.toString());
		}

	}

	/**
	 * Overloaded constructor
	 * 
	 * @param bldr
	 *            : THe stringbuilder to seed the stream
	 */
	public TokenStream(String string) {
		// TODO: Implement this method
		tokenList = new ArrayList<String>();
		iterator = tokenList.listIterator();
		if (string != null && !string.equals("")) {
			iterator.add(string);
		}
	}

	/**
	 * Method to append tokens to the stream
	 * 
	 * @param tokens
	 *            : The tokens to be appended
	 */
	public void append(String... tokens) {
		// TODO: Implement this method
		if (tokens != null) {
			int index = iterator.nextIndex() - 1;
			for (String token : tokens) {
				if (token != null && !token.equals("")) {
					tokenList.add(token);
				}
			}
			if (index < 0) {
				iterator = tokenList.listIterator();
			} else {
				iterator = tokenList.listIterator(index);
			}
		}
	}

	/**
	 * Method to retrieve a map of token to count mapping This map should
	 * contain the unique set of tokens as keys The values should be the number
	 * of occurrences of the token in the given stream
	 * 
	 * @return The map as described above, no restrictions on ordering
	 *         applicable
	 */
	public Map<String, Integer> getTokenMap() {
		Map<String, Integer> tokenMap = new HashMap<String, Integer>();
		ListIterator<String> tempIterator = tokenList.listIterator();
		while (tempIterator.hasNext()) {
			String temp = tempIterator.next();
			int value = 1;
			if (tokenMap.containsKey(temp)) {
				value = tokenMap.get(temp);
				value++;
			}
			tokenMap.put(temp, value);
		}
		if (tokenMap.size() <= 0) {
			tokenMap = null;
		}
		return tokenMap;
	}

	/**
	 * Method to get the underlying token stream as a collection of tokens
	 * 
	 * @return A collection containing the ordered tokens as wrapped by this
	 *         stream Each token must be a separate element within the
	 *         collection. Operations on the returned collection should NOT
	 *         affect the token stream
	 */
	public Collection<String> getAllTokens() {
		// TODO: Implement this method
		if (tokenList.size() > 0) {
			String[] tokens = tokenList.toArray(new String[tokenList.size()]);
			return new ArrayList<String>(Arrays.asList(tokens));
		}
		return null;
	}

	/**
	 * Method to query for the given token within the stream
	 * 
	 * @param token
	 *            : The token to be queried
	 * @return: THe number of times it occurs within the stream, 0 if not found
	 */
	public int query(String token) {
		int searchCount = 0;
		if (token != null && !token.equals("")) {
			ListIterator<String> tempIterator = tokenList.listIterator();
			while (tempIterator.hasNext()) {
				String temp = tempIterator.next();
				if ((temp).equals(token)) {
					searchCount++;
				}
			}
		}

		return searchCount;
	}

	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * 
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasNext() {
		// TODO: Implement this method
		return iterator.hasNext();
	}

	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * 
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasPrevious() {
		// TODO: Implement this method
		return iterator.hasPrevious();
	}

	/**
	 * Iterator method: Method to get the next token from the stream Callers
	 * must call the set method to modify the token, changing the value of the
	 * token returned by this method must not alter the stream
	 * 
	 * @return The next token from the stream, null if at the end
	 */
	public String next() {
		// TODO: Implement this method
		String data = null;
		if (iterator.hasNext()) {
			data = iterator.next();
		}
		return data;
	}

	/**
	 * Iterator method: Method to get the previous token from the stream Callers
	 * must call the set method to modify the token, changing the value of the
	 * token returned by this method must not alter the stream
	 * 
	 * @return The next token from the stream, null if at the end
	 */
	public String previous() {
		// TODO: Implement this method
		String data = null;
		if (iterator.hasPrevious()) {
			data = iterator.previous();
		}
		return data;
	}

	/**
	 * Iterator method: Method to remove the current token from the stream
	 */
	public void remove() {
		// TODO: Implement this method
		int index = iterator.nextIndex();
		if (tokenList.size() > 0 && index < tokenList.size()) {
			tokenList.remove(index);
			iterator = tokenList.listIterator(index);
		} else if (tokenList.size() == 1) {
			tokenList.remove(0);
			iterator = tokenList.listIterator();
		} else {
			iterator = tokenList.listIterator();
		}
	}

	/**
	 * Method to merge the current token with the previous token, assumes
	 * whitespace separator between tokens when merged. The token iterator
	 * should now point to the newly merged token (i.e. the previous one)
	 * 
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithPrevious() {
		boolean success = false;
		if (iterator.hasPrevious() && iterator.hasNext()) {
			String presentString = iterator.next();
			iterator.previous();
			String nextToken = iterator.previous();
			set(nextToken + " " + presentString);
			iterator.next();
			iterator.next();
			iterator.remove();
			iterator.previous();
			success = true;
		}
		return success;
	}

	/**
	 * Method to merge the current token with the next token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the current one)
	 * 
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithNext() {
		boolean success = false;
		if (iterator.hasNext()) {
			String presentString = iterator.next();
			if (iterator.hasNext()) {
				String nextToken = iterator.next();
				iterator.previous();
				iterator.remove();
				iterator.previous();
				set(presentString + " " + nextToken);
				if (tokenList.size() == 1) {
					iterator = tokenList.listIterator();
				}
				success = true;
			}
		}
		return success;
	}

	/**
	 * Method to replace the current token with the given tokens The stream
	 * should be manipulated accordingly based upon the number of tokens set It
	 * is expected that remove will be called to delete a token instead of
	 * passing null or an empty string here. The iterator should point to the
	 * last set token, i.e, last token in the passed array.
	 * 
	 * @param newValue
	 *            : The array of new values with every new token as a separate
	 *            element within the array
	 */
	public void set(String... newValue) {
		if (newValue != null) {
			if (tokenList.size() > 0) {
				int index = 0;
				int tokenIndex = iterator.nextIndex();
				for (String token : newValue) {
					if (token != null && !token.equals("")
							&& (iterator.hasNext() || tokenList.size() == 1)) {
						if (index < 1 && tokenList.size() == 1) {
							tokenList.add(token);
							tokenList.remove(0);
							iterator = tokenList.listIterator();
							iterator.next();
							index++;
						} else { 
							if (index < 1) {
								remove();
								index++;
							}
							tokenList.add(tokenIndex, token);
							iterator = tokenList.listIterator(tokenIndex);
							tokenIndex++;
						}
					}
				}
			}
		}
	}

	/**
	 * Iterator method: Method to reset the iterator to the start of the stream
	 * next must be called to get a token
	 */
	public void reset() {
		iterator = tokenList.listIterator(0);
	}

	/**
	 * Iterator method: Method to set the iterator to beyond the last token in
	 * the stream previous must be called to get a token
	 */
	public void seekEnd() {
		iterator = tokenList.listIterator(tokenList.size());
	}

	/**
	 * Method to merge this stream with another stream
	 * 
	 * @param other
	 *            : The stream to be merged
	 */
	public void merge(TokenStream other) {
		if (other != null && other.getAllTokens() != null) {
			int index = iterator.nextIndex();
			int oldCount = tokenList.size();
			tokenList.addAll(other.getAllTokens());
			if (oldCount > 0 && index < oldCount) {
				iterator = tokenList.listIterator(index);
			} else {
				iterator = tokenList.listIterator();
			}
		}
	}
}
