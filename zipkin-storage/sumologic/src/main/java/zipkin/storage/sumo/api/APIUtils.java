package zipkin.storage.sumo.api;

import com.sumologic.client.model.SearchRequest;
import zipkin.storage.QueryRequest;

import java.util.Date;

public class APIUtils {

  public static final int DEFAULT_LIMIT = 30;
  public static final int DEFAULT_OFFSET = 0;
  private static final double DEFAULT_FROM = 1;
  private static final String AND_NOT = " AND (_collector=\"udit-*\") AND !(TELEMETRY) AND !(_source=api)";
  private static final String BASIC_QUERY = " ZipkinReporter" + AND_NOT;

  public static SearchRequest buildSearchRequestForServiceNames() {
    Date currentTime = new Date();
    Date from = new Date(currentTime.getTime() - (int) (1000 * 60 * 60 * DEFAULT_FROM));
    Date to = new Date(currentTime.getTime());

    return buildSearchRequest(BASIC_QUERY, from, to, DEFAULT_LIMIT, DEFAULT_OFFSET);
  }

  public static SearchRequest buildSearchRequestFromQuery(QueryRequest request) {
    int limit = request.limit;
    Date to = new Date(request.endTs);
    Date from = new Date(request.endTs - request.lookback);

    String query = "";
    query += request.serviceName + BASIC_QUERY;
    return new SearchRequest(query).withFromTime(from).withToTime(to).withLimit(limit).withOffset(DEFAULT_OFFSET);
  }

  public static SearchRequest buildSearchRequestFromTraceId(String traceId) {
    return buildDefaultSearchRequest(traceId);
  }

  public static SearchRequest nextOffset(SearchRequest request) {
    return new SearchRequest(request.getQuery()).withFromTime(request.getFromTime()).withToTime(request.getToTime())
      .withLimit(request.getLimit()).withOffset(request.getOffset() + DEFAULT_LIMIT);
  }

  private static SearchRequest buildDefaultSearchRequest(String query) {
    Date currentTime = new Date();
    Date from = new Date(currentTime.getTime() - (int) (1000 * 60 * 60 * DEFAULT_FROM));
    Date to = new Date(currentTime.getTime());

    return buildSearchRequest(query, from, to, DEFAULT_LIMIT, DEFAULT_OFFSET);
  }

  private static SearchRequest buildSearchRequest(String query, Date from, Date to, int limit, int offset) {
    return new SearchRequest(query).withFromTime(from).withToTime(to).withLimit(limit).withOffset(offset);
  }
}
