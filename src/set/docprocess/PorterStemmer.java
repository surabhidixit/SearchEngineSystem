package set.docprocess;

import java.util.regex.*;

public class PorterStemmer {

	// a single consonant
	private static final String c = "[^aeiou]";
	// a single vowel
	private static final String v = "[aeiouy]";

	// a sequence of consonants; the second/third/etc consonant cannot be 'y'
	private static final String C = c + "[^aeiouy]*";
	// a sequence of vowels; the second/third/etc cannot be 'y'
	private static final String V = v + "[aeiou]*";

	// this regex pattern tests if the token has measure > 0 [at least one VC].
	private static final Pattern mGr0 = Pattern.compile("^(" + C + ")?" + V + C);

	private String finalToken = "";

	// add more Pattern variables for the following patterns:
	// m equals 1: token has measure == 1
	// m greater than 1: token has measure > 1
	// vowel: token has a vowel after the first (optional) C
	// double consonant: token ends in two consonants that are the same,
	// unless they are L, S, or Z. (look up "backreferencing" to help
	// with this)
	// m equals 1, cvc: token is in Cvc form, where the last c is not w, x,
	// or y.

	public String processToken(String token) {
		if (token.length() < 4) {
			return token; // token must be at least 3 chars
		}

		// step 1a
		if (token.endsWith("sses")) {
			token = token.substring(0, token.length() - 2);
		} else if (token.endsWith("ies")) {
			token = token.substring(0, token.length() - 2);
		} else if (!token.matches(".*(ss)") && token.matches(".*(s)")) {
			// }else if (!token.matches("/\bs\b/g")) {
			// System.out.println("frse");
			token = token.substring(0, token.length() - 1);
		}
		// program the other steps in 1a.
		// note that Step 1a.3 implies that there is only a single 's' as the
		// suffix; ss does not count. you may need a regex pattern here for
		// "not s followed by s".

		// step 1b
		boolean doStep1bb = false;
		// step 1b
		if (token.endsWith("eed")) { // 1b.1
			// token.substring(0, token.length() - 3) is the stem prior to
			// "eed".
			// if that has m>0, then remove the "d".

			String stem = stemming(token, 3);
			// stem = stem token.substring(0, token.length() - 3);
			if (mGr0.matcher(stem).find()) { // if the pattern matches the stem
				token = stem + "ee";
			}
		} else if (token.endsWith("ed")) {
			String stem = stemming(token, 2);
			if (Pattern.compile(v).matcher(stem).find()) {
				token = stem;
				doStep1bb = true;
			}

		} else if (token.endsWith("ing")) {
			String stem = stemming(token, 3);
			if (Pattern.compile(v).matcher(stem).find()) {
				token = stem;
				doStep1bb = true;
			}
		}
		// program the rest of 1b. set the boolean doStep1bb to true if Step 1b*
		// should be performed.

		// step 1b*, only if the 1b.2 or 1b.3 were performed.
		if (doStep1bb) {
			if (token.endsWith("at") || token.endsWith("bl") || token.endsWith("iz")) {

				token = token + "e";
			} else if (token.matches("^\\w*[^aoyieu]+[aoyieu]([^aoyieu])\\1")) {
				if (!(token.endsWith("l") || token.endsWith("s") || token.endsWith("z"))) {
					token = token.substring(0, token.length() - 1);
				}
			} else if (token.matches("^\\w*" + c + v + "[^aeiouwxy]") && countM(token) == 1) {
				token = token + "e";
			}
			// use the regex patterns you wrote for 1b*.4 and 1b*.5
		}

		// step 1c
		// program this step. test the suffix of 'y' first, then test the
		// condition *v* on the stem.

		if (token.endsWith("y")) {
			String stem = stemming(token, 1);
			if (Pattern.compile("[aeiouy]").matcher(stem).find()) {
				token = stem + "i";

			}
		}

		// step 2
		// program this step. for each suffix, see if the token ends in the
		// suffix.
		// * if it does, extract the stem, and do NOT test any other suffix.
		// * take the stem and make sure it has m > 0.
		// * if it does, complete the step and do not test any others.
		// if it does not, attempt the next suffix.

		// you may want to write a helper method for this. a matrix of
		// "suffix"/"replacement" pairs might be helpful. It could look like
		// string[][] step2pairs = { new string[] {"ational", "ate"},
		// new string[] {"tional", "tion"}, ....
		finalToken = token;
		char[] tokenCharArray = finalToken.toCharArray();
		switch (tokenCharArray[tokenCharArray.length - 1]) {
		case 'l':
			if (finalToken.endsWith("ational")) {
				stemAndAppend(finalToken, "ate", 7, 0);
				break;
			}
			if (finalToken.endsWith("tional")) {
				stemAndAppend(finalToken, "tion", 6, 0);
				break;
			}
			break;
		case 'i':
			if (finalToken.endsWith("enci")) {
				stemAndAppend(finalToken, "ence", 4, 0);
				break;
			}
			if (finalToken.endsWith("anci")) {
				stemAndAppend(finalToken, "ance", 4, 0);
				break;
			}
			if (finalToken.endsWith("abli")) {
				stemAndAppend(finalToken, "able", 4, 0);
				break;
			}
			if (finalToken.endsWith("alli")) {
				stemAndAppend(finalToken, "al", 4, 0);
				break;
			}
			if (finalToken.endsWith("entli")) {
				stemAndAppend(finalToken, "ent", 5, 0);
				break;
			}
			if (finalToken.endsWith("eli")) {
				stemAndAppend(finalToken, "e", 3, 0);
				break;
			}
			if (finalToken.endsWith("ousli")) {
				stemAndAppend(finalToken, "ous", 5, 0);
				break;
			}
			if (finalToken.endsWith("aliti")) {
				stemAndAppend(finalToken, "al", 5, 0);
				break;
			}
			if (finalToken.endsWith("iviti")) {
				stemAndAppend(finalToken, "ive", 5, 0);
				break;
			}
			if (finalToken.endsWith("biliti")) {
				stemAndAppend(finalToken, "ble", 6, 0);
				break;
			}
			break;
		case 'r':
			if (finalToken.endsWith("izer")) {
				stemAndAppend(finalToken, "ize", 4, 0);
				break;
			}

			break;

		case 'o':
			if (finalToken.endsWith("ization")) {
				stemAndAppend(finalToken, "ize", 7, 0);
				break;
			}
			if (finalToken.endsWith("ation")) {
				stemAndAppend(finalToken, "ate", 5, 0);
				break;
			}
			break;
		case 's':
			if (finalToken.endsWith("iveness")) {
				stemAndAppend(finalToken, "ive", 7, 0);
				break;
			}
			if (finalToken.endsWith("fulness")) {
				stemAndAppend(finalToken, "ful", 7, 0);
				break;
			}
			if (finalToken.endsWith("ousness")) {
				stemAndAppend(finalToken, "ous", 7, 0);
				break;
			}
			break;
		case 't':
			if (finalToken.endsWith("ator")) {
				stemAndAppend(finalToken, "ate", 4, 0);
				break;
			}
			break;
		case 'm':
			if (finalToken.endsWith("alism")) {
				stemAndAppend(finalToken, "al", 5, 0);
				break;
			}
		}

		// step 3
		// program this step. the rules are identical to step 2 and you can use
		// the same helper method. you may also want a matrix here.
		tokenCharArray = finalToken.toCharArray();

		switch (tokenCharArray[tokenCharArray.length - 1]) {
		case 'l':

			if (finalToken.endsWith("ical")) {
				stemAndAppend(finalToken, "ic", 4, 0);
				break;
			}
			if (finalToken.endsWith("ful")) {
				stemAndAppend(finalToken, "", 3, 0);
				break;
			}

			break;
		case 'i':
			if (finalToken.endsWith("iciti")) {
				stemAndAppend(finalToken, "ic", 5, 0);
				break;
			}

			break;
		case 'e':
			if (finalToken.endsWith("icate")) {
				stemAndAppend(finalToken, "ic", 5, 0);
				break;
			}
			if (finalToken.endsWith("ative")) {
				stemAndAppend(finalToken, "", 5, 0);
				break;
			}
			if (finalToken.endsWith("alize")) {
				stemAndAppend(finalToken, "al", 5, 0);
				break;
			}

			break;

		case 's':
			if (finalToken.endsWith("ness")) {
				stemAndAppend(finalToken, "", 4, 0);
				break;
			}
			break;

		}

		// step 4
		// program this step similar to step 2/3, except now the stem must have
		// measure > 1.
		// note that ION should only be removed if the suffix is SION or TION,
		// which would leave the S or T.
		// as before, if one suffix matches, do not try any others even if the
		// stem does not have measure > 1.

		tokenCharArray = finalToken.toCharArray();
		switch (tokenCharArray[tokenCharArray.length - 1]) {
		case 'l':
			if (finalToken.endsWith("al")) {
				stemAndAppend(finalToken, "", 2, 1);
				break;
			}

		case 'e':
			if (finalToken.endsWith("ance")) {
				stemAndAppend(finalToken, "", 4, 1);
				break;
			}
			if (finalToken.endsWith("ence")) {
				stemAndAppend(finalToken, "", 4, 1);
				break;
			}
			if (finalToken.endsWith("able")) {
				stemAndAppend(finalToken, "", 4, 1);
				break;
			}
			if (finalToken.endsWith("ible")) {
				stemAndAppend(finalToken, "", 4, 1);
				break;
			}
			if (finalToken.endsWith("ate")) {
				stemAndAppend(finalToken, "", 3, 1);
				break;
			}
			if (finalToken.endsWith("ive")) {
				stemAndAppend(finalToken, "", 3, 1);
				break;
			}
			if (finalToken.endsWith("ize")) {
				stemAndAppend(finalToken, "", 3, 1);
				break;
			}

		case 'r':
			if (finalToken.endsWith("er")) {
				stemAndAppend(finalToken, "", 2, 1);
				break;
			}
		case 'c':
			if (finalToken.endsWith("ic")) {
				stemAndAppend(finalToken, "", 2, 1);
				break;
			}

		case 't':
			if (finalToken.endsWith("ant")) {
				stemAndAppend(finalToken, "", 3, 1);
				break;
			}
			if (finalToken.endsWith("ement")) {
				stemAndAppend(finalToken, "", 5, 1);
				break;
			}
			if (finalToken.endsWith("ment")) {
				stemAndAppend(finalToken, "", 4, 1);
				break;
			}

			if (finalToken.endsWith("ent")) {
				stemAndAppend(finalToken, "", 3, 1);
				break;
			}
		case 'n':
			if (finalToken.endsWith("sion")) {
				stemAndAppend(finalToken, "", 3, 1);
				break;
			}
			if (finalToken.endsWith("tion")) {
				stemAndAppend(finalToken, "", 3, 1);
				break;
			}

		case 'u':
			if (finalToken.endsWith("ou")) {
				stemAndAppend(finalToken, "", 2, 1);
				break;
			}
		case 'm':
			if (finalToken.endsWith("ism")) {
				stemAndAppend(finalToken, "", 3, 1);
				break;
			}
		case 'i':
			if (finalToken.endsWith("iti")) {
				stemAndAppend(finalToken, "", 3, 1);
				break;
			}
		case 's':
			if (finalToken.endsWith("ous")) {
				stemAndAppend(finalToken, "", 3, 1);
				break;
			}

		}

		// step 5
		// program this step. you have a regex for m=1 and for "Cvc", which
		// you can use to see if m=1 and NOT Cvc.
		// all your code should change the variable finalToken, which represents
		// the stemmed term for the token.
		if (finalToken.endsWith("e")) {
			String stem = stemming(finalToken, 1);
			if (countM(stem) > 1) {
				finalToken = stem;
			} else if (!stem.matches("^\\w*" + c + v + "[^aeiouwxy]") && countM(stem) == 1) {
				finalToken = stem;
			}
		}
		if (countM(finalToken) > 1 && finalToken.matches("^\\w*[^aoyieu]+[aoyieu]([^aoyieu])\\1")) {
			if (finalToken.endsWith("l")) {
				finalToken = finalToken.substring(0, finalToken.length() - 1);
			}
		}

		return finalToken;
	}

	public static String stemming(String token, int length) {
		return token.substring(0, token.length() - length);
	}

	public static int countM(String token) {
		// System.out.println(token);
		int i = 0;
		Matcher match = Pattern.compile(v + c).matcher(token);
		while (match.find()) {
			i++;

		}
		return i;
	}

	private boolean stemAndAppend(String tok, String append, int trimLength, Integer vcPair) {

		String tempStemValue = tok.substring(0, tok.length() - trimLength);
		if (countM(tempStemValue) > vcPair) {
			finalToken = tempStemValue + append;
			return true;
		}

		return false;

	}

	public static String performStepMatching(String[] matchingArray, String token, int size) {
		if (token.endsWith(matchingArray[0])) {
			String stem = stemming(token, matchingArray[0].length());
			if (countM(stem) > size) {
				token = stem + matchingArray[1];
				return token;
			}
			return "false";
		}
		return token;

	}

	public static void main(String[] args) {
		PorterStemmer ps = new PorterStemmer();
		ps.processToken("conformabli");
	}

}
