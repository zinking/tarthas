package arthas;

import static com.taobao.arthas.core.shell.term.impl.http.api.ApiAction.ASYNC_EXEC;
import static com.taobao.arthas.core.shell.term.impl.http.api.ApiAction.CLOSE_SESSION;
import static com.taobao.arthas.core.shell.term.impl.http.api.ApiAction.EXEC;
import static com.taobao.arthas.core.shell.term.impl.http.api.ApiAction.INIT_SESSION;
import static com.taobao.arthas.core.shell.term.impl.http.api.ApiAction.INTERRUPT_JOB;
import static com.taobao.arthas.core.shell.term.impl.http.api.ApiAction.PULL_RESULTS;
import static com.taobao.arthas.core.shell.term.impl.http.api.ApiState.SUCCEEDED;

import com.alibaba.fastjson2.JSON;
import com.taobao.arthas.core.shell.term.impl.http.api.ApiRequest;
import com.taobao.arthas.core.shell.term.impl.http.api.ApiResponse;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

@Slf4j
public class ArthasSession {
  private static final String HOST = "127.0.0.1";
  private static final String PORT = "33333";

  private final String sessionId;
  private final String consumerId;

  private ArthasSession(String sessionId, String consumerId) {
    this.sessionId = sessionId;
    this.consumerId = consumerId;
  }

  public static void initStub() {
    try {
      ArthasClassLoader classLoader = new ArthasClassLoader();
      Class clazz = classLoader.loadClass("arthas.ArthasStub");
      clazz.newInstance();
      log.info("initStub {}", clazz.getClassLoader());
    } catch (Exception e) {
      log.error("initStub error {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public static ArthasSession getSession() {
    ApiRequest initRequest = new ApiRequest();
    initRequest.setAction(INIT_SESSION.name());
    ApiResponse response = makeApiRequest(initRequest);
    if (response.getState() == SUCCEEDED) {
      final String sessionId = response.getSessionId();
      final String consumerId = response.getConsumerId();
      ArthasSession session = new ArthasSession(sessionId, consumerId);
      session.pullResults();
      return session;
    } else {
      throw new RuntimeException("StartSession faile:" + response.getMessage());
    }
  }

  public void close() {
    ApiRequest request = getRequest();
    request.setAction(CLOSE_SESSION.name());
    ApiResponse response = makeApiRequest(request);
    if (response.getState() == SUCCEEDED) {
      log.info("session {} closed", sessionId);
    } else {
      log.error("session {} close failed", sessionId);
    }
  }

  private ApiRequest getRequest() {
    ApiRequest request = new ApiRequest();
    request.setSessionId(sessionId);
    request.setConsumerId(consumerId);
    return request;
  }

  public void pullResults() {
    ApiRequest request = getRequest();
    request.setAction(PULL_RESULTS.name());
    ApiResponse response = makeApiRequest(request);
    if (response.getState() == SUCCEEDED) {
      Map<String, Object> result = (Map) response.getBody();
      System.out.println("\n\n\npulled result:");
      for (Map.Entry<String, Object> kv : result.entrySet()) {
        System.out.printf("%s=%s%n", kv.getKey(), kv.getValue());
      }
      System.out.println("\n\n\n");
    } else {
      System.out.println("pull request failed: " + response.getMessage());
    }
  }

  public void makeAsyncCommand(String command) {
    ApiRequest request = getRequest();
    request.setAction(ASYNC_EXEC.name());
    request.setCommand(command);
    ApiResponse response = makeApiRequest(request);
    if (response.getState() == SUCCEEDED) {
      Map<String, Object> result = (Map) response.getBody();
      System.out.println("made cmd:" + command);
      System.out.println("cmd result:");
      for (Map.Entry<String, Object> kv : result.entrySet()) {
        System.out.printf("%s=%s%n", kv.getKey(), kv.getValue());
      }
    } else {
      System.out.println("pull request failed: " + response.getMessage());
    }
  }

  public void makeCommand(String command) {
    ApiRequest request = getRequest();
    request.setAction(EXEC.name());
    request.setCommand(command);
    ApiResponse response = makeApiRequest(request);
    if (response.getState() == SUCCEEDED) {
      Map<String, Object> result = (Map) response.getBody();
      System.out.println("made cmd:" + command);
      System.out.println("cmd result:");
      for (Map.Entry<String, Object> kv : result.entrySet()) {
        System.out.printf("%s=%s%n", kv.getKey(), kv.getValue());
      }
    } else {
      System.out.println("pull request failed: " + response.getMessage());
    }
  }

  public void interrupt() {
    ApiRequest request = getRequest();
    request.setAction(INTERRUPT_JOB.name());
    ApiResponse response = makeApiRequest(request);
    if (response.getState() == SUCCEEDED) {
      System.out.println("interrupted");
    } else {
      System.out.println("interrupt error: " + response.getMessage());
    }
  }

  public static ApiResponse makeApiRequest(ApiRequest request) {
    final String uri = String.format("http://%s:%s/api", HOST, PORT);
    HttpPost post = new HttpPost(uri);
    try {
      String payload = JSON.toJSONString(request);
      post.setEntity(new StringEntity(payload));

      try (CloseableHttpClient httpClient = HttpClients.createDefault();
          CloseableHttpResponse response = httpClient.execute(post)) {

        String resultData = EntityUtils.toString(response.getEntity());
        return JSON.parseObject(resultData, ApiResponse.class);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
  }
}
