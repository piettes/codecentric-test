package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.JPA;
import play.db.jpa.Model;

/**
 * Represent a Git Repository
 * 
 * @author piettes
 *
 */
@Entity
public class Repo extends Model {

  public String name;
  public String language;

  @ManyToOne
  public GithubUser owner;

  public Repo(String name, String language, GithubUser owner) {
    this.name = name;
    this.language = language;
    this.owner = owner;
  }
  
  public static List<String> getAvailableLanguages() {
    List<String> langs = JPA.em()
        .createNativeQuery(
            "SELECT DISTINCT Repo.language FROM Repo")
        .getResultList();
    return langs;
  }
}
