package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

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
}
