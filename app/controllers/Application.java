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

  /**
   * Entry point of the app. Just get the user statistics and render the index
   */
  public static void index(String filter) {
    List<Object[]> stats;
    if (filter == null) {
      stats = GithubUser.getUserStatistics();
    } else {
      stats = GithubUser.getUserStatisticsFiltered(filter);
    }
    List<String> langs = Repo.getAvailableLanguages();
    render(stats, langs);
  }

  /**
   * To initialize the application. Get all users from organization codecentric
   * on Github
   */
  public static void init() {
    // The Github API is limited to 50 call per hour for unauth request.
    String clientID = "a1b4f53dc80d2a8b25d0";
    String clientSecret = "f897ef476d9cd914074cce1adc43134adedf8ccf";

    GithubUserRetriever userRetriever = new GithubUserRetriever(new WebServiceWrapper(), "https://api.github.com", clientID, clientSecret);

    userRetriever.getUsersWithRepos("codecentric");
    // redirect to index
    index(null);
  }

}