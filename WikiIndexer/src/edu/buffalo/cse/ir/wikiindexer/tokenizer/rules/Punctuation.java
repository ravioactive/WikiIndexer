package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.glassfish.external.statistics.annotations.Reset;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.PUNCTUATION)
public class Punctuation implements TokenizerRule {

	private static Pattern punctuationPattern = Pattern
			.compile("([\\.\\!\\?])(.*)");

	public Punctuation() {

	}

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			String token = null;
			stream.reset();
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					token = token.replaceAll("[\\.\\!\\?]+$", "");
					Matcher matcher = punctuationPattern.matcher(token);
					while (matcher.find()) {
						String pattern = matcher.group(2);
						if (pattern.length() > 0 && pattern.charAt(0) == ' ') {
							token = token.replaceFirst("[\\.\\!\\?]", "");
						}
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
