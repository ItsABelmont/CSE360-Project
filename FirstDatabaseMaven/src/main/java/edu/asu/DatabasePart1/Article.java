package edu.asu.DatabasePart1;

/**
 * A class for holding all the information of an article
 * @author Just Wise
 *
 */
public class Article {
	//Every piece of article information is contained in these variables
	public int id;
	String title; 
	String group;
	String authors;  
	String abstrac;
	String keywords;
	String body;
	String references;
	
	/**
	 * Creates a new Article
	 * @param id
	 * @param title
	 * @param group
	 * @param authors
	 * @param abstrac
	 * @param keywords
	 * @param body
	 * @param references
	 */
	public Article(int id, String title, String group, String authors, String abstrac, String keywords, String body, String references) {
		this.id = id;
		this.title = title;
		this.group = group;
		this.authors = authors;
		this.abstrac = abstrac;
		this.keywords = keywords;
		this.body = body;
		this.references = references;
	}
}
