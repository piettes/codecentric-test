package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class GitUser extends Model {

	public String login;
	public String reposUrl; 
	
	public GitUser(String login, String reposUrl) {
		this.login = login;
		this.reposUrl = reposUrl;
	}

}
