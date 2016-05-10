package services;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import models.GithubUser;
import models.Repo;
import play.libs.WS.HttpResponse;
import play.test.Fixtures;
import play.test.UnitTest;

public class UserServiceTest extends UnitTest {

  /**
   * Test File init : drop database and read entries from local JSON files
   * 
   * @throws JsonIOException
   * @throws JsonSyntaxException
   * @throws FileNotFoundException
   */
  @BeforeClass
  public static void setup() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
    Fixtures.deleteDatabase();

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
    new GithubUserRetriever(ws, "www.test.com", null , null).getUsersWithRepos("test");
  }

  @Test
  public void userCreation() {
    assertEquals(2, GithubUser.count());
    assertEquals(3, Repo.count());
    GithubUser user = GithubUser.find("byLogin", "Bob").first();
    assertNotNull(user);
    List<Repo> repos = Repo.find("byOwner", user).fetch();
    assertEquals(2, repos.size());

    Repo repo = Repo.find("byName", "AliceProject").first();
    assertEquals(repo.language, "Scala");
    assertEquals(repo.owner.login, "Alice");
  }

  @Test
  public void repoCreation() {
    assertEquals(3, Repo.count());
    Repo repo = Repo.find("byName", "AliceProject").first();
    assertEquals(repo.language, "Scala");
    assertEquals(repo.owner.login, "Alice");
  }

  @Test
  public void repoWithUserCreation() {
    GithubUser user = GithubUser.find("byLogin", "Bob").first();
    assertNotNull(user);
    List<Repo> repos = Repo.find("byOwner", user).fetch();
    assertEquals(2, repos.size());
  }
}
