/**
 * 
 */
package be.ac.vub.ngui.model;

import java.util.List;

/**
 * @author thuhuongly
 *
 */
public class Publication {
	private String id;
	private String title;
	private List<Name> authors;
	private List<String> keywords;
	private List<String> tags;
	private String abstracts;

	public class Name {
		public String first_name;
		public String last_name;
		/**
		 * @return the first_name
		 */
		public String getFirst_name() {
			return first_name;
		}
		/**
		 * @param first_name the first_name to set
		 */
		public void setFirst_name(String first_name) {
			this.first_name = first_name;
		}
		/**
		 * @return the last_name
		 */
		public String getLast_name() {
			return last_name;
		}
		/**
		 * @param last_name the last_name to set
		 */
		public void setLast_name(String last_name) {
			this.last_name = last_name;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return  first_name + " " + last_name;
		}
		
		
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the authors
	 */
	public List<Name> getAuthors() {
		return authors;
	}

	/**
	 * @param authors
	 *            the authors to set
	 */
	public void setAuthors(List<Name> authors) {
		this.authors = authors;
	}

	/**
	 * @return the keywords
	 */
	public List<String> getKeywords() {
		return keywords;
	}

	/**
	 * @param keywords
	 *            the keywords to set
	 */
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	
	/**
	 * @return the abstracts
	 */
	public String getAbstracts() {
		return abstracts;
	}

	/**
	 * @param abstracts
	 *            the abstracts to set
	 */
	public void setAbstracts(String abstracts) {
		this.abstracts = abstracts;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Publication [id=" + id + ", title=" + title + ", authors=" + authors
				+ ", keywords=" + keywords + ", tags=" + tags + ", abstracts=" + abstracts + "]";
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
}
