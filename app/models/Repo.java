package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Repo extends Model {

	public String name;
	public String language;
	@ManyToOne
	public GitUser owner;
	
	public Repo(String name, String language, GitUser owner) {
		this.name = name;
		this.language = language;
		this.owner = owner;
	}
}
