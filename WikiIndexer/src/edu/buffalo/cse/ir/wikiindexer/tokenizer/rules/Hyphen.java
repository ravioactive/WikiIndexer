package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.HYPHEN)
public class Hyphen implements TokenizerRule {
	
	private static Pattern hyphenPattern = Pattern
			.compile("(\\s+)(\\-+)(\\s+)");

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			String token;
			stream.reset();
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null && token.contains("-")) {
					Matcher m = hyphenPattern.matcher(token);
					if(m.find()){
						stream.previous();
						stream.remove();
						continue;
					}
					
					String[] subStrings = token.split("\\-+");
					if (subStrings.length == 1) {
						token = subStrings[0];
						if(token.equals("")){
							stream.previous();
							stream.remove();
							continue;
						}
					} else if(subStrings.length > 1){
						if (subStrings[0].equals("")) {
							token = subStrings[1];
						} else if ((!subStrings[0].matches(".*[0-9].*"))
								&& (!subStrings[1].matches(".*[0-9].*"))) {
							token = token.replaceFirst("\\s*-", " ");
						}
					} else {
						token = "";
					}
					
					Matcher m2 = hyphenPattern.matcher(token);
					if(m2.find()){
						stream.previous();
						stream.remove();
						continue;
					}
					stream.previous();
					stream.set(token);
					stream.next();
				}
			}
			stream.reset();
		}
	}

}
