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
package zipkin.storage;

import org.junit.Test;
import zipkin.Codec;
import zipkin.Span;
import zipkin.internal.GroupByTraceId;
import zipkin.storage.sumo.SumoSpanStore;
import zipkin.storage.sumo.api.APIResponseParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static zipkin.internal.GroupByTraceId.TRACE_DESCENDING;
import static zipkin.internal.Util.UTF_8;

public class SumoSpanStoreTest {

  @Test
  public void getServiceNameTest() {
    SumoSpanStore sumoSpanStore = new SumoSpanStore(null, null, null);

    sumoSpanStore.getServiceNamesFromSpan(getCorrectSpan());
  }

  @Test
  public void assembleSpans() {
    List<Span> spanList = buildSpanList();
    List<List<Span>> result = new ArrayList<>();

    for (List<Span> next : GroupByTraceId.apply(spanList, true, true)) {
      result.add(next);
    }
    Collections.sort(result, TRACE_DESCENDING);
    System.out.println(new String(Codec.JSON.writeTraces(result), UTF_8));
  }

  private List<Span> buildSpanList() {
    List<Span> spanList = new ArrayList<>();
    List<String> stringList = getDummyCorrectResponseList();
    for (String string : stringList) {
      String parsedResponseStr = APIResponseParser.parseResponseStr(string);
      Span span = Codec.JSON.readSpan(parsedResponseStr.getBytes());
      spanList.add(span);
    }
    return spanList;
  }

  private List<String> getDummyCorrectResponseList() {
    List<String> apiResponseList = new ArrayList<>();

    String s1 = "017-07-12 13:01:31,714 -0700 INFO  [hostId=udit-frontend-1] [module=SERVICE] [localUserName=service] [logger=tracing.zipkin.ZipkinReporter] [thread=qtp718231523-301] [auth=User:usaxena@demo.com:000000000000076A:000000000000009D:false:DefaultSumoSystemUser:-1:USERNAME_PASSWORD] [remote_ip=38.99.50.98] [web_session=4ablvcyf...] [api_session=dDlN8xag...] [remotemodule=service] [execution_interface=UI] {\"traceId\":\"ebb8de53f57e3f67\",\"id\":\"ebb8de53f57e3f67\",\"name\":\"com.sumologic.service.endpoint.ireport.v1.impl.interactivereportservicedelegate.getreport\",\"timestamp\":1499889691586124,\"duration\":127264,\"binaryAnnotations\":[{\"key\":\"lc\",\"value\":\"\",\"endpoint\":{\"serviceName\":\"search\",\"ipv4\":\"172.31.26.159\"}}]}";
    String s2 = "2017-07-12 13:01:31,713 -0700 INFO  [hostId=udit-frontend-1] [module=SERVICE] [localUserName=service] [logger=tracing.zipkin.ZipkinReporter] [thread=qtp718231523-301] [auth=User:usaxena@demo.com:000000000000076A:000000000000009D:false:DefaultSumoSystemUser:-1:USERNAME_PASSWORD] [remote_ip=38.99.50.98] [web_session=4ablvcyf...] [api_session=dDlN8xag...] [remotemodule=service] [execution_interface=UI] {\"traceId\":\"ebb8de53f57e3f67\",\"id\":\"109806871ccd6357\",\"name\":\"com.sumologic.service.endpoint.ireport.v1.impl.interactivereportservicedelegate.getreportinternal\",\"parentId\":\"ebb8de53f57e3f67\",\"timestamp\":1499889691586545,\"duration\":125398,\"binaryAnnotations\":[{\"key\":\"lc\",\"value\":\"\",\"endpoint\":{\"serviceName\":\"search\",\"ipv4\":\"172.31.26.159\"}}]}";
    String s3 = "2017-07-12 13:01:31,651 -0700 INFO  [hostId=udit-frontend-1] [module=SERVICE] [localUserName=service] [logger=tracing.zipkin.ZipkinReporter] [thread=qtp718231523-301] [auth=User:usaxena@demo.com:000000000000076A:000000000000009D:false:DefaultSumoSystemUser:-1:USERNAME_PASSWORD] [remote_ip=38.99.50.98] [web_session=4ablvcyf...] [api_session=dDlN8xag...] [remotemodule=service] [execution_interface=UI] {\"traceId\":\"ebb8de53f57e3f67\",\"id\":\"a97dd7d6770a56f7\",\"name\":\"com.sumologic.service.endpoint.ireport.v1.impl.interactivereportservicedelegate.fromapireport\",\"parentId\":\"109806871ccd6357\",\"timestamp\":1499889691609953,\"duration\":3610,\"binaryAnnotations\":[{\"key\":\"lc\",\"value\":\"\",\"endpoint\":{\"serviceName\":\"search\",\"ipv4\":\"172.31.26.159\"}}]}";

    apiResponseList.add(s1);
    apiResponseList.add(s2);
    apiResponseList.add(s3);

    return apiResponseList;
  }

  private String getDummyCorrectSingleResponse() {
    return "017-07-12 13:01:31,714 -0700 INFO  [hostId=udit-frontend-1] [module=SERVICE] [localUserName=service] [logger=tracing.zipkin.ZipkinReporter] [thread=qtp718231523-301] [auth=User:usaxena@demo.com:000000000000076A:000000000000009D:false:DefaultSumoSystemUser:-1:USERNAME_PASSWORD] [remote_ip=38.99.50.98] [web_session=4ablvcyf...] [api_session=dDlN8xag...] [remotemodule=service] [execution_interface=UI] {\"traceId\":\"ebb8de53f57e3f67\",\"id\":\"ebb8de53f57e3f67\",\"name\":\"com.sumologic.service.endpoint.ireport.v1.impl.interactivereportservicedelegate.getreport\",\"timestamp\":1499889691586124,\"duration\":127264,\"binaryAnnotations\":[{\"key\":\"lc\",\"value\":\"\",\"endpoint\":{\"serviceName\":\"search\",\"ipv4\":\"172.31.26.159\"}}]}";
  }

  private Span getCorrectSpan() {
    String filteredResponse = APIResponseParser.filterResponseIfRequired(getDummyCorrectSingleResponse());
    if (filteredResponse != null) {
      String parsedResponseStr = APIResponseParser.parseResponseStr(filteredResponse);
      Span span = Codec.JSON.readSpan(parsedResponseStr.getBytes());
      return span;
    }
    return null;
  }

}
