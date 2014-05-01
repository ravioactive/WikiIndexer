package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.WHITESPACE)
public class WhiteSpace implements TokenizerRule {

	public static String whiteSpaceDelimiter = "[\\s]+";

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			String token = null;
			stream.reset();

			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					String[] tokens = token.trim().split(whiteSpaceDelimiter);
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
