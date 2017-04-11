package set.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * 
 * 
 *         this is Token Bean file used for storing position and document id of
 *         the tokens
 */
public class TokenDetails {
	private int docId;
	private List<Integer> position = new ArrayList<>();

	public TokenDetails(Integer documentID) {
		this.docId = documentID;
	}

	public TokenDetails(int docID2, int wordPosition) {
		this.docId = docID2;
		this.position.add(wordPosition);
	}

	public TokenDetails() {
		// TODO Auto-generated constructor stub
	}

	public int getDocId() {
		return docId;
	}

	public List<Integer> getPosition() {
		return position;
	}

	public void setPosition(int pos) {
		this.position.add(pos);
	}

	@Override
	public String toString() {
		return "docId=" + docId + ", position=" + position;
	}

}
