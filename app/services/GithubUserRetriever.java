package services;

import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import models.GithubUser;
import models.Repo;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.WS;
import play.libs.WS.HttpResponse;

/**
 * Service to crawl an Github like API.
 *
 * @author piettes
 *
 */
public class GithubUserRetriever {

  /*
   * Web service to use.
   */
  private WebServiceWrapper ws;

  /*
   * API Url
   */
  private String serverUrl;

  /*
   * Application Credential for Github.
   */
  private String clientID;

  private String clientSecret;

  public GithubUserRetriever(WebServiceWrapper ws, String serverUrl, String clientID, String clientSecret) {
    this.ws = ws;
    this.serverUrl = serverUrl;
    this.clientID = clientID;
    this.clientSecret = clientSecret;
  }

  /**
   * Get all User and their Repository from a Github-like API
   * 
   * @param organization
   */
  public void getUsersWithRepos(String organization) {

    Session session = (Session) JPA.em().getDelegate();

    List<GithubUser> users = getUsers(session, organization);

    getRepos(users, session);
  }

  /**
   * Get all users
   * 
   * @param session
   * @param organization
   * @return users
   */
  private List<GithubUser> getUsers(Session session, String organization) {

    List<GithubUser> users = new LinkedList<GithubUser>();

    // Build the url
    String url = serverUrl + "/orgs/" + organization + "/members";
    if (clientID != null && clientSecret != null) {
      url += "?client_id=" + clientID + "&client_secret=" + clientSecret;
    }

    // The first call is there to get the JSON containing all Users
    HttpResponse res = ws.get(url);
    JsonElement json = res.getJson();
    if (!json.isJsonArray()) {
      Logger.error("Unable to get members list.");
      Logger.error(json.toString());
      return users;
    }
    json.getAsJsonArray().forEach(elem -> {
      JsonObject obj = elem.getAsJsonObject();
      // create a new GithubUser for each user
      GithubUser user = new GithubUser(obj.get("login").getAsString(), obj.get("repos_url").getAsString());
      session.save(user);
      if (users.size() % 50 == 0) {
        session.flush();
        session.clear();
      }
      users.add(user);
    });
    session.flush();
    session.clear();

    return users;
  }

  /**
   * Get all repos
   * 
   * @param users
   * @param session
   * @return repos
   */
  private List<Repo> getRepos(List<GithubUser> users, Session session) {
    List<Repo> repos = new LinkedList<Repo>();

    // We iterate over all users, and get their Repository list from their
    // repo_url
    users.forEach(user -> {
      String reposUrl = user.reposUrl;
      if (clientID != null && clientSecret != null) {
        reposUrl += "?client_id=" + clientID + "&client_secret=" + clientSecret;
      }

      HttpResponse repoRes = ws.get(reposUrl);
      JsonElement repoJson = repoRes.getJson();

      if (!repoJson.isJsonArray()) {
        Logger.error("Unable to get repo JSON for user %s", user.login);
        Logger.error(repoJson.toString());
      } else {
        repoJson.getAsJsonArray().forEach(elem -> {
          JsonObject obj = elem.getAsJsonObject();
          String name = "";
          if (!obj.get("name").isJsonNull()) {
            name = obj.get("name").getAsString();
          }
          String lang = "";
          if (!obj.get("language").isJsonNull()) {
            lang = obj.get("language").getAsString();
          }
          Repo repo = new Repo(name, lang, user);

          session.save(repo);
          if (repos.size() % 50 == 0) {
            session.flush();
            session.clear();
          }
          repos.add(repo);
        });
      }
    });
    session.flush();
    session.clear();

    return repos;
  }
}
