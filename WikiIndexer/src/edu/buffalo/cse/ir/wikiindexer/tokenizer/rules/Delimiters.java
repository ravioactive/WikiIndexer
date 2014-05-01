package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.DELIM)
public class Delimiters implements TokenizerRule {

	private String delimiter = null;

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getDelimiter() {
		return this.delimiter;
	}

	/*public Delimiters() {
		this.delimiter = "[\\s]+";
	}*/
	
	public Delimiters(String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null && delimiter != null) {
			String token = null;
			stream.reset();
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					String[] tokens = token.trim().split(delimiter);
					if (stream.getAllTokens().size() > 1) {
						stream.previous();
					}
					stream.set(tokens);
					stream.next();
				}
			}
			stream.reset();
		}
	}

}
