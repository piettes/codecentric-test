package services;

import play.libs.WS;
import play.libs.WS.HttpResponse;

public class WebServiceWrapper {
	
	public HttpResponse get(String url) {
		return WS.url(url).get();
	}
	
}
