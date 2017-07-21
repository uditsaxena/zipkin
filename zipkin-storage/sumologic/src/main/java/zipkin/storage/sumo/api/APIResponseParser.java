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
