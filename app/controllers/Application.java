package controllers;

import play.*;
import play.db.jpa.GenericModel.JPAQuery;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.*;
import services.GithubUserRetriever;
import services.WebServiceWrapper;

import java.util.*;
import java.util.function.Consumer;

import javax.persistence.EntityManager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import models.*;

public class Application extends Controller {

	public static void index() {
		List<GitUser> users = GitUser.findAll();
		render(users);
	}

	public static void init() {
		new GithubUserRetriever(new WebServiceWrapper(), "https://api.github.com").getUsers("codecentric");
		index();
	}

}