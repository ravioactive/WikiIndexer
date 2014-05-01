package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.text.Normalizer;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.ACCENTS)
public class Accents implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			stream.reset();
			while (stream.hasNext()) {
				String token = stream.next();
				if (token != null) {
					if (!Normalizer.isNormalized(token, Normalizer.Form.NFD)) {
						token = Normalizer
								.normalize(token, Normalizer.Form.NFD);
						token = token.replaceAll("[^\\p{ASCII}]", "");
					} else {
						token = token.replaceAll("а̀", "a");
					}
					//System.out.println(token);
					stream.previous();
					stream.set(token);
					stream.next();
				}
			}
			stream.reset();
		}
	}
}
