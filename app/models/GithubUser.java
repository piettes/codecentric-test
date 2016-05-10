package models;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;

import play.db.jpa.JPA;
import play.db.jpa.Model;
import play.libs.F.Tuple;

/**
 * A Github User. Just have a login and a repo URL.
 * 
 * @author piettes
 *
 */
@Entity
public class GithubUser extends Model {

  public String login;
  public String reposUrl;

  public GithubUser(String login, String reposUrl) {
    this.login = login;
    this.reposUrl = reposUrl;
  }

  /**
   * Get some statistics about users (Language used in their repo)
   * 
   * @return
   */
  public static List<Object[]> getUserStatistics() {
    List<Object[]> lines = JPA.em()
        .createNativeQuery(
            "SELECT GithubUser.login, Repo.language, COUNT(*) FROM GithubUser JOIN Repo on Repo.owner_id = GithubUser.id GROUP BY language, login ORDER BY login, COUNT(*) desc")
        .getResultList();
    return lines;
  }

}
