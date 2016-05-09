package services;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import models.GitUser;
import models.Repo;
import play.libs.WS;
import play.libs.WS.HttpResponse;

public class GithubUserRetriever {
	
	private WebServiceWrapper ws;

	private String serverUrl;

	public GithubUserRetriever(WebServiceWrapper ws, String serverUrl) {
		this.ws = ws;
		this.serverUrl = serverUrl;
	}

	public void getUsers(String organization) {
		HttpResponse res = ws.get(serverUrl + "/orgs/" + organization + "/members");
		JsonElement json = res.getJson();
		List<GitUser> users = new LinkedList<GitUser>();
		json.getAsJsonArray().forEach(elem -> {
			JsonObject obj = elem.getAsJsonObject();
			GitUser user = new GitUser(obj.get("login").getAsString(), obj.get("repos_url").getAsString());
			// TODO batch save
			user.save();
			users.add(user);
		});
		
		List<Repo> repos = new LinkedList<Repo>();
		users.forEach(user -> {
			HttpResponse repoRes = ws.get(user.reposUrl);
			JsonElement repoJson = repoRes.getJson();
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
				repos.add(repo);
				// TODO batch save
				repo.save();
			});
		});
	}
}
