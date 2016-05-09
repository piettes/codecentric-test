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
		GitUser newUser = new GitUser("Me", "www.google.com");
		newUser.save();

		// Run
		GitUser me = GitUser.find("byLogin", "Me").first();

		// Verify
		assertNotNull(me);
		assertEquals("Me", me.login);
	}

	@Test
	public void userWithRepos() {
		// Having
		GitUser newUser = new GitUser("Me", "www.google.com");
		newUser.save();
		Repo repo = new Repo("MyRepo", "C+++", newUser);
		repo.save();

		// Run
		Repo myRepo = Repo.find("byName", "MyRepo").first();

		// Verify
		assertNotNull(myRepo);
		assertEquals(myRepo.owner.login, "Me");
	}

}
