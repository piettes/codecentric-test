package services;

import play.libs.WS;
import play.libs.WS.HttpResponse;

/**
 * Wrapper for a webservice. For testing/mocking purpose
 * 
 * @author piettes
 *
 */
public class WebServiceWrapper {

  public HttpResponse get(String url) {
    return WS.url(url).get();
  }

}
