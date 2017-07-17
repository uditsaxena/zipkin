package zipkin.storage.api;

import org.junit.Test;
import zipkin.Codec;
import zipkin.Span;
import zipkin.storage.sumo.api.APIResponseParser;

import java.util.ArrayList;
import java.util.List;

public class APIResponseParserTest {

  @Test
  public void parseAPIResponseListTest() {
    List<String> apiResponseList = getDummyCorrectResponseList();
//    APIResponseParser.parseAPIResponse(apiResponseList);
  }

  @Test
  public void parseAPIResponseTest() {
    String apiResponse = getDummyCorrectSingleResponse();
//    Span span = APIResponseParser.parseAPIResponse(apiResponse);
//    assert getCorrectSpan().equals(span);
  }

  @Test
  public void filterAPIResponseTest() throws Exception {
    String correctResponse = "017-07-12 13:01:31,714 -0700 INFO  [hostId=udit-frontend-1] [module=SERVICE] [localUserName=service] [logger=tracing.zipkin.ZipkinReporter] [thread=qtp718231523-301] [auth=User:usaxena@demo.com:000000000000076A:000000000000009D:false:DefaultSumoSystemUser:-1:USERNAME_PASSWORD] [remote_ip=38.99.50.98] [web_session=4ablvcyf...] [api_session=dDlN8xag...] [remotemodule=service] [execution_interface=UI] {\"traceId\":\"ebb8de53f57e3f67\",\"id\":\"ebb8de53f57e3f67\",\"name\":\"com.sumologic.service.endpoint.ireport.v1.impl.interactivereportservicedelegate.getreport\",\"timestamp\":1499889691586124,\"duration\":127264,\"binaryAnnotations\":[{\"key\":\"lc\",\"value\":\"\",\"endpoint\":{\"serviceName\":\"search\",\"ipv4\":\"172.31.26.159\"}}]}";
    String incorrectResponse = "017-07-12 13:01:31,714 -0700 INFO  [hostId=udit-frontend-1] [module=SERVICE] [localUserName=service] [thread=qtp718231523-301] [auth=User:usaxena@demo.com:000000000000076A:000000000000009D:false:DefaultSumoSystemUser:-1:USERNAME_PASSWORD] [remote_ip=38.99.50.98] [web_session=4ablvcyf...] [api_session=dDlN8xag...] [remotemodule=service] [execution_interface=UI] \"traceId\":\"ebb8de53f57e3f67\",\"id\":\"ebb8de53f57e3f67\",\"name\":\"com.sumologic.service.endpoint.ireport.v1.impl.interactivereportservicedelegate.getreport\",\"timestamp\":1499889691586124,\"duration\":127264,\"binaryAnnotations\":[{\"key\":\"lc\",\"value\":\"\",\"endpoint\":{\"serviceName\":\"search\",\"ipv4\":\"172.31.26.159\"";

    String filteredCorrectResponse = APIResponseParser.filterResponseIfRequired(correctResponse);
    assert filteredCorrectResponse.equalsIgnoreCase(correctResponse);

    String filteredIncorrectResponse = APIResponseParser.filterResponseIfRequired(incorrectResponse);
    assert filteredIncorrectResponse == null;
  }


  /*
  Private Methods
  */

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
