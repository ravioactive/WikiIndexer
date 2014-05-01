package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RuleClass(className = RULENAMES.DATES)
public class Dates implements TokenizerRule {
	private static Set<String> months = new HashSet<String>();
	static {
		months.addAll(Arrays.asList("january", "febrary", "march", "april",
				"may", "june", "july", "august", "september", "october",
				"november", "december"));
	}

	private static Pattern pattern = Pattern
			.compile("(([0-9]{2}:[0-9]{2}(( am| pm)|(am|pm)|(:[0-9]{2}))|[0-9]{1,4})|(january|febrary|march|april|may|june|july|august|september|october|november|december))");

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			String token;
			stream.reset();
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					token = modifyDates(token);
					stream.previous();
					stream.set(token);
					stream.next();
				}
			}
		}
		stream.reset();
		while (stream.hasNext()) {
			System.out.println(stream.next());
		}
	}

	private static String modifyDates(String data) {
		// TODO Auto-generated method stub
		if (data != null) {
			StringBuilder sb = new StringBuilder();

			data = "For instance, the 1948 ABL finalist Baltimore Bullets moved to the BAA and won that league's 1948 title.";

			Matcher matcher = pattern.matcher(data.toLowerCase());
			boolean foundatleastonematch = false;
			boolean hasTime = false;
			int start = 0;
			int end = 0;
			StringBuilder sb1 = new StringBuilder();
			while (matcher.find()) {
				start = matcher.start();
				if (isMonth(matcher.group())) {
					sb.append("MMM ");
				} else if (isNumber(matcher.group()) && isTime(matcher.group())) {
					hasTime = true;
					if (matcher.group().split(":").length == 3) {
						sb.append("HH:mm:ss ");
						continue;
					} else {
						if (matcher.group().split(" ").length > 1)
							sb.append("hh:mm aaa ");
						else
							sb.append("hh:mmaaa ");

					}
					end = matcher.end() - 1;

				} else if (isNumber(matcher.group())) {
					boolean isYear = false;
					if (matcher.group().length() > 2) {
						isYear = true;
						end = matcher.end() - 1;
						sb.append("yyyy ");
						if (matcher.end() + 2 < sb1.length()
								&& checkIfYear(sb1.substring(matcher.start(),
										matcher.end() + 3))) {
							sb.append("G ");
							end = matcher.end() + 2;
						}

						SimpleDateFormat sdf1 = new SimpleDateFormat(sb
								.toString().trim());
						Date d = null;
						try {
							String s2 = sb1.substring(start, end + 1).trim();
							System.out.println(s2);
							d = sdf1.parse(s2);

						} catch (ParseException e) {
							e.printStackTrace();
						}
						SimpleDateFormat requiredDateFormat = null;
						if (hasTime)
							requiredDateFormat = new SimpleDateFormat(
									"yyyyMMdd HH:mm:ss");
						else
							requiredDateFormat = new SimpleDateFormat(
									"yyyyMMdd");
						System.out.println(requiredDateFormat.format(d));
						sb1.append(data.substring(start, end+1));
						sb1.replace(start, end + 1,
								requiredDateFormat.format(d));
						matcher = pattern.matcher(data.substring(end+2));
						continue;

					} else {
						System.out.println("camehere 4");
						isYear = matcher.end() + 2 > sb1.length() ? false
								: checkIfYear(sb1.substring(matcher.start(),
										matcher.end() + 3));
						if (isYear) {
							sb.append("yyyy GGG ");
							end = matcher.end() + 2;
							break;
						} else {
							if (sb1.charAt(matcher.end()) == ',')
								sb.append("dd, ");
							else
								sb.append("dd ");
						}
					}

				}

			}
			SimpleDateFormat sdf1 = new SimpleDateFormat(sb1.toString().trim());
			Date d = null;
			try {
				d = sdf1.parse(sb1.substring(start, end + 1).trim());

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SimpleDateFormat requiredDateFormat = null;
			if (hasTime)
				requiredDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
			else
				requiredDateFormat = new SimpleDateFormat("yyyyMMdd");
			StringBuilder sb2 = new StringBuilder(sb1);
			sb2.replace(start, end + 1, requiredDateFormat.format(d));
			System.out.println(sb2.toString());
		}
		return data;
	}

	private static boolean isTime(String group) {
		if (group.contains(":"))
			return true;
		return false;
	}

	private static boolean isNumber(String s) {
		return Character.isDigit(s.charAt(0));
	}

	private static boolean isMonth(String s) {
		return months.contains(s.toLowerCase());
	}

	private static boolean checkIfYear(String s) {
		if (s.length() > 2 && (s.contains(" AD") || s.contains(" BC")))
			return true;
		return false;
	}

}
