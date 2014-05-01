package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.APOSTROPHE)
public class Apostrophe implements TokenizerRule {

	static private Map<String, String> contractedTerms = new LinkedHashMap<String, String>();
	static {
		contractedTerms.put("can't", "can not");
		contractedTerms.put("won't", "will not");
		contractedTerms.put("shan't", "shall not");
		contractedTerms.put("let's", "let us");
		contractedTerms.put("'m", "am");
		contractedTerms.put("'re", "are");
		contractedTerms.put("n't", "not");
		contractedTerms.put("'ll", "will");
		contractedTerms.put("'d", "would");
		contractedTerms.put("'ve", "have");
		contractedTerms.put("'em", "them");

	}
	static private String[] terms = contractedTerms.keySet().toArray(
			new String[contractedTerms.size()]);

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			String token;
			stream.reset();
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					for (String term : terms) {
						token = token.replaceAll(term,
								" " + contractedTerms.get(term));
					}
					token = token.replaceAll("'s", "");
					token = token.replaceAll("s'", "s");
					token = token.replaceAll("'", "");
					String tokens[] = token.split("\\s");
					for (int i = 0; i < (tokens.length / 2); i++) {
						String temp = tokens[i];
						tokens[i] = tokens[tokens.length - i - 1];
						tokens[tokens.length - i - 1] = temp;
					}
					stream.previous();
					stream.set(tokens);
					stream.next();
				}
			}
		}
		stream.reset();
	}
}
