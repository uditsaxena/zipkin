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
package zipkin.storage.api;

import com.sumologic.client.model.SearchRequest;
import org.junit.Test;
import zipkin.storage.sumo.api.APIUtils;
import zipkin.storage.sumo.api.SearchAPI;

/**
 * Created by usaxena on 7/14/17.
 */
public class SumoSearchAPITest {

  public static final String API_URL = "https://udit-api.sumologic.net";
  public static final String KEY = "QvqORHYJyztvQyqUmaWl8wJzQdWbMgstnk50Kr1KupAdakRSP1iGf9rS7NqeqSsQ";
  public static final String ID = "suX5wT4cc4fL03";

  @Test
  public void sumoSearchAPITest() {
    SearchAPI api = new SearchAPI(ID, KEY, API_URL);
//    api.getResponse(APIUtils.buildSearchRequestForServiceNames());
  }
  @Test
  public void buildDefaultRequestTestForService() {
//    SearchRequest searchRequest = APIUtils.buildSearchRequestForServiceNames();
//    System.out.println(searchRequest);
  }
}
