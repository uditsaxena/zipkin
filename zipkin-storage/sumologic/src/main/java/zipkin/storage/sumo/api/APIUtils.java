package zipkin.storage.sumo.api;

import com.sumologic.client.model.SearchRequest;
import zipkin.storage.QueryRequest;

import java.util.Date;

public class APIUtils {

  public static final int DEFAULT_LIMIT = 10;
  public static final int DEFAULT_OFFSET = 0;
  public static final int DEFAULT_FROM = 3;
  public static final int DEFAULT_TO = 0;

  public static SearchRequest buildSearchRequestForServiceNames() {
    String serviceNameQueryStr = "4a8e2dae5a09ab44";
    return buildDefaultSearchRequest(serviceNameQueryStr);
  }

  private static SearchRequest buildDefaultSearchRequest(String query) {
    Date currentTime = new Date();
    Date from = new Date(currentTime.getTime() - (1000 * 60 * 60 * DEFAULT_FROM));
    Date to = new Date(currentTime.getTime());

    return buildSearchRequest(query, from, to, DEFAULT_LIMIT, DEFAULT_OFFSET);
  }

  public static SearchRequest buildSearchRequestFromQuery(QueryRequest request) {
    int limit = request.limit;
    Date to = new Date(request.endTs);
    Date from = new Date(request.endTs - request.lookback);

    String query = "";
    query += request.serviceName + " " + request.spanName;
    return new SearchRequest(query).withFromTime(from).withToTime(to).withLimit(limit).withOffset(DEFAULT_OFFSET);
  }

  public static SearchRequest buildSearchRequestFromTraceId(long traceId) {
    return buildDefaultSearchRequest(String.valueOf(traceId));
  }

  private static SearchRequest buildSearchRequest(String query, Date from, Date to, int limit, int offset) {
    return new SearchRequest(query).withFromTime(from).withToTime(to).withLimit(limit).withOffset(offset);
  }
}
