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
package zipkin.autoconfigure.storage.sumo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import zipkin.storage.sumo.SumoStorage;

import java.io.Serializable;

@ConfigurationProperties("zipkin.storage.sumo")
public class ZipkinSumoStorageProperties implements Serializable {

  private String accessId;
  private String accessKey;
  private String apiURL;

  public String getAccessId() {
    return accessId;
  }

  public void setAccessId(String accessId) {
    this.accessId = accessId;
  }

  public String getAccessKey() {
    return accessKey;
  }

  public void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }

  public String getApiURL() {
    return apiURL;
  }

  public void setApiURL(String apiURL) {
    this.apiURL = apiURL;
  }

  public SumoStorage.Builder toBuilder() {
    return SumoStorage.builder().accessId(accessId).accessKey(accessKey).apiURL(apiURL);
  }
}
