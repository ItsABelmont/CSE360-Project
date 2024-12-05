/**
 * <p> JUnit_ArticleOrganization Class </p>
 * 
 * <p> Description: A JUnit demonstration of testing the article organization of the Article class </p>
 * 
 * @author Reem Helal
 * 
 * @version 1.00	2024-12-04	A set of semi-automated tests
 * 
 */
package edu.asu.DatabasePart1;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import java.util.ArrayList;

public class JUnit_ArticleOrganization {

	@Test
	// create a list of articles to test filtering by the title
	public void testSetAndFilterContentLevel() {
		List<Article> articles = new ArrayList<>();
	    articles.add(new Article(1, "Beginner Java", "Tu3", "Author3", "Abstract3", "Keyword3", "Body3", "References3"));
	    articles.add(new Article(2, "Intermediate Python", "Tu2", "Author2", "Abstract2", "Keyword2", "Body2", "References2"));
	    
	    // filter articles with "Beginner" in the title
	    List<Article> beginnerArticles = new ArrayList<>();
	    for (Article article : articles) {
	        if (article.title.contains("Beginner")) {
	            beginnerArticles.add(article);
	        }
	    }
	    assertEquals(1, beginnerArticles.size());
	    assertEquals("Beginner Java", beginnerArticles.get(0).title);
	}
	
	@Test
	//ensures that articles can be searched using specific keywords
	public void testSearchArticleUsingKeyword() {
		Article article = new Article(1, "Java", null, "Jack Frost", "AbstractArticle", "Java, Project", "Body Article", "References");
	    // Validate keywords that contain the search term
	    assertTrue(article.keywords.contains("Java"));
	    assertFalse(article.keywords.contains("C++")); 
	}

	@Test
	//ensures that articles can be filtered based on their group
	public void testFilterArticlesByGroup() {
		Article article1 = new Article(1, "Article1", "Tu3", "Author1", "Abstract1", "Keyword1", "Body1", "Refrences1");
		Article article2 = new Article(2, "Article2", "Tu2", "Author2", "Abstract2", "Keyword2", "Body2", "Refrences2");
		assertEquals("Tu3", article1.group);
		assertEquals("Tu2", article2.group);
	}
	
}
