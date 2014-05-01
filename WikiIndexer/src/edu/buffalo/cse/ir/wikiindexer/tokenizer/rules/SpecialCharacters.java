package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.SPECIALCHARS)
public class SpecialCharacters implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			String token = null;
			stream.reset();
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					String tokenData[] = token.split("[\\p{Punct} && [^-.?!]]");
					if (tokenData.length == 1 && tokenData[0].equals("")) {
						stream.previous();
						stream.remove();
					} else if (tokenData.length == 0) {
						stream.previous();
						stream.remove();
					} else {
						stream.previous();
						stream.set(tokenData);
						stream.next();
					}
				}
			}
			stream.reset();
		}
	}
}
