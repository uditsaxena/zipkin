package zipkin.storage.sumo;

import com.sumologic.client.model.SearchRequest;
import com.sumologic.client.model.SearchResponse;
import zipkin.BinaryAnnotation;
import zipkin.Codec;
import zipkin.DependencyLink;
import zipkin.Span;
import zipkin.internal.GroupByTraceId;
import zipkin.storage.QueryRequest;
import zipkin.storage.SpanStore;
import zipkin.storage.sumo.api.APIResponseParser;
import zipkin.storage.sumo.api.APIUtils;
import zipkin.storage.sumo.api.SearchAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    // build search request
    SearchRequest searchRequest = APIUtils.buildSearchRequestFromQuery(request);
    // make an api call
    SearchResponse response = apiClient.getResponse(searchRequest);
    // parse responses -
    List<Span> spanList = APIResponseParser.parseAPIResponse(response.getMessages());
    // TRICKY bit - assemble into traces

    List<List<Span>> result = new ArrayList<>();

    for (List<Span> next : GroupByTraceId.apply(spanList, true, true)) {
      if (request.test(next)) {
        result.add(next);
      }
    }
    Collections.sort(result, TRACE_DESCENDING);
    return result;


//    return getLists();
  }

  public static List<List<Span>> getLists() {
    List<Span> spanList = buildSpanList();
    List<List<Span>> result = new ArrayList<>();

    for (List<Span> next : GroupByTraceId.apply(spanList, true, true)) {
      result.add(next);
    }
    Collections.sort(result, TRACE_DESCENDING);
    return result;
  }

  private static List<Span> buildSpanList() {
    List<Span> spanList = new ArrayList<>();
    List<String> stringList = getDummyCorrectResponseList();
    for (String string : stringList) {
      String parsedResponseStr = APIResponseParser.parseResponseStr(string);
      Span span = Codec.JSON.readSpan(parsedResponseStr.getBytes());
      spanList.add(span);
    }
    return spanList;
  }

  private static List<String> getDummyCorrectResponseList() {
    List<String> apiResponseList = new ArrayList<>();

    String s1 = "017-07-12 13:01:31,714 -0700 INFO  [hostId=udit-frontend-1] [module=SERVICE] [localUserName=service] [logger=tracing.zipkin.ZipkinReporter] [thread=qtp718231523-301] [auth=User:usaxena@demo.com:000000000000076A:000000000000009D:false:DefaultSumoSystemUser:-1:USERNAME_PASSWORD] [remote_ip=38.99.50.98] [web_session=4ablvcyf...] [api_session=dDlN8xag...] [remotemodule=service] [execution_interface=UI] {\"traceId\":\"ebb8de53f57e3f67\",\"id\":\"ebb8de53f57e3f67\",\"name\":\"com.sumologic.service.endpoint.ireport.v1.impl.interactivereportservicedelegate.getreport\",\"timestamp\":1499889691586124,\"duration\":127264,\"binaryAnnotations\":[{\"key\":\"lc\",\"value\":\"\",\"endpoint\":{\"serviceName\":\"search\",\"ipv4\":\"172.31.26.159\"}}]}";
    String s2 = "2017-07-12 13:01:31,713 -0700 INFO  [hostId=udit-frontend-1] [module=SERVICE] [localUserName=service] [logger=tracing.zipkin.ZipkinReporter] [thread=qtp718231523-301] [auth=User:usaxena@demo.com:000000000000076A:000000000000009D:false:DefaultSumoSystemUser:-1:USERNAME_PASSWORD] [remote_ip=38.99.50.98] [web_session=4ablvcyf...] [api_session=dDlN8xag...] [remotemodule=service] [execution_interface=UI] {\"traceId\":\"ebb8de53f57e3f67\",\"id\":\"109806871ccd6357\",\"name\":\"com.sumologic.service.endpoint.ireport.v1.impl.interactivereportservicedelegate.getreportinternal\",\"parentId\":\"ebb8de53f57e3f67\",\"timestamp\":1499889691586545,\"duration\":125398,\"binaryAnnotations\":[{\"key\":\"lc\",\"value\":\"\",\"endpoint\":{\"serviceName\":\"search\",\"ipv4\":\"172.31.26.159\"}}]}";
    String s3 = "2017-07-12 13:01:31,651 -0700 INFO  [hostId=udit-frontend-1] [module=SERVICE] [localUserName=service] [logger=tracing.zipkin.ZipkinReporter] [thread=qtp718231523-301] [auth=User:usaxena@demo.com:000000000000076A:000000000000009D:false:DefaultSumoSystemUser:-1:USERNAME_PASSWORD] [remote_ip=38.99.50.98] [web_session=4ablvcyf...] [api_session=dDlN8xag...] [remotemodule=service] [execution_interface=UI] {\"traceId\":\"ebb8de53f57e3f67\",\"id\":\"a97dd7d6770a56f7\",\"name\":\"com.sumologic.service.endpoint.ireport.v1.impl.interactivereportservicedelegate.fromapireport\",\"parentId\":\"109806871ccd6357\",\"timestamp\":1499889691609953,\"duration\":3610,\"binaryAnnotations\":[{\"key\":\"lc\",\"value\":\"\",\"endpoint\":{\"serviceName\":\"search\",\"ipv4\":\"172.31.26.159\"}}]}";

    apiResponseList.add(s1);
    apiResponseList.add(s2);
    apiResponseList.add(s3);

    return apiResponseList;
  }

  @Override
  public List<Span> getTrace(long traceIdHigh, long traceIdLow) {
    return null;
  }

  @Override
  public List<Span> getRawTrace(long traceIdHigh, long traceIdLow) {
    return null;
  }

  @Override
  public List<Span> getTrace(long traceId) {
    return null;
  }

  @Override
  public List<Span> getRawTrace(long traceId) {
    SearchRequest searchRequest = APIUtils.buildSearchRequestFromTraceId(traceId);
    SearchResponse response = apiClient.getResponse(searchRequest);
    // parse responses -
    List<Span> spanList = APIResponseParser.parseAPIResponse(response.getMessages());
    // TRICKY bit - assemble into traces

    List<Span> result = new ArrayList<>();

    for (List<Span> next : GroupByTraceId.apply(spanList, true, true)) {
      result.addAll(next);
    }
    return result;
  }

  @Override
  public List<String> getServiceNames() {
    // Make a default Sumo Search Request
    SearchRequest defaultSearchRequest = APIUtils.buildSearchRequestForServiceNames();
    // Make an API call
    SearchResponse response = apiClient.getResponse(defaultSearchRequest);
    // Parse responses from it
    List<Span> spanList = APIResponseParser.parseAPIResponse(response.getMessages());
    // return a list of service names
    // TODO: 7/14/17 usaxena what to do about duplicate string names ?
    List<String> names = new ArrayList<>();
    for (Span span : spanList) {
      names.addAll(getServiceNamesFromSpan(span));
    }
    return names;
  }

  public List<String> getServiceNamesFromSpan(Span span) {
    List<BinaryAnnotation> binaryAnnotations = span.binaryAnnotations;
    List<String> serviceNameList = new ArrayList<>();
    for (BinaryAnnotation binaryAnnotation : binaryAnnotations) {
      serviceNameList.add(binaryAnnotation.endpoint.serviceName);
    }
    return serviceNameList;
  }

  @Override
  public List<String> getSpanNames(String serviceName) {
    return null;
  }

  @Override
  public List<DependencyLink> getDependencies(long endTs, Long lookback) {
    return null;
  }
}
