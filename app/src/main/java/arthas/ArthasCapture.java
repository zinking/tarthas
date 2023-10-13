package arthas;

import java.util.ArrayList;
import java.util.List;

public class ArthasCapture {
  public static List<Object> capturedObjs = new ArrayList<>();

  public static void capture(Object obj) {
    capturedObjs.add(obj);
  }
}
