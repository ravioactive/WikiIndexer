package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.CAPITALIZATION)
public class Capitalization implements TokenizerRule{

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			String token;
			stream.reset();
			boolean isPreviousCamelCase = false;
			int count = 0;
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null && token.length() > 0) {
					if(Character.isUpperCase(token.charAt(0))){
						String tempString = token.length() > 1 ? token.substring(1):"";
						if(count > 0 && isPreviousCamelCase){
							token = token.toUpperCase().charAt(0) + tempString;
							isPreviousCamelCase = true;
						} else if(count == 0){
							token = token.toLowerCase().charAt(0) + tempString;
							isPreviousCamelCase = true;
							count++;
						}
					}
				}
				stream.previous();
				stream.set(token);
				stream.next();
			}
			stream.reset();
		}
	}

}
