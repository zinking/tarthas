package arthas;

import com.taobao.arthas.core.server.ArthasBootstrap;
import java.lang.instrument.Instrumentation;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.ByteBuddyAgent;

@Slf4j
public class ArthasStub {
  private static final String HOST = "127.0.0.1";
  private static final String PORT = "33333";

  private static final Instrumentation instrumentation = ByteBuddyAgent.install();
  private static final ArthasBootstrap arthas = getArthasBootstrap();

  private static ArthasBootstrap getArthasBootstrap() {
    String configs = String.format("ip=%s; httpPort=%s", HOST, PORT);
    try {
      return ArthasBootstrap.getInstance(instrumentation, configs);
    } catch (Throwable e) {
      log.error("ArthasBootstrap {}", e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }
}
