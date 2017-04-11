package set.docprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import set.beans.TokenDetails;
//import set.gui.MainJFrame;

public class Indexing {
	// biWord index list

	// PI index list
	private HashMap<String, List<TokenDetails>> mIndex = new HashMap<>();
	public HashMap<String, List<TokenDetails>> getmIndex() {
		return mIndex;
	}


	/**
	 * processes the token by removing all alphanumeric from first and last position
	 * removing all singles quotes from the query
	 * converting into lowercase
	 * @param next
	 * @return
	 */
	public String processWord(String next) {

        return next.trim().replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "").trim().toLowerCase();
	}

	/**
	 * maps Positon of the token and documentId to the tokenDetail Object 
	 * and stores it in hashmap
	 * @param term
	 * @param docID
	 * @param wordPosition
	 */
	public void addTermInvertedIndex(String term, int docID, int wordPosition) {

		List<TokenDetails> list = new ArrayList<>();
		TokenDetails docList = new TokenDetails(docID, wordPosition);

		try {

			if (mIndex.containsKey(term)) {
				list = mIndex.get(term);

				docList = list.get(list.size() - 1);
				if (docList.getDocId() == docID) {
					docList.setPosition(wordPosition);
				} else {

					docList = new TokenDetails(docID, wordPosition);
					list.add(docList);
					mIndex.put(term, list);

				}
			} else {

				list.add(docList);
				mIndex.put(term, list);
			}

		} catch (Exception e) {
			Logger.getLogger(Indexing.class.getName()).log(java.util.logging.Level.SEVERE, null,e);
			System.out.print("addTerm  " + e);
		}

	}


	/**
	 * returns list of documents id containing the term for PI index
	 * @param term
	 * @return
	 */
	public List<TokenDetails> getInvertedIndexPostings(String term) {
		// TO-DO: return the postings list for the given term from the index
		// map.
		return mIndex.get(term);

	}

	/**
	 * returns size of the PI index
	 * @return
	 */
	public int getTermCountPII() {
		// TO-DO: return the number of terms in the index.

		return mIndex.size();
	}

	/**
	 * returns complete list of the vocabulary term of the PI index
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String[] getInvertedIndexDictionary() {
		// TO-DO: fill an array of Strings with all the keys from the hashtable.
		// Sort the array and return it.
		PorterStemmer ps=new PorterStemmer();
		String[] dictionary = new String[mIndex.size()];
		Iterator it = mIndex.entrySet().iterator();
		int i = 0;
        Pattern p=Pattern.compile("\\s+");
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			if(!(p.matcher(pair.getKey().toString()).find()))
            dictionary[i] =pair.getKey().toString();
			i++;
		}
		Arrays.sort(dictionary);
        String[] sortedDictionary = new String[dictionary.length-1];
        System.arraycopy(dictionary, 1, sortedDictionary, 0, dictionary.length-1);

		return sortedDictionary;
	}

}
