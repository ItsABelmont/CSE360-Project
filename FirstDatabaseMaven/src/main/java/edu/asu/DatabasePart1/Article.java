package edu.asu.DatabasePart1;

/**
 * </p> Article Class </p>
 * 
 * </p> Description: A class for holding all the information of an article </p>
 * 
 * @author Just Wise
 *
 * @version 1.00	2024-10-29	This class contains article functionality
 * 
 */
public class Article {
	//Every piece of article information is contained in these variables
	public long id;
	String title; 
	String group;
	String authors;  
	String abstrac;
	String keywords;
	String body;
	String references;
	boolean special = false;
	
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
	public Article(long id, String title, String group, String authors, String abstrac, String keywords, String body, String references) {
		this.id = id;
		this.title = title;
		this.group = group;
		this.authors = authors;
		this.abstrac = abstrac;
		this.keywords = keywords;
		this.body = body;
		this.references = references;
	}
	
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
	public Article(long id, String title, String group, String authors, String abstrac, String keywords, String body, String references, boolean special) {
		this.id = id;
		this.title = title;
		this.group = group;
		this.authors = authors;
		this.abstrac = abstrac;
		this.keywords = keywords;
		this.body = body;
		this.references = references;
		this.special = special; 
	}
}
