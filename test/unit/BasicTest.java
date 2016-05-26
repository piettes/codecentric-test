package unit;
import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

  @Before
  public void setup() {
    Fixtures.deleteDatabase();
  }

  @Test
  public void userCreation() {
    // Having
    GithubUser newUser = new GithubUser("Me", "www.google.com");
    newUser.save();

    // Run
    GithubUser me = GithubUser.find("byLogin", "Me").first();

    // Verify
    assertNotNull(me);
    assertEquals("Me", me.login);
  }

  @Test
  public void userWithRepos() {
    // Having
    GithubUser newUser = new GithubUser("Me", "www.google.com");
    newUser.save();
    Repo repo = new Repo("MyRepo", "C+++", newUser);
    repo.save();

    // Run
    Repo myRepo = Repo.find("byName", "MyRepo").first();

    // Verify
    assertNotNull(myRepo);
    assertEquals("Me", myRepo.owner.login);
  }

  @Test
  public void userStatistics() {
    // Having
    GithubUser newUser1 = new GithubUser("Me", "www.google.com").save();
    new Repo("MyRepo1", "C+++", newUser1).save();
    new Repo("MyRepo2", "C+++", newUser1).save();
    new Repo("MyRepo3", "Java", newUser1).save();
    GithubUser newUser2 = new GithubUser("You", "www.facebook.com").save();
    new Repo("YouRepo1", "Java", newUser2).save();
    new Repo("YouRepo2", "Java", newUser2).save();
    new Repo("YouRepo3", "Java", newUser2).save();
    new Repo("YouRepo4", "", newUser2).save();

    // Run
    List<Object[]> stats = GithubUser.getUserStatistics();

    // Verify
    assertEquals(4, stats.size());
    boolean okMeC = false;
    boolean okYouJava = false;
    boolean okYouEmpty = false;

    for (Object[] line : stats) {
      if ("Me".equals(line[0]) && "C+++".equals(line[1]) && "2".equals(line[2].toString())) {
        okMeC = true;
      }
      if ("You".equals(line[0]) && "Java".equals(line[1]) && "3".equals(line[2].toString())) {
        okYouJava = true;
      }
      if ("You".equals(line[0]) && "".equals(line[1]) && "1".equals(line[2].toString())) {
        okYouEmpty = true;
      }
    }
    assertTrue(okMeC);
    assertTrue(okYouJava);
    assertTrue(okYouEmpty);
  }
  
  @Test
  public void userStatisticsFiltered() {
    // Having
    GithubUser newUser1 = new GithubUser("Me", "www.google.com").save();
    new Repo("MyRepo1", "C+++", newUser1).save();
    new Repo("MyRepo2", "C+++", newUser1).save();
    new Repo("MyRepo3", "Java", newUser1).save();
    GithubUser newUser2 = new GithubUser("You", "www.facebook.com").save();
    new Repo("YouRepo1", "Java", newUser2).save();
    new Repo("YouRepo2", "Java", newUser2).save();
    new Repo("YouRepo3", "Java", newUser2).save();
    new Repo("YouRepo4", "", newUser2).save();
    
    // Run
    List<Object[]> stats = GithubUser.getUserStatisticsFiltered("Java");
    
    // Verify
    assertEquals(2, stats.size());

    assertEquals("You", stats.get(0)[0]);
    assertEquals("Java", stats.get(0)[1]);
    assertEquals("Me", stats.get(1)[0]);
    assertEquals("Java", stats.get(1)[1]);
  }

}
