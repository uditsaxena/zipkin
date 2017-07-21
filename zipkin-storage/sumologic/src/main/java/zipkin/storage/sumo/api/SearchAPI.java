/**
 * Copyright 2015-2017 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
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
