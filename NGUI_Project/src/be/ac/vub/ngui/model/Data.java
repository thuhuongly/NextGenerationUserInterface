/**
 * 
 */
package be.ac.vub.ngui.model;

import java.util.List;

/**
 * @author thuhuongly
 *
 */
public class Data {
	private List<Document> documents;
	private List<Link> links;

	/**
	 * @param documents
	 * @param links
	 */
	public Data(List<Document> documents, List<Link> links) {
		super();
		this.documents = documents;
		this.links = links;
	}

	/**
	 * @return the documents
	 */
	public List<Document> getDocuments() {
		return documents;
	}

	/**
	 * @param documents
	 *            the documents to set
	 */
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	/**
	 * @return the links
	 */
	public List<Link> getLinks() {
		return links;
	}

	/**
	 * @param links
	 *            the links to set
	 */
	public void setLinks(List<Link> links) {
		this.links = links;
	}

}
