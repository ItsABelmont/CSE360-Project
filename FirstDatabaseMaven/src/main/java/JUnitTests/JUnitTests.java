package JUnitTests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;

import edu.asu.DatabasePart1.DatabaseHelper;
import edu.asu.DatabasePart1.GUI;

import org.junit.jupiter.api.Test;

public class JUnitTests {
	String testHelper = "";
	DatabaseHelper databaseHelper = new DatabaseHelper();
	
	JUnitTests() {
		try {
			databaseHelper.connectToDatabase();
		}
		catch (SQLException e) {
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*@Test
	public void T1() {
		testHelper = "";
		databaseHelper.forEachArticle((id, titleArticle, group, author, abstrac, keywords, body, references, i) -> {
			testHelper += id + titleArticle + group + author + abstrac + keywords + body + references;
		});
		
		databaseHelper.backupArticles("testBackupFile");
		databaseHelper.restoreArticles("testBackupFile");
		
		String oldTestHelper = testHelper;
		testHelper = "";
		databaseHelper.forEachArticle((id, titleArticle, group, author, abstrac, keywords, body, references, i) -> {
			testHelper += id + titleArticle + group + author + abstrac + keywords + body + references;
		});
		assertEquals(oldTestHelper, testHelper);
	}
	
	@Test
	public void T2() {
		testHelper = "";
		databaseHelper.forEachArticle((id, titleArticle, group, author, abstrac, keywords, body, references, i) -> {
			testHelper += id + titleArticle + group + author + abstrac + keywords + body + references;
		});
		
		databaseHelper.groupBackupArticles("testBackupFile", "1");
		databaseHelper.mergeArticles("testBackupFile");
		
		String oldTestHelper = testHelper;
		testHelper = "";
		databaseHelper.forEachArticle((id, titleArticle, group, author, abstrac, keywords, body, references, i) -> {
			testHelper += id + titleArticle + group + author + abstrac + keywords + body + references;
		});
		//System.out.println(oldTestHelper + "\n" + testHelper);
		assertEquals(oldTestHelper, testHelper);
	}*/
	
	/*@Test
	public void T3() {
		testHelper = "";
		databaseHelper.forEachUserAccess((userEmail, userRole, adminRights, groupName) -> {
			testHelper += userEmail + userRole + adminRights + groupName;
		});
		
		databaseHelper.addUserAccessSpecial("gcrl", "instructor", "NewGroup");
		databaseHelper.removeUserAccessSpecial("gcrl", "instructor", "NewGroup");
		
		String oldString = testHelper;
		testHelper = "";
		databaseHelper.forEachUserAccess((userEmail, userRole, adminRights, groupName) -> {
			testHelper += userEmail + userRole + adminRights + groupName;
		});
		
		assertEquals(oldString , testHelper);
	}*/
	
	/*@Test
	public void T4() {
		testHelper = "";
		databaseHelper.forEachUserAccess((userEmail, userRole, adminRights, groupName) -> {
			testHelper += userEmail + userRole + adminRights + groupName;
		});
		
		databaseHelper.addUserAccessSpecial("gcrl", "admin", "NewGroup");
		databaseHelper.removeUserAccessSpecial("gcrl", "admin", "NewGroup");
		
		String oldString = testHelper;
		testHelper = "";
		databaseHelper.forEachUserAccess((userEmail, userRole, adminRights, groupName) -> {
			testHelper += userEmail + userRole + adminRights + groupName;
		});
		
		assertEquals(oldString , testHelper);
	}*/
	
	/*@Test
	public void T5() {
		testHelper = "";
		databaseHelper.forEachUserAccess((userEmail, userRole, adminRights, groupName) -> {
			testHelper += userEmail + userRole + adminRights + groupName;
		});
		
		databaseHelper.addUserAccessViewSpecial("gcrl", "admin", "NewGroup");
		databaseHelper.removeUserAccessSpecial("gcrl", "admin", "NewGroup");
		
		String oldString = testHelper;
		testHelper = "";
		databaseHelper.forEachUserAccess((userEmail, userRole, adminRights, groupName) -> {
			testHelper += userEmail + userRole + adminRights + groupName;
		});
		System.out.println(oldString + "\n" + testHelper);
		assertEquals(oldString , testHelper);
	}*/
	
	/*@Test
	public void T6() {
		ArrayList<String> groups = new ArrayList<String>();
		
		databaseHelper.forEachArticle((id, titleArticle, group, author, abstrac, keywordsArticle, body, references, i) -> {
			boolean test = false;
			
			//Add displayed groups to the list of groups at the top
			for (String str : groups) {
				if (str == group) {
					test = true;
					break;
				}
			}
			if (!test)
				groups.add(group);
		});
		
		testHelper = "";
		for (String i : groups) {
			testHelper += i;
		}
		
		databaseHelper.addArticle("title", "NEWGROUP", "author", "abstract", "beginner keywords", "body", "references");
		
		databaseHelper.deleteGroup("NEWGROUP");
		
		String oldString = testHelper;
		
		ArrayList<String> groups2 = new ArrayList<String>();
		
		databaseHelper.forEachArticle((id, titleArticle, group, author, abstrac, keywordsArticle, body, references, i) -> {
			boolean test = false;
			
			//Add displayed groups to the list of groups at the top
			for (String str : groups2) {
				if (str == group) {
					test = true;
					break;
				}
			}
			if (!test)
				groups2.add(group);
		});
		
		testHelper = "";
		for (String i : groups2) {
			testHelper += i;
		}
		
		assertEquals(oldString, testHelper);
	}
	
	@Test
	public void T7() {
		ArrayList<String> groups = new ArrayList<String>();
		
		databaseHelper.forEachArticle((id, titleArticle, group, author, abstrac, keywordsArticle, body, references, i) -> {
			boolean test = false;
			
			//Add displayed groups to the list of groups at the top
			for (String str : groups) {
				if (str == group) {
					test = true;
					break;
				}
			}
			if (!test)
				groups.add(group);
		});
		
		testHelper = "";
		for (String i : groups) {
			testHelper += i;
		}
		
		databaseHelper.addArticle("title", "NEWGROUP", "author", "abstract", "beginner keywords", "body", "references");
		databaseHelper.addArticle("title", "NEWGROUP2", "author", "abstract", "beginner keywords", "body", "references");
		
		databaseHelper.deleteGroup("NEWGROUP");
		databaseHelper.deleteGroup("NEWGROUP2");
		
		String oldString = testHelper;
		
		ArrayList<String> groups2 = new ArrayList<String>();
		
		databaseHelper.forEachArticle((id, titleArticle, group, author, abstrac, keywordsArticle, body, references, i) -> {
			boolean test = false;
			
			//Add displayed groups to the list of groups at the top
			for (String str : groups2) {
				if (str == group) {
					test = true;
					break;
				}
			}
			if (!test)
				groups2.add(group);
		});
		
		testHelper = "";
		for (String i : groups2) {
			testHelper += i;
		}
		
		assertEquals(oldString, testHelper);
	}*/
	
	@Test
	public void T8() {
ArrayList<String> groups = new ArrayList<String>();
		
		databaseHelper.forEachArticle((id, titleArticle, group, author, abstrac, keywordsArticle, body, references, i) -> {
			boolean test = false;
			
			//Add displayed groups to the list of groups at the top
			for (String str : groups) {
				if (str == group) {
					test = true;
					break;
				}
			}
			if (!test)
				groups.add(group);
		});
		
		testHelper = "";
		for (String i : groups) {
			testHelper += i;
		}
		
		databaseHelper.addArticle("title", "NEWGROUP", "author", "abstract", "beginner keywords", "body", "references");
		
		String oldString = testHelper;
		
		ArrayList<String> groups2 = new ArrayList<String>();
		
		databaseHelper.forEachArticle((id, titleArticle, group, author, abstrac, keywordsArticle, body, references, i) -> {
			boolean test = false;
			
			//Add displayed groups to the list of groups at the top
			for (String str : groups2) {
				if (str == group) {
					test = true;
					break;
				}
			}
			if (!test)
				groups2.add(group);
		});
		
		testHelper = "";
		for (String i : groups2) {
			testHelper += i;
		}
		
		assertEquals(oldString + "NEWGROUP", testHelper);
	}
}
