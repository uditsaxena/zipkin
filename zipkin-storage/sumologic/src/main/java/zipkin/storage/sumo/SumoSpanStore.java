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

import com.sumologic.client.model.LogMessage;
import com.sumologic.client.model.SearchRequest;
import com.sumologic.client.model.SearchResponse;
import zipkin.BinaryAnnotation;
import zipkin.DependencyLink;
import zipkin.Span;
import zipkin.internal.DependencyLinker;
import zipkin.internal.GroupByTraceId;
import zipkin.internal.Util;
import zipkin.storage.QueryRequest;
import zipkin.storage.SpanStore;
import zipkin.storage.sumo.api.APIResponseParser;
import zipkin.storage.sumo.api.APIUtils;
import zipkin.storage.sumo.api.SearchAPI;

import java.util.*;

import static zipkin.internal.GroupByTraceId.TRACE_DESCENDING;

public class SumoSpanStore implements SpanStore {

  private String accessKey;
  private String accessId;
  private String apiURL;
  private SearchAPI apiClient;

  public SumoSpanStore(String accessId, String accessKey, String apiURL) {
    this.accessKey = accessKey;
    this.accessId = accessId;
    apiClient = new SearchAPI(accessId, accessKey, apiURL);
  }

  @Override
  public List<List<Span>> getTraces(QueryRequest request) {
    SearchRequest searchRequest = APIUtils.buildSearchRequestFromQuery(request);
    List<Span> spanList = fetchFromAPI(searchRequest);

    List<List<Span>> result = new ArrayList<>();

    for (List<Span> next : GroupByTraceId.apply(spanList, true, true)) {
      if (request.test(next)) {
        result.add(next);
      }
    }
    Collections.sort(result, TRACE_DESCENDING);
    return result;
  }

  @Override
  public List<Span> getTrace(long traceIdHigh, long traceIdLow) {
    String traceId = String.valueOf(Util.toLowerHex(traceIdLow));
    SearchRequest searchRequest = APIUtils.buildSearchRequestFromTraceId(traceId);
    List<Span> spanList = fetchFromAPI(searchRequest);

    List<Span> result = new ArrayList<>();

    for (List<Span> next : GroupByTraceId.apply(spanList, true, true)) {
      result.addAll(next);
    }

    // todo: check why we don't need a sort here - ideally we should
    return result;
  }

  @Override
  public List<Span> getRawTrace(long traceIdHigh, long traceIdLow) {
    return getTrace(traceIdHigh, traceIdLow);
  }

  @Override
  public List<Span> getTrace(long traceId) {
    return getTrace(0L, traceId);
  }

  @Override
  public List<Span> getRawTrace(long traceId) {
    String _traceId = "";
    SearchRequest searchRequest = APIUtils.buildSearchRequestFromTraceId(_traceId);
    List<Span> spanList = fetchFromAPI(searchRequest);

    List<Span> result = new ArrayList<>();

    for (List<Span> next : GroupByTraceId.apply(spanList, true, true)) {
      result.addAll(next);
    }
    return result;
  }

  @Override
  public List<String> getServiceNames() {
    SearchRequest defaultSearchRequest = APIUtils.buildSearchRequestForServiceNames();
    List<Span> spanList = fetchFromAPI(defaultSearchRequest);

    Set<String> namesSet = new HashSet<>();
    for (Span span : spanList) {
      namesSet.addAll(getServiceNamesFromSpan(span));
    }
    List<String> names = new ArrayList<>();
    names.addAll(namesSet);
    return names;
  }

  @Override
  public List<String> getSpanNames(String serviceName) {
    return new ArrayList<>();
  }

  @Override
  public List<DependencyLink> getDependencies(long endTs, Long lookback) {
    QueryRequest request = QueryRequest.builder()
      .endTs(endTs)
      .lookback(lookback)
      .limit(Integer.MAX_VALUE).build();

    DependencyLinker linksBuilder = new DependencyLinker();
    for (Collection<Span> trace : getTraces(request)) {
      linksBuilder.putTrace(trace);
    }
    return linksBuilder.link();
  }

  public List<String> getServiceNamesFromSpan(Span span) {
    List<BinaryAnnotation> binaryAnnotations = span.binaryAnnotations;
    List<String> serviceNameList = new ArrayList<>();
    for (BinaryAnnotation binaryAnnotation : binaryAnnotations) {
      serviceNameList.add(binaryAnnotation.endpoint.serviceName);
    }
    return serviceNameList;
  }

  private List<Span> fetchFromAPI(SearchRequest request) {
    // Make an API call
    SearchResponse response = apiClient.getResponse(request);
    // Parse responses from it
    List<LogMessage> messages = response.getMessages();
    List<Span> spanList = APIResponseParser.parseAPIResponse(messages);
    while (messages.size() >= request.getLimit()) {
      request = APIUtils.nextOffset(request);
      response = apiClient.getResponse(request);
      messages = response.getMessages();
      spanList.addAll(APIResponseParser.parseAPIResponse(messages));
    }
    return spanList;
  }

  public String getAccessKey() {
    return accessKey;
  }

  public void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }

  public String getAccessId() {
    return accessId;
  }

  public void setAccessId(String accessId) {
    this.accessId = accessId;
  }

  public String getApiURL() {
    return apiURL;
  }

  public void setApiURL(String apiURL) {
    this.apiURL = apiURL;
  }

  public SearchAPI getApiClient() {
    return apiClient;
  }

  public void setApiClient(SearchAPI apiClient) {
    this.apiClient = apiClient;
  }
}
