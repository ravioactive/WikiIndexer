package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.NUMBERS)
public class Numbers implements TokenizerRule {

	private static Pattern p = Pattern.compile("(\\s*)([0-9]*[.,]?[0-9]+)(\\s*)");
	private static Pattern p1 = Pattern.compile("(\\s*)(\\d+\\.\\d+)(\\s*)");


	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			stream.reset();
			String token = null;
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					Matcher m = p.matcher(token);
					if (m.find()) {
						String replace = "";
						if(m.group(1).equals(" ") || m.group(3).equals(" ")){
							replace = " ";
						}
						token = m.replaceAll(replace);
						if(token.equals("")){
							stream.previous();
							stream.remove();
						}
						stream.previous();
						stream.set(token);
						stream.next();
					}
				}
			}
			stream.reset();
		}
	}

}
