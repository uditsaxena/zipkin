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
package zipkin.storage.sumo;

import zipkin.storage.AsyncSpanConsumer;
import zipkin.storage.AsyncSpanStore;
import zipkin.storage.SpanStore;
import zipkin.storage.StorageComponent;

import java.io.IOException;

public final class SumoStorage implements StorageComponent {

  private final String accessKey;
  private final String accessId;
  private final boolean strictTraceId;
  private String apiURL;

  @Override
  public SpanStore spanStore() {
    return new SumoSpanStore(this.accessId, this.accessKey, this.apiURL);
  }

  @Override
  public AsyncSpanStore asyncSpanStore() {
    return null;
  }

  @Override
  public AsyncSpanConsumer asyncSpanConsumer() {
    return null;
  }

  @Override
  public CheckResult check() {
    return null;
  }

  @Override
  public void close() throws IOException {

  }

  public static Builder builder() {
    return new Builder();
  }

  public final static class Builder implements StorageComponent.Builder {
    boolean strictTraceId = true;
    private String accessId;
    private String accessKey;
    private String apiURL;

    /**
     * {@inheritDoc}
     */
    @Override
    public Builder strictTraceId(boolean strictTraceId) {
      this.strictTraceId = strictTraceId;
      return this;
    }

    public Builder accessId(String accessId) {
      this.accessId = accessId;
      return this;
    }

    public Builder accessKey(String accessKey) {
      this.accessKey = accessKey;
      return this;
    }

    public Builder apiURL(String apiURL) {
      this.apiURL = apiURL;
      return this;
    }

    @Override
    public SumoStorage build() {
      return new SumoStorage(this);
    }

    Builder() {
    }
  }

  SumoStorage(SumoStorage.Builder builder) {
    this.strictTraceId = builder.strictTraceId;
    this.accessId = builder.accessId;
    this.accessKey = builder.accessKey;
    this.apiURL = builder.apiURL;
  }
}
