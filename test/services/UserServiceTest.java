package services;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import models.GitUser;
import models.Repo;
import play.libs.WS.HttpResponse;
import play.test.Fixtures;
import play.test.UnitTest;

public class UserServiceTest extends UnitTest {

	@Before
    public void setup() {
        Fixtures.deleteDatabase();
    }
	
	@Test
	public void userCreation() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		// Having
		JsonElement jsonUsers = new JsonParser().parse(new FileReader(new File("test/services/UserServiceTestUsers.json")));
		JsonElement jsonRepo1 = new JsonParser().parse(new FileReader(new File("test/services/UserServiceTestRepo1.json")));
		JsonElement jsonRepo2 = new JsonParser().parse(new FileReader(new File("test/services/UserServiceTestRepo2.json")));
		
		WebServiceWrapper ws = mock(WebServiceWrapper.class);
		HttpResponse respUsers = mock(HttpResponse.class);
		when(respUsers.getJson()).thenReturn(jsonUsers);
		when(ws.get("www.test.com/orgs/test/members")).thenReturn(respUsers);
		
		HttpResponse respRepo1 = mock(HttpResponse.class);
		when(respRepo1.getJson()).thenReturn(jsonRepo1);
		when(ws.get("bobRepoUrl")).thenReturn(respRepo1);
		HttpResponse respRepo2 = mock(HttpResponse.class);
		when(respRepo2.getJson()).thenReturn(jsonRepo2);
		when(ws.get("aliceRepoUrl")).thenReturn(respRepo2);
		
		// Run 
		new GithubUserRetriever(ws, "www.test.com").getUsers("test");
		
		// Verify
		assertEquals(2, GitUser.count());
		assertEquals(3, Repo.count());
		GitUser user = GitUser.find("byLogin", "Bob").first();
		assertNotNull(user);
		List<Repo> repos = Repo.find("byOwner", user).fetch();
		assertEquals(2, repos.size());
		
		Repo repo = Repo.find("byName", "AliceProject").first();
		assertEquals(repo.language, "Scala");
		assertEquals(repo.owner.login, "Alice");
		
	}
}
