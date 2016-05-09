package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Repo extends Model{

	public String language;
}
