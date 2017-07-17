package zipkin.storage.sumo.api;

import com.sumologic.client.Credentials;
import com.sumologic.client.SumoLogicClient;
import com.sumologic.client.model.SearchRequest;
import com.sumologic.client.model.SearchResponse;

import java.net.MalformedURLException;

public class SearchAPI {

  private SumoLogicClient sumoClient = null;

  public SearchAPI(String accessId, String accessKey, String apiURL) {
    Credentials credential = new Credentials(accessId, accessKey);
    sumoClient = new SumoLogicClient(credential);
    try {
//      sumoClient.setURL("https://udit-api.sumologic.net");
      sumoClient.setURL(apiURL);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  public SearchResponse getResponse(SearchRequest searchRequest) {
    if (sumoClient == null || searchRequest == null) {
      return null;
    }
    SearchResponse searchResponse = sumoClient.search(searchRequest);
    return searchResponse;
  }
}
