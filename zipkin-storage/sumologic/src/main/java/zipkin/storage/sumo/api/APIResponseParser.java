package zipkin.storage.sumo.api;

import com.sumologic.client.model.LogMessage;
import zipkin.Codec;
import zipkin.Span;

import java.util.ArrayList;
import java.util.List;

public class APIResponseParser {

  public static List<Span> parseAPIResponse(List<LogMessage> apiResponseList) {
    List<Span> spanList = new ArrayList<>();
    for (LogMessage apiResponse : apiResponseList) {
      Span parsedSpan = parseAPIResponse(apiResponse.toString());
      if (parsedSpan != null) {
        spanList.add(parsedSpan);
      }
    }
    return spanList;
  }

  public static String filterResponseIfRequired(String apiResponse) {

    // Pretty basic filtering for now
    // can be revisited later if required and this seems too trivial/incompetent
    if (apiResponse.contains("logger=tracing.zipkin.ZipkinReporter")) {
      return apiResponse;
    }
    return null;
  }

  public static String parseResponseStr(String apiResponse) {
    int ibegin = apiResponse.indexOf("{");
    int iend = apiResponse.lastIndexOf("}");
    return apiResponse.substring(ibegin, iend + 1);
  }

  /* Private methods */

  private static Span parseAPIResponse(String apiResponse) {
    if (apiResponse == null || apiResponse.isEmpty()) {
      return null;
    }
    // filter somehow
    String filteredResponse = filterResponseIfRequired(apiResponse);
    if (filteredResponse == null) {
      return null;
    }

    String substring = parseResponseStr(apiResponse);
    Span span = Codec.JSON.readSpan(substring.getBytes());

    return span;
  }
}
