package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Users extends Model{

	@OneToMany
	public List<Repo> repos;
	
	public String login;
}
